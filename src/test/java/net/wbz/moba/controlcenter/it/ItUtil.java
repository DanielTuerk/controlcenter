package net.wbz.moba.controlcenter.it;

import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.shared.bus.RailVoltageEvent;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ItUtil {

    public static int setCurrentConstruction() {
        var constructionId = given()
            .when().get("/api/constructions")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getInt("[0].routeSequenceId");

        setCurrentConstruction(constructionId);
        return constructionId;
    }

    public static void setCurrentConstruction(int constructionId) {
        given()
            .contentType(ContentType.TEXT)
            .body(constructionId)
            .when()
            .post("/api/current-construction")
            .then()
            .statusCode(200)
            .body("id", equalTo(constructionId));
    }

    public static void connectTestDevice() {
        disconnectTestDevice(false);
        var deviceId = given()
            .when().get("/api/devices")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("find { it.key == 'TEST' }.id");

        given()
            .contentType(ContentType.JSON)
            .pathParam("id", deviceId)
            .when()
            .post("/api/devices/{id}/connect")
            .then()
            .statusCode(200);
    }

    public static void disconnectTestDevice() {
        disconnectTestDevice(true);
    }

    public static void disconnectTestDevice(boolean verify) {
        final var then = given()
            .contentType(ContentType.JSON)
            .when()
            .post("/api/devices/disconnect")
            .then();
        if (verify) {
            then.statusCode(200);
        }
    }

    public static boolean fetchRailVoltage() {
        return given()
            .when().get("/api/bus/railvoltage")
            .then()
            .statusCode(200)
            .extract().body().as(Boolean.class);
    }

    public static void toggleRailvoltage() {
        given()
            .contentType(ContentType.JSON)
            .when().post("/api/bus/railvoltage")
            .then()
            .statusCode(200);
    }

    public static void enableRailvoltage(WebSocketEventReceiver eventReceiver) {
        if (!fetchRailVoltage()) {
            toggleRailvoltage();
            // the initial one
            eventReceiver.verifyReceivedEvent(RailVoltageEvent.class, "state");
//            eventReceiver.verifyReceivedEvent(RailVoltageEvent.class, "state");
            final var railVoltageEvent = eventReceiver.catchEvent(RailVoltageEvent.class);
            if (!railVoltageEvent.isState()) {
                throw new RuntimeException("Rail voltage not enabled after toggle");
            }
//            try {
//                Thread.sleep(3000L);
//
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            eventReceiver.reset();
        }
    }

    public static void sendBusData(int bus, int address, int value) {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                "bus":%d,
                "address":%d,
                "value":%d
                }
                """.formatted(bus, address, value))
            .when().post("/api/bus/bus-data")
            .then()
            .statusCode(200);
    }


    public static int fetchBusData(int bus, int address) {
        return given()
            .contentType(ContentType.JSON)
            .when().get("/api/bus/bus-address-data?bus={bus}&address={address}", bus, address)
            .then()
            .statusCode(200).extract().body().as(Integer.class);
    }
}
