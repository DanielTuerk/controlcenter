---
description: Start the Quarkus backend in dev mode (live reload, port 8080)
---

Start the backend in Quarkus dev mode.

Run `./mvnw quarkus:dev` from the project root **in the background** (it is a long-running process). Dev mode gives live reload, serves on port 8080, and exposes the Dev UI at `/q/dev/` and the OpenAPI spec at `/q/openapi`.

After launching, wait until the log shows the app is listening on :8080, then report that it's up (and that `/regen-api` can now be run). If startup fails on a Hibernate schema validation error, that almost always means an entity change is missing a Flyway migration — point the user to `/new-migration`.
