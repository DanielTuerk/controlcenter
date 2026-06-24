---
description: Scaffold a new backend domain following the project's vertical-slice layering
argument-hint: <DomainName>
---

Scaffold a new backend domain named `$ARGUMENTS`, following the established vertical-slice pattern used by `train`, `track`, `scenario`, etc. Study an existing slice (e.g. the `train` domain) first and mirror its structure and conventions exactly.

Create, under `src/main/java/net/wbz/moba/controlcenter/`:

1. **`persist/entity/$ARGUMENTSEntity.java`** — Panache entity `extends AbstractEntity`, public fields (no getters/setters), JPA annotations.
2. **`persist/repository/$ARGUMENTSRepository.java`** — `@ApplicationScoped` implementing `PanacheRepository<$ARGUMENTSEntity>`.
3. **`shared/<domain>/$ARGUMENTS.java`** — plain shared model returned to the frontend, plus a `$ARGUMENTSDataChangedEvent` extending the project's item-event base if the domain needs live updates.
4. **`service/<domain>/$ARGUMENTSMapper.java`** — MapStruct `@Mapper(componentModel = "jakarta-cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)` mapping entity → shared model.
5. **`service/<domain>/$ARGUMENTSDataProvider.java`** — DB access wrapper owning `@CacheResult`/`@CacheInvalidateAll` (give it a unique cache name) and `@Transactional` on mutating methods.
6. **`service/<domain>/$ARGUMENTSManager.java`** — CRUD entry point. Calls the DataProvider (never the repository directly) and fires a change event via `fireEvent(id, ACTION_TYPE)` on every mutation, mirroring `TrainManager`.
7. **`api/<domain>/$ARGUMENTSResource.java`** — `@Path("/api/...")`, `@Blocking`, `@Produces`/`@Consumes` JSON. Keep it thin: validate, delegate to Manager, map HTTP status. Add a `$ARGUMENTSDto` record for request bodies.

After scaffolding:
- Write a Flyway migration for the new table (see `/new-migration`) — do not rely on Hibernate to create it (validate mode).
- Remind the user to run `/regen-api` so the frontend client picks up the new endpoints.

Match existing formatting: 4-space indent for Java, `@author Daniel Tuerk` Javadoc header where neighbors have it.
