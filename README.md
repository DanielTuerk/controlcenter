ControlCenter
=============

Software to control a model railroad construction with selectrix.

### Features
- track builder (blocks, switch, signal, uncoupler)
  - assign Block Occupancy Detector
- control trains
- bus monitor: live monitoring, recording and playback of the SX1 bus
- scenarios: define and run automated routes/sequences

### Supported Format: SX1

## Downloads

Native executables for Linux and Windows are built automatically and attached to each [GitHub Release](../../releases).

> **_NOTE:_** The Windows executable is not code-signed. Windows SmartScreen will show an
> "unknown publisher" warning ("Der Computer wurde durch Windows geschützt") on first launch.
> Click **More info → Run anyway** (or right-click the file → Properties → check
> "Unblock" before running) to start it anyway.


## Development


Build native executable of control-center:

```
mvn clean package -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true -Pwith-frontend
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.


### Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package -Pwith-frontend
```

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

## License

This project is licensed under the GNU Affero General Public License v3.0 (AGPL-3.0) — see the [LICENSE](LICENSE) file for details.

Copyright (C) 2026 Daniel Tuerk
