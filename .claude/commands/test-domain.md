---
description: Run backend and frontend tests for a single domain
argument-hint: <domain> (e.g. train, track, scenario)
---

Run the tests scoped to the `$ARGUMENTS` domain on both sides.

1. **Backend**: from the project root, run the matching JUnit tests via `./mvnw test -Dtest='*$ARGUMENTS*'` (case-insensitive match against class names like `TrainResourceTest`, `TrainServiceTest`). If unsure which classes exist, list `src/test/java/**` first and pick the ones for this domain.
2. **Frontend**: from `frontend/`, run the matching specs. Locate `*.spec.ts` under `frontend/src/app/**/$ARGUMENTS*/` and run `npm test` (note `ng test` defaults to watch/Karma — pass the appropriate single-run flags if needed).
3. Summarize pass/fail for each side. On failures, show the relevant output; do not auto-fix unless asked.
