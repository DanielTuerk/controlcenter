# Communication Flow: UI → Backend → selectrix4java

Two simplified sequence diagrams showing how the Angular UI communicates
with the Quarkus backend, and how the backend talks to the hardware via
`selectrix4java`. The key point: REST requests almost always either fetch a
snapshot or submit a command (a bare acknowledgement, no new state in the
response body). The actual state is delivered exclusively and
asynchronously via a WebSocket event, once the hardware reports the change
back.

## 1. General pattern

```mermaid
sequenceDiagram
    participant UI as Angular UI
    participant BE as Backend (Quarkus REST)
    participant HW as selectrix4java (SX bus)

    Note over UI,BE: WebSocket connection is opened on app start<br/>and stays open for the session

    Note over UI,HW: Read access - just a snapshot
    UI->>BE: GET /api/... (e.g. /api/trains)
    BE-->>UI: 200 OK + current data (one-time snapshot)

    Note over UI,HW: Write access - just a command
    UI->>BE: POST/PUT /api/... (command, e.g. change a value)
    BE->>HW: send telegram (fire-and-forget)
    BE-->>UI: 200 OK (only acknowledges the command was accepted)

    Note over HW,UI: State change - async, decoupled from the request
    HW-->>BE: bus echo / module listener reports the new state
    BE-->>UI: WebSocket event (e.g. TrainDrivingLevelEvent)
    Note over UI: UI updates its state ONLY from events,<br/>never from the REST response
```

## 2. Example: changing a train's `drivingLevel`

Concrete flow for `POST /api/trains/{id}/level`
(`TrainResource` → `TrainService` → `selectrix4java` `TrainModule`).
The REST response only confirms the command was received; the value that
actually takes effect only arrives via `TrainDrivingLevelEvent`, fired by
the `TrainDataListener` that `TrainService` registers on the `TrainModule`
at startup.

```mermaid
sequenceDiagram
    participant UI as Angular UI
    participant Res as TrainResource
    participant Svc as TrainService
    participant SX as selectrix4java (TrainModule / bus)
    participant EB as EventBroadcaster

    UI->>Res: POST /api/trains/{id}/level (level in body)
    Res->>Svc: updateDrivingLevel(id, level)
    Svc->>Svc: validates 0 <= level <= 31
    Svc->>SX: trainModule.setDrivingLevel(level)
    SX->>SX: BusAddress.send() (telegram out, fire-and-forget)
    Res-->>UI: 200 OK (just an ack, no level in body)

    Note over SX: bus echo confirms the new value later, asynchronously
    SX-->>Svc: TrainDataListener.drivingLevelChanged(level)
    Svc->>EB: fireEvent(TrainDrivingLevelEvent(trainId, level))
    EB-->>UI: WebSocket push "TrainDrivingLevelEvent: {...}"
    Note over UI: the UI slider/display only updates here
```
