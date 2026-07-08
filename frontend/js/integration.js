// integration.js
// Analytics capture ONLY -> ClickHouse (via ClickHouse-JS HTTP interface,
// using the restricted insert-only user - see clickhouse-prep/).
//
// Registration Service is NOT called from the frontend - per the module
// FAQ, frontend/backend integration isn't required, and it's demonstrated
// via Postman instead, same as Event/Program Service. The register_click
// event below is still a real, useful metric on its own: comparing its
// count against Registration Service's own recorded registrations
// (checked separately via Postman/DB query, not via this file) gives the
// click-to-completion funnel/drop-off rate without needing a live
// frontend-to-backend call.
const CLICKHOUSE_URL = "http://localhost:8123";
const CLICKHOUSE_USER = "analytics_writer";
const CLICKHOUSE_PASSWORD = "CHANGE_ME_BEFORE_RUNNING"; // matches clickhouse-prep/restricted-user.sql

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

// 4) Register button click - the only half of the funnel captured
// client-side. Not wired to the Registration Service API (see note above).
// preventDefault() stops the page navigating to nowhere (the template's
// original action="#"), then shows a neutral acknowledgment - honest
// about this being a tracked click, not a real submission.
const registrationForm = document.querySelector("#registrationForm");
const statusDiv = document.querySelector("#registrationStatus");

if (registrationForm) {
  registrationForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const eventId = registrationForm.dataset.eventId || "1";
    const ticketCount = parseInt(document.querySelector("#ticketCount").value, 10) || 1;

    trackEvent("register_click", eventId, ticketCount);

    if (statusDiv) {
      statusDiv.textContent = "Thanks for your interest! Registration is processed via our API - see Postman collection for a live demo.";
      statusDiv.style.color = "green";
    }
  });
}

