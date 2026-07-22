---
description: Scaffold a new live event across backend broadcast and frontend subscription
argument-hint: <EventName> (e.g. SignalStateEvent)
---

Add a new live (websocket) event named `$ARGUMENTS`, wiring all coordinated touchpoints. Study the `train` domain's events as the reference (`shared/train/TrainDataChangedEvent`, `TrainManager.fireEvent`, `frontend/.../websocket/train.subscription.ts`).

Backend:
1. Create `shared/<domain>/$ARGUMENTS.java` implementing `Event` (or extending `AbstractItemEvent` if it carries an `itemId` for a CRUD-style change — that base gives you the `ACTION_TYPE {CREATE, UPDATE, DELETE}` enum and `itemId`). Annotate with `@Schema(...)` and `@Tag(ref = "websocket")` like the existing events so it appears in the OpenAPI spec.
2. Fire it from the owning service/manager:
   - `eventBroadcaster.fireEvent(event)` to broadcast to all clients, or `fireTrackingEvent(event, connectionIds)` to target specific clients.
   - For CRUD changes, follow the `TrainManager.fireEvent(id, ACTION_TYPE)` pattern (fires both the CDI event and the broadcast).
   - If the event should be replayed to clients that connect later, make it a `StateEvent` so `EventCache` retains it.

Frontend:
3. Run `/regen-api` (backend must be running) so `$ARGUMENTS` appears in `frontend/src/shared/openapi-gen`.
4. Add an accessor in the matching `frontend/src/app/shared/websocket/<domain>.subscription.ts`:
   `readonly <camelName> = this.createEventAccessor<$ARGUMENTS>('$ARGUMENTS');`
   The string key MUST equal the event's Java class simple name — that is how `WebSocketService` routes the message (wire format is `ClassSimpleName: <json>`).

Remind the user that the class simple name is the contract between backend broadcast and frontend subscription — renaming the class breaks routing on both sides.
