package net.wbz.moba.controlcenter.api.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.api.BaseIt;
import net.wbz.moba.controlcenter.api.ItUtil;
import net.wbz.moba.controlcenter.shared.viewer.TrackPartStateEvent;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Integration test to verify toggling a track part via TrackResource:
 * - Connect TEST device and ensure rail voltage is ON
 * - Create a toggle-capable Turnout with a valid toggleFunction
 * - Call toggle endpoint and verify TrackPartStateEvent via WebSocket
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrackResourceToggleTrackPartTest extends BaseIt {

    @Test
    void toggleTurnoutAndVerifyEvent() {
        final var constructionId = ItUtil.setCurrentConstruction();
        ItUtil.connectTestDevice();
        var turnoutId = createTurnoutWithToggleFunction(constructionId);

        given()
            .contentType(ContentType.JSON)
            .pathParam("id", turnoutId)
            .when()
            .post("/api/track/{id}/toggle")
            .then()
            .statusCode(200);

        // Verify TrackPartStateEvent received for this turnoutId and configuration
        // We assert the routeSequenceId and parts of the configuration (bus/address) to avoid coupling to state value
        EVENT_RECEIVER.verifyReceivedEvent(
            TrackPartStateEvent.class,
            "\"trackPartId\":" + turnoutId,
            "\"bus\":1,\"address\":4"
        );
    }

    private long createTurnoutWithToggleFunction(int constructionId) {
        // Create a Turnout at (4,4) with a valid toggle function (bus=0,address=4,bit=1)
        String payload = """
            {
              "addActions": [
                {
                  "trackPart": {
                    "constructionId": %d,
                    "trackPartType": "Turnout",
                    "gridPosition": {
                      "x": 4,
                      "y": 4
                    },
                    "currentDirection": "RIGHT",
                    "currentPresentation": "LEFT_TO_RIGHT",
                    "toggleFunction": {
                      "bus": 1,
                      "address": 4,
                      "bit": 1,
                      "bitState": true
                    }
                  }
                }
              ],
              "moveActions": [],
              "rotateActions": []
            }
            """.formatted(constructionId);


        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .put("/api/track")
            .then()
            .statusCode(200);

        // Capture created turnout routeSequenceId
        return given()
            .when().get("/api/track")
            .then()
            .statusCode(200)
            .body("find { it.trackPartType == 'Turnout' && it.gridPosition.x == 4 && it.gridPosition.y == 4 }", notNullValue())
            .extract()
            .jsonPath()
            .getLong("find { it.trackPartType == 'Turnout' && it.gridPosition.x == 4 && it.gridPosition.y == 4 }.id");
    }
}
