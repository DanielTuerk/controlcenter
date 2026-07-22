ControlCenter
=============

Software to control a model railroad construction with selectrix.

For more information feel free to contact me, the current state is NOT ready to use. 

###Supported Format: SX1

Build native executable of control-center:

```
mvn clean package -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true -Pwith-frontend
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package -Pwith-frontend
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

## License

This project is licensed under the GNU Affero General Public License v3.0 (AGPL-3.0) — see the [LICENSE](LICENSE) file for details.

Copyright (C) 2026 Daniel Tuerk
