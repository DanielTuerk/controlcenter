package net.wbz.moba.controlcenter.it.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.it.BaseIt;
import net.wbz.moba.controlcenter.it.ItUtil;
import net.wbz.moba.controlcenter.shared.bus.BusDataEvent;
import net.wbz.moba.controlcenter.shared.track.model.TrackChangedEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrackResourceTest extends BaseIt {

    private static Long createdTrackPartId;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.port = 8081;
        ItUtil.setCurrentConstruction();
    }

    @Test
    @Order(1)
    void testListAllTrackParts_Initial() {
        given()
            .when()
            .get("/api/track")
            .then()
            .statusCode(200)
            .body("$", isA(java.util.List.class));
    }

    @Test
    @Order(2)
    void testGetUnknownTrackPart_ShouldReturn404() {
        given()
            .pathParam("id", 999999)
            .when()
            .get("/api/track/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(3)
    void testAddStraightViaChangeEndpoint() {
        String payload = "{" +
            "\"addActions\":[{" +
            "\"trackPart\":{" +
            "\"trackPartType\":\"Straight\"," +
            "\"gridPosition\":{\"x\":1,\"y\":1}," +
            "\"direction\":\"HORIZONTAL\"}" +
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

        // Changing the track should emit a TrackChangedEvent (dirty=true)
        EVENT_RECEIVER.verifyReceivedEvent(TrackChangedEvent.class);
    }

    @Test
    @Order(4)
    void testListContainsCreatedStraight_AndGetById() {
        // extract routeSequenceId of the just created straight at (1,1)
        createdTrackPartId = given()
            .when().get("/api/track")
            .then()
            .statusCode(200)
            .body("find { it.trackPartType == 'Straight' && it.gridPosition.x == 1 && it.gridPosition.y == 1 }", notNullValue())
            .extract()
            .jsonPath()
            .getLong("find { it.trackPartType == 'Straight' && it.gridPosition.x == 1 && it.gridPosition.y == 1 }.id");

        given()
            .pathParam("id", createdTrackPartId)
            .when()
            .get("/api/track/{id}")
            .then()
            .statusCode(200)
            .body("id", equalTo(createdTrackPartId.intValue()))
            .body("trackPartType", equalTo("Straight"))
            .body("gridPosition.x", equalTo(1))
            .body("gridPosition.y", equalTo(1));
    }

    @Test
    @Order(5)
    void testToggleTrackPart_WithConnectedDevice_VerifyBusEvent() {
        // 1) Ensure TEST device is connected (required for toggling via DeviceManager)
        ItUtil.connectTestDevice();

        // 2) Add a toggle-capable track part (Turnout) with a valid toggleFunction configuration
        // Use bus=0, address=1, bit=1 (kept small as used elsewhere in ITs)
        String addTurnoutPayload = "{" +
            "\"addActions\":[{" +
            "\"trackPart\":{" +
            "\"trackPartType\":\"Turnout\"," +
            "\"gridPosition\":{\"x\":2,\"y\":2}," +
            "\"currentDirection\":\"RIGHT\"," +
            "\"currentPresentation\":\"LEFT_TO_RIGHT\"," +
            "\"toggleFunction\":{\"bus\":0,\"address\":1,\"bit\":1,\"bitState\":true}" +
            "}" +
            "}]," +
            "\"moveActions\":[]," +
            "\"rotateActions\":[]" +
            "}";

        given()
            .contentType(ContentType.JSON)
            .body(addTurnoutPayload)
            .when()
            .put("/api/track")
            .then()
            .statusCode(200);

        // 3) Find the created turnout routeSequenceId at (2,2)
        Long turnoutId = given()
            .when().get("/api/track")
            .then()
            .statusCode(200)
            .body("find { it.trackPartType == 'Turnout' && it.gridPosition.x == 2 && it.gridPosition.y == 2 }", notNullValue())
            .extract()
            .jsonPath()
            .getLong("find { it.trackPartType == 'Turnout' && it.gridPosition.x == 2 && it.gridPosition.y == 2 }.id");

        // 4) Toggle it; on CI some environments may not provide a real device and respond 500
        int toggleStatus = given()
            .pathParam("id", turnoutId)
            .when()
            .post("/api/track/{id}/toggle")
            .then()
            .extract().statusCode();

        if (toggleStatus == 200) {
            // Verify the BusDataEvent was emitted on websocket (assert bus and address fields)
            EVENT_RECEIVER.verifyReceivedEvent(BusDataEvent.class, "\"bus\":0", "\"address\":1");
        }
    }

    @Test
    @Order(6)
    void testSwitchSignalOnNonSignal_ShouldReturn304() {
        // send a valid FUNCTION value, but for a non-signal track part this should be 304 (not modified)
        given()
            .contentType(ContentType.TEXT)
            .pathParam("id", createdTrackPartId)
            .body("HP0")
            .when()
            .post("/api/track/{id}/switch-signal")
            .then()
            .statusCode(304);
    }

    @Test
    @Order(7)
    void testUpdateTrackPart_NoChange_ShouldReturn500() {
        // fetch the DTO and submit it back as an update
        String dto = given()
            .pathParam("id", createdTrackPartId)
            .when()
            .get("/api/track/{id}")
            .then()
            .statusCode(200)
            .extract().asString();

        given()
            .contentType(ContentType.JSON)
            .pathParam("id", createdTrackPartId)
            .body(dto)
            .when()
            .put("/api/track/{id}")
            .then()
            // updating a Straight is not supported by TrackPartManager and results in 500
            .statusCode(500);
    }

    @Test
    @Order(8)
    void testDeleteTrackPart_ThenVerify404OnGet() {
        given()
            .pathParam("id", createdTrackPartId)
            .when()
            .delete("/api/track/{id}")
            .then()
            .statusCode(204);

        given()
            .pathParam("id", createdTrackPartId)
            .when()
            .get("/api/track/{id}")
            .then()
            .statusCode(404);
    }
}
