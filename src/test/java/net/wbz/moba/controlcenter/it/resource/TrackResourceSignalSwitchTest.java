package net.wbz.moba.controlcenter.it.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.it.BaseIt;
import net.wbz.moba.controlcenter.it.ItUtil;
import net.wbz.moba.controlcenter.shared.viewer.SignalFunctionStateEvent;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Integration test to verify switching a Signal via TrackResource:
 * - Connect TEST device and ensure rail voltage is ON
 * - Create a BLOCK-type Signal with minimal RED1/GREEN1 bus config
 * - Call switch-signal to HP1 and verify SignalFunctionStateEvent via WebSocket
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrackResourceSignalSwitchTest extends BaseIt {

    private static Long signalId;

    void createBlockSignal() {
        // Minimal BLOCK signal at (3,3) using two lights: RED1 and GREEN1
        // Use small bus/address/bit values similar to other ITs
        String payload = "{" +
            "\"addActions\":[{" +
            "\"trackPart\":{" +
            "\"trackPartType\":\"Signal\"," +
            "\"gridPosition\":{\"x\":3,\"y\":3}," +
            "\"direction\":\"HORIZONTAL\"," +
            "\"type\":\"BLOCK\"," +
            "\"signalConfigRed1\":{\"bus\":0,\"address\":3,\"bit\":1,\"bitState\":true}," +
            "\"signalConfigGreen1\":{\"bus\":0,\"address\":3,\"bit\":2,\"bitState\":true}" +
            "}" +
            "}]," +
            "\"moveActions\":[]," +
            "\"rotateActions\":[]" +
            "}";

        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .put("/api/track")
            .then()
            .statusCode(200);

        // Find created signal routeSequenceId
        signalId = given()
            .when().get("/api/track")
            .then()
            .statusCode(200)
            .body("find { it.trackPartType == 'Signal' && it.gridPosition.x == 3 && it.gridPosition.y == 3 }", notNullValue())
            .extract()
            .jsonPath()
            .getLong("find { it.trackPartType == 'Signal' && it.gridPosition.x == 3 && it.gridPosition.y == 3 }.id");
    }

    @Test
    void switchSignalAndVerifyEvent() {
        ItUtil.setCurrentConstruction();
        ItUtil.connectTestDevice();
        createBlockSignal();

        // Switch to HP1 (expect GREEN1 active for BLOCK type)
        given()
            .contentType(ContentType.TEXT)
            .pathParam("id", signalId)
            .body("HP1")
            .when()
            .post("/api/track/{id}/switch-signal")
            .then()
            .statusCode(200);

        // Verify SignalFunctionStateEvent received for this signalId and function HP1
        EVENT_RECEIVER.verifyReceivedEvent(
            SignalFunctionStateEvent.class,
            "\"signalId\":" + signalId,
            "\"signalFunction\":\"HP1\""
        );
    }
}
