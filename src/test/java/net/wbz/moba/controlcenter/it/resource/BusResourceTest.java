package net.wbz.moba.controlcenter.it.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.it.ItUtil;
import net.wbz.moba.controlcenter.it.WebSocketEventReceiver;
import net.wbz.moba.controlcenter.shared.bus.BusDataEvent;
import net.wbz.moba.controlcenter.shared.bus.RailVoltageEvent;
import net.wbz.moba.controlcenter.shared.bus.SystemFormatEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BusResourceTest {

    private static final WebSocketEventReceiver EVENT_RECEIVER = new WebSocketEventReceiver();

    @BeforeAll
    public static void beforeAll() {
        RestAssured.port = 8081;
        ItUtil.connectTestDevice();
    }

    @Test
    @Order(2)
    void testFetchRailVoltage() {
        given()
            .when().get("/api/bus/railvoltage")
            .then()
            .statusCode(200)
            .body(notNullValue());
    }

    @Test
    @Order(3)
    void testToggleRailVoltageAndVerifyEvent() {
        given()
            .contentType(ContentType.JSON)
            .when().post("/api/bus/railvoltage")
            .then()
            .statusCode(200);

        // Verify a RailVoltageEvent was received (no need to assert payload content here)
        EVENT_RECEIVER.verifyReceivedEvent(RailVoltageEvent.class);
    }

    @Test
    @Order(4)
    void testFetchSystemFormat() {
        given()
            .when().get("/api/bus/system-format")
            .then()
            .statusCode(200)
            .body(notNullValue());
    }

    @Test
    @Order(5)
    void testSwitchSystemFormatAndVerifyEvent() {
        given()
            .contentType(ContentType.JSON)
            .when().post("/api/bus/system-format")
            .then()
            .statusCode(200);

        // Verify a SystemFormatEvent was received
        EVENT_RECEIVER.verifyReceivedEvent(SystemFormatEvent.class);
    }

    @Test
    @Order(6)
    void testStartTrackingBus() {
        String clientId = EVENT_RECEIVER.getClientId();

        given()
            .contentType(ContentType.JSON)
            .body("{\"clientId\":\"" + clientId + "\"}")
            .when().post("/api/bus/start-tracking-bus")
            .then()
            .statusCode(200);
    }

    @Test
    @Order(7)
    void testSendBusDataAndVerifyEvent() {
        // write a byte to dispatch a BusDataEvent to our client
        int bus = 0;
        int address = 98;
        int value = 12;

        given()
            .contentType(ContentType.JSON)
            .body("{\"bus\":" + bus + ",\"address\":" + address + ",\"value\":" + value + "}")
            .when().post("/api/bus/bus-data")
            .then()
            .statusCode(200);

        // Verify BusDataEvent targeted to our websocket client (type + bus/address are sufficient)
        EVENT_RECEIVER.verifyReceivedEvent(BusDataEvent.class, "\"bus\":" + bus, "\"address\":" + address);
    }

    @Test
    @Order(8)
    void testSendBusBitAndVerifyEvent() {
        int bus = 0;
        int address = 99;
        int bit = 4;
        boolean state = true;

        given()
            .contentType(ContentType.JSON)
            .body("{\"bus\":" + bus + ",\"address\":" + address + ",\"bit\":" + bit + ",\"state\":" + state + "}")
            .when().post("/api/bus/bus-bit")
            .then()
            .statusCode(200);

        // Verify BusDataEvent (we only assert type + bus/address)
        EVENT_RECEIVER.verifyReceivedEvent(BusDataEvent.class, "\"bus\":" + bus, "\"address\":" + address);
    }

    @Test
    @Order(9)
    void testStopTrackingBus() {
        String clientId = EVENT_RECEIVER.getClientId();

        given()
            .contentType(ContentType.JSON)
            .body("{\"clientId\":\"" + clientId + "\"}")
            .when().post("/api/bus/stop-tracking-bus")
            .then()
            .statusCode(200);
    }
}
