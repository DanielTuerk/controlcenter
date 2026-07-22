---
description: Back up and reset the local H2 database so Flyway re-runs migrations from scratch
---

Reset the local H2 database. Use this when an old `data/` DB file fails to apply a new migration cleanly, or Hibernate validation fails against a stale schema.

This is destructive to local data — confirm with the user before deleting anything.

Steps:
1. Stop the backend if it is running (the H2 file uses `DB_CLOSE_DELAY=-1`, so the file can stay locked by a live process).
2. Back up the current DB: copy `data/controlcenter-db.mv.db` (and `.trace.db` if present) to a timestamped name, matching the existing backup convention in `data/` (e.g. `controlcenter-db-bak-<dd-MM-yy>.mv.db`). Do NOT commit these — `data/` is gitignored.
3. Delete `data/controlcenter-db.mv.db` and `data/controlcenter-db.trace.db`.
4. Tell the user to restart the backend (`/run-backend`). On startup Flyway (`migrate-at-start=true`) recreates the schema from `src/main/resources/db/migration/`.

Report what was backed up and removed.
