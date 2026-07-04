# New Event Cloud Platform — CMM707 Coursework

Microservices architecture for the "New Event" frontend template, deployed on
a Kubernetes cluster, with analytics, observability, and a blue-green CI/CD
pipeline.

## Architecture

- **event-service** — Event details (ID, title, venue, date/time, price, capacity, seats)
- **program-service** — Agenda data (day, track, session, speaker, times)
- **registration-service** — Attendee registrations; triggers a Lambda when
  seats drop below threshold
- **frontend** — Static "New Event" template, served via Nginx, instrumented
  with ClickHouse-JS for analytics
- **lambda/** — Writes a notification object to S3 when triggered
- **k8s/** — Kubernetes manifests for every component (Postgres, each
  service, frontend, ClickHouse, Metabase, observability stack)
- **.github/workflows/** — CI/CD pipeline (build/push images, blue-green
  deploy)

## Local-first workflow

Per the module's own guidance: build and test each service locally with
`docker-compose` before deploying anything to the cloud cluster. Faster
iteration, zero cloud cost while developing.

```bash
docker-compose up -d postgres
# build/test a service locally, then uncomment it in docker-compose.yml
```

Once a service works locally, containerize it, write/refine its k8s
manifest (Kompose can bootstrap a starting point from docker-compose.yml),
and deploy to the cluster.

## Cloud infrastructure

- **Compute:** k3s on a single AWS EC2 instance (t3.large)
- **Database:** self-hosted PostgreSQL in-cluster (PVC-backed)
- **Serverless:** AWS Lambda, writes to S3
- **Analytics:** ClickHouse (Helm chart), ClickHouse-JS client called
  directly from the frontend
- **Dashboard:** Metabase
- **Observability:** kube-prometheus-stack (Prometheus + Grafana)
- **CI/CD:** GitHub Actions, blue-green deploy via two Deployments + a
  Service selector swap

See the full build plan (CMM707_Plan.md) for the day-by-day schedule,
requirement compliance mapping, and cost breakdown.

## Status

- [ ] Day 1-2: AWS setup, EC2 + k3s cluster
- [ ] Day 3: Postgres in-cluster
- [ ] Day 4-7: Event / Program / Registration services
- [ ] Day 8: Lambda + S3 integration
- [ ] Day 9: Frontend deployment
- [ ] Day 10-12: ClickHouse + analytics
- [ ] Day 13: Metabase dashboards
- [ ] Day 14: Observability
- [ ] Day 15-16: CI/CD + blue-green
- [ ] Day 17-19: Report + submission
