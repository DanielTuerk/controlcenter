---
description: Create a new Flyway migration for a schema change
argument-hint: <short-description-of-change>
---

Create a new Flyway migration in `src/main/resources/db/migration/`.

Context: Hibernate runs in `validate` mode, so ANY entity field add/change/remove requires a matching migration or the app fails to start.

Steps:
1. List the existing migrations to find the latest version number (files are named `V1.0.x__name.sql`). Pick the next patch version.
2. Create `V1.0.<next>__$ARGUMENTS.sql` (lower-kebab the description for the filename suffix).
3. Write the SQL DDL/DML for the change. Match the existing style: uppercase keywords, `PUBLIC.` schema prefix, one statement per logical change (see `V1.0.2__config-scenario-defaults.sql` for the INSERT style and `V1.0.0__controlcenter.sql` for DDL style).
4. If this migration backs an entity change, confirm the entity field names/types line up with the columns so Hibernate validation passes.
5. Remind the user the migration applies at startup (`quarkus.flyway.migrate-at-start=true`) and that the existing H2 file in `data/` may need to be reset if it predates the change.
