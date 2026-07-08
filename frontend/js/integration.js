// integration.js
// Two responsibilities, kept in one file since both touch the same page:
//   1) Analytics capture -> ClickHouse (via ClickHouse-JS HTTP interface,
//      using the restricted insert-only user - see clickhouse-prep/)
//   2) Wiring the real Register form -> Registration Service API
//
// Update these two constants once the real endpoints exist (Day 12/13
// for ClickHouse, Day 10 for Registration Service) - everything else
// stays the same.
const CLICKHOUSE_URL = "http://localhost:8123";
const CLICKHOUSE_USER = "analytics_writer";
const CLICKHOUSE_PASSWORD = "CHANGE_ME_BEFORE_RUNNING"; // matches clickhouse-prep/restricted-user.sql

const REGISTRATION_SERVICE_URL = "http://localhost:8083"; // -> real cluster address on Day 10

// ---------- Analytics (anonymous - no name/email ever sent here) ----------

function getSessionId() {
  let sid = sessionStorage.getItem("session_id");
  if (!sid) {
    sid = crypto.randomUUID();
    sessionStorage.setItem("session_id", sid);
  }
  return sid;
}

async function trackEvent(eventType, eventRef, ticketCount) {
  const row = {
    session_id: getSessionId(),
    event_type: eventType,
    page_url: window.location.pathname,
    event_ref: eventRef || "",
    ticket_count: ticketCount || 0,
    event_time: new Date().toISOString().slice(0, 19).replace("T", " "),
  };

  const body = `INSERT INTO analytics.web_events FORMAT JSONEachRow\n${JSON.stringify(row)}`;

  try {
    await fetch(
      `${CLICKHOUSE_URL}/?user=${CLICKHOUSE_USER}&password=${CLICKHOUSE_PASSWORD}`,
      { method: "POST", body }
    );
  } catch (err) {
    // Analytics failures should never break the actual page.
    console.warn("Analytics event failed to send", err);
  }
}

// 1) Page view / session
window.addEventListener("load", () => {
  trackEvent("page_view", window.location.pathname, 0);
});

// 2) Video section viewed (simplified from "play %" - the real template
// embeds YouTube via a plain iframe, which doesn't expose native play
// events without the YouTube IFrame Player API. Visibility-based tracking
// is a conscious simplification, using the same technique as metric #3.)
const videoSection = document.querySelector("#video");
if (videoSection) {
  let videoSeen = false;
  const videoObserver = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting && !videoSeen) {
      videoSeen = true;
      trackEvent("video_section_viewed", "video", 0);
    }
  });
  videoObserver.observe(videoSection);
}

// 3) Scroll depth on the Programs section (real id is "#program", singular)
const programSection = document.querySelector("#program");
if (programSection) {
  let programsSeen = false;
  const programsObserver = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting && !programsSeen) {
      programsSeen = true;
      trackEvent("scroll_depth_programs", "program", 0);
    }
  });
  programsObserver.observe(programSection);
}

// 4) Register button click -> completed registration funnel.
// register_click fires on submit attempt; register_complete fires only
// after Registration Service actually confirms the booking (see below) -
// that gap is the funnel/drop-off signal.

// ---------- Registration Service wiring (the one real integration) ----------

const registrationForm = document.querySelector("#registrationForm");
const statusDiv = document.querySelector("#registrationStatus");

if (registrationForm) {
  registrationForm.addEventListener("submit", async (e) => {
    e.preventDefault(); // stop the default page-reload submit

    const eventId = parseInt(registrationForm.dataset.eventId, 10);
    const firstName = document.querySelector("#firstname").value.trim();
    const lastName = document.querySelector("#lastname").value.trim();
    const email = document.querySelector("#email").value.trim();
    const ticketCount = parseInt(document.querySelector("#ticketCount").value, 10) || 1;

    trackEvent("register_click", String(eventId), ticketCount);

    if (!firstName || !email) {
      statusDiv.textContent = "Please fill in your name and email.";
      statusDiv.style.color = "red";
      return;
    }

    statusDiv.textContent = "Submitting...";
    statusDiv.style.color = "black";

    try {
      const response = await fetch(`${REGISTRATION_SERVICE_URL}/api/registrations`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          eventId: eventId,
          name: `${firstName} ${lastName}`.trim(),
          email: email,
          ticketCount: ticketCount,
        }),
      });

      if (response.ok) {
        statusDiv.textContent = "Registration successful! Check your email for confirmation.";
        statusDiv.style.color = "green";
        trackEvent("register_complete", String(eventId), ticketCount);
        registrationForm.reset();
      } else {
        const errorBody = await response.json().catch(() => ({}));
        statusDiv.textContent = errorBody.message || "Registration failed - please try again.";
        statusDiv.style.color = "red";
      }
    } catch (err) {
      statusDiv.textContent = "Could not reach the registration service.";
      statusDiv.style.color = "red";
      console.warn("Registration request failed", err);
    }
  });
}
