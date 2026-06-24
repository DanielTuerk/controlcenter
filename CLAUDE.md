# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# Project Overview

**ControlCenter** is a model railroad control system for selectrix-based (SX1) layouts. Java (Quarkus 3) backend + Angular 22 frontend. Features: bus monitoring/recording/playback, track editing, train control, scenario/route execution, and station configuration. Hardware access goes through the external `selectrix4java` library (must be installed in the local Maven repo — see README).

## Tech Stack
- **Backend**: Quarkus 3.36.2, Java 21, Hibernate ORM Panache, RESTEasy (quarkus-rest + Jackson), SmallRye OpenAPI, Quartz scheduler, WebSockets-Next, H2 (file-based) + Flyway, MapStruct + Lombok (annotation processors)
- **Frontend**: Angular 22.0.2, Angular Material 22, RxJS; tests via Vitest (config) and Karma/Jasmine
- **Integration**: OpenAPI — backend exposes spec at `/q/openapi`; frontend generates a typed Angular client from it

## Key Commands

### Backend (Maven — use `./mvnw`)
```bash
./mvnw quarkus:dev          # dev mode w/ live reload, port 8080, Dev UI at /q/dev/
./mvnw package              # build (target/quarkus-app/)
./mvnw test                 # all tests
./mvnw test -Dtest=TrainServiceTest          # single test class
./mvnw test -Dtest=TrainServiceTest#myMethod # single test method
# Native build (frontend bundled):
./mvnw clean package -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true -Pwith-frontend
```
Note: integration tests (`*IT`) are skipped by default (`skipITs=true`).

### Frontend (in `frontend/`)
```bash
npm run start:dev   # ng serve with proxyconfig.json (proxies /api -> :8080)
npm run build       # production build -> frontend/dist/essentials-practice/browser
npm test            # ng test
npm run watch       # dev build, watch mode
```

### Regenerating the API client (backend must be running)
```bash
npm run generate:api-and-fetch   # curls /q/openapi, then regenerates src/shared/openapi-gen
```
Run this after changing any REST resource or shared DTO. The generated client in `frontend/src/shared/openapi-gen/` is committed and consumed throughout the frontend — never hand-edit it.

## Architecture

### Backend layering (`src/main/java/net/wbz/moba/controlcenter/`)
Each domain (train, track, scenario, construction, config, bus/device) follows the same vertical slice:

- **`api/<domain>/`** — JAX-RS `*Resource` classes (`@Path("/api/...")`, `@Blocking`) plus request `*Dto` records. Resources are thin: validate, delegate, map HTTP status. They depend on `Manager` (CRUD/data) and `Service` (hardware/runtime behavior).
- **`service/<domain>/`** — business logic, split into roles:
  - `*Manager` — orchestrates CRUD, fires change events. Public entry point for data mutations.
  - `*DataProvider` — DB access wrapper; owns the Quarkus `@CacheResult`/`@CacheInvalidateAll` caching and `@Transactional` boundaries. Managers call providers, not repositories directly.
  - `*Mapper` — MapStruct interfaces mapping entity ↔ shared model.
  - `*Service` — runtime/hardware behavior (e.g. `TrainService` talks to `selectrix4java` `TrainModule`s, registers device listeners, rebroadcasts hardware changes as events).
- **`persist/entity/`** — Panache entities (`extends AbstractEntity`, public fields). **`persist/repository/`** — `PanacheRepository` implementations.
- **`shared/<domain>/`** — plain model classes and `*Event` types serialized to the frontend (the "DTO over websocket" surface).

Layer flow: `Resource → Manager → DataProvider → Repository → Entity`, with `Mapper` converting entity→shared model and `Service` handling hardware + live events.

### Schema is migration-managed, NOT generated
Hibernate runs in `validate` mode (`quarkus.hibernate-orm.schema-management.strategy=validate`). The schema comes from **Flyway** migrations in `src/main/resources/db/migration/` (`V1.0.x__*.sql`), applied at startup. Adding/changing an entity field means writing a new versioned migration — Hibernate will fail validation otherwise. The H2 DB file lives in `data/` (gitignored alongside backups).

### Events & WebSocket (the live-update backbone)
- `EventBroadcaster` (`@WebSocket(path = "/websocket")`) is the single hub. On client connect it assigns a `clientId` and replays cached `StateEvent`s (via `EventCache`) so late joiners catch up.
- `fireEvent(event)` broadcasts to all clients; `fireTrackingEvent(event, connectionIds)` targets specific clients (used for per-client bus tracking).
- Two event mechanisms coexist: CDI events (`@Observes`, in-process — e.g. `TrainService.onTrainDataChanged`) and websocket broadcasts (to the browser). Managers often fire **both** for the same change.
- Frontend: `WebSocketService` connects to `ws://localhost:8080/websocket`, auto-reconnects, and dispatches messages by event name (the message format is `EventClassSimpleName: <json>`) into per-key `ReplaySubject`s. The `frontend/src/app/shared/websocket/*.subscription.ts` files subscribe to specific event types.

### Frontend structure (`frontend/src/app/`)
- `control-center/` — feature modules: `bus-monitor`, `editor` (track editor), `track`, `train`, `station`, `scenario`, `setting`, plus `viewer`, `header`, `footer`, `common`.
- `shared/` — singleton services (`*.service.ts`, registered in `app.config.ts`) that wrap the generated OpenAPI client and/or post to `/api/...`, surfacing errors via `SnackBar`. `shared/websocket/` holds the live-event subscriptions.
- `shared/openapi-gen/` — generated client (do not edit).
- Routing: `app.routes.ts` → `control-center.routes.ts`. Standalone components + `provideHttpClient`; API base path comes from `src/env/local.env.ts`.

## Conventions
- Backend mutations fire change events through the `Manager` — when adding CRUD, follow the `fireEvent(id, ACTION_TYPE)` pattern so the frontend stays in sync.
- Cached reads (`@CacheResult`) must be paired with `@CacheInvalidateAll` on every mutating method in the same provider, or the frontend will read stale data.
- `old-sources-non-migrated/` is legacy pre-Quarkus code kept for reference during migration — not built, don't modify.
