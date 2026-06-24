---
description: Regenerate the frontend OpenAPI client from the running backend
---

Regenerate the typed Angular API client from the backend's OpenAPI spec.

Steps:
1. Confirm the backend is running on port 8080 (the generator curls `http://localhost:8080/q/openapi`). If it is not reachable, tell the user to start it with `./mvnw quarkus:dev` and stop.
2. From the `frontend/` directory, run `npm run generate:api-and-fetch`.
3. Report which generated files under `frontend/src/shared/openapi-gen/` changed (via `git status`).
4. If the regeneration changed model/DTO shapes, scan the frontend `shared/*.service.ts` and feature components for now-broken usages and list them — do NOT auto-fix unless asked.

Never hand-edit files in `frontend/src/shared/openapi-gen/` — they are generated and committed.
