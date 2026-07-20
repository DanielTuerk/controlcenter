package io.github.danieltuerk.controlcenter.api.resource;

import io.github.danieltuerk.controlcenter.api.BaseIt;
import io.github.danieltuerk.controlcenter.api.ItUtil;
import io.github.danieltuerk.controlcenter.shared.bus.BusDataEvent;
import io.github.danieltuerk.controlcenter.shared.bus.RailVoltageEvent;
import io.github.danieltuerk.controlcenter.shared.bus.SystemFormatEvent;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BusResourceTest extends BaseIt {

    @BeforeAll
    public static void beforeAll() {
        RestAssured.port = 8081;
        ItUtil.connectTestDevice();
    }

    @Test
    @Order(2)
    void testFetchRailVoltage() {
        ItUtil.fetchRailVoltage();
    }

    @Test
    @Order(3)
    void testToggleRailVoltageAndVerifyEvent() {
        ItUtil.toggleRailvoltage();

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

        ItUtil.sendBusData(bus, address, value);

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
            .body("""
                {
                    "bus":%d,
                    "address":%d,
                    "bit":%d,
                    "state":%s
                }""".formatted(bus, address, bit, state))
            .when().post("/api/bus/bus-bit")
            .then()
            .statusCode(200);

        // Verify BusDataEvent (we only assert type + bus/address)
        EVENT_RECEIVER.verifyReceivedEvent(BusDataEvent.class, "\"bus\":" + bus, "\"address\":" + address);
    }

    @Test
    @Order(9)
    void testStopTrackingBus() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"clientId\":\"%s\"}".formatted(EVENT_RECEIVER.getClientId()))
            .when().post("/api/bus/stop-tracking-bus")
            .then()
            .statusCode(200);
    }
}
