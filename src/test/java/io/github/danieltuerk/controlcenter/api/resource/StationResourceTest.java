package io.github.danieltuerk.controlcenter.api.resource;

import io.github.danieltuerk.controlcenter.api.BaseIt;
import io.github.danieltuerk.controlcenter.api.ItUtil;
import io.github.danieltuerk.controlcenter.shared.station.StationDataChangedEvent;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StationResourceTest extends BaseIt {

    @BeforeAll
    public static void beforeAll() {
        RestAssured.port = 8081;
        ItUtil.setCurrentConstruction();
    }

    @Test
    @Order(1)
    void testListAllStations() {
        given()
            .when().get("/api/stations")
            .then()
            .statusCode(200)
            .body("$", isA(java.util.List.class));
    }

    @Test
    @Order(2)
    void testGetStationById_NotFound() {
        given()
            .pathParam("id", 999L)
            .when().get("/api/stations/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(3)
    void testCreateStation() {
        int initialCount = given()
            .when().get("/api/stations")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$")
            .size();

        var stationId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-STATION-1\",\"platforms\":[]}")
            .when()
            .post("/api/stations")
            .then()
            .statusCode(201)
            .body("name", equalTo("IT-STATION-1"))
            .extract()
            .jsonPath()
            .getLong("id");

        // Verify WebSocket event was received
        EVENT_RECEIVER.verifyReceivedEvent(StationDataChangedEvent.class, "\"itemId\":" + stationId);

        // Verify created
        given()
            .when().get("/api/stations")
            .then()
            .statusCode(200)
            .body("$", hasSize(initialCount + 1))
            .body("find { it.name == 'IT-STATION-1' }.name", equalTo("IT-STATION-1"));
    }

    @Test
    @Order(4)
    void testGetStationById_Found() {
        Long stationId = given()
            .when().get("/api/stations")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("[0].id");

        given()
            .pathParam("id", stationId)
            .when().get("/api/stations/{id}")
            .then()
            .statusCode(200)
            .body("id", equalTo(stationId.intValue()))
            .body("name", notNullValue());
    }

    @Test
    @Order(5)
    void testUpdateStation() {
        // Create dedicated station for update
        Long stationId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-STATION-TO-UPDATE\",\"platforms\":[]}")
            .when()
            .post("/api/stations")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-STATION-UPDATED\",\"platforms\":[]}")
            .pathParam("id", stationId)
            .when()
            .put("/api/stations/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("IT-STATION-UPDATED"));

        // Verify WebSocket event was received for update
        EVENT_RECEIVER.verifyReceivedEvent(StationDataChangedEvent.class, "\"itemId\":" + stationId);

        // Verify update
        given()
            .pathParam("id", stationId)
            .when().get("/api/stations/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("IT-STATION-UPDATED"));
    }

    @Test
    @Order(6)
    void testUpdateStation_NotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"NOT-EXIST\",\"platforms\":[]}")
            .pathParam("id", 999L)
            .when()
            .put("/api/stations/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(7)
    void testDeleteStation() {
        // Create a station to delete
        Long stationId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-STATION-TO-DELETE\",\"platforms\":[]}")
            .when()
            .post("/api/stations")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        // Delete
        given()
            .pathParam("id", stationId)
            .when()
            .delete("/api/stations/{id}")
            .then()
            .statusCode(204);

        // Verify delete event
        EVENT_RECEIVER.verifyReceivedEvent(StationDataChangedEvent.class, "\"itemId\":" + stationId);

        // Verify it is gone
        given()
            .pathParam("id", stationId)
            .when().get("/api/stations/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(8)
    void testDeleteStation_NotFound() {
        given()
            .pathParam("id", 999L)
            .when()
            .delete("/api/stations/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(9)
    void testCreateStation_WithPlatformAndBlockStraight() {
        // Add a BlockStraight track part to assign to a platform
        String addBlockStraightPayload = "{" +
            "\"addActions\":[{" +
            "\"trackPart\":{" +
            "\"trackPartType\":\"BlockStraight\"," +
            "\"gridPosition\":{\"x\":50,\"y\":50}," +
            "\"direction\":\"HORIZONTAL\"," +
            "\"blockLength\":1" +
            "}" +
            "}]," +
            "\"moveActions\":[]," +
            "\"rotateActions\":[]" +
            "}";

        given()
            .contentType(ContentType.JSON)
            .body(addBlockStraightPayload)
            .when()
            .put("/api/track")
            .then()
            .statusCode(200);

        Long blockStraightId = given()
            .when().get("/api/track")
            .then()
            .statusCode(200)
            .body("find { it.trackPartType == 'BlockStraight' && it.gridPosition.x == 50 && it.gridPosition.y == 50 }", notNullValue())
            .extract()
            .jsonPath()
            .getLong("find { it.trackPartType == 'BlockStraight' && it.gridPosition.x == 50 && it.gridPosition.y == 50 }.id");

        Long stationId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-STATION-WITH-PLATFORM\",\"platforms\":[{\"name\":\"Platform A\",\"blockStraightIds\":[" + blockStraightId + "]}]}")
            .when()
            .post("/api/stations")
            .then()
            .statusCode(201)
            .body("platforms", hasSize(1))
            .body("platforms[0].name", equalTo("Platform A"))
            .body("platforms[0].blockStraights", hasSize(1))
            .body("platforms[0].blockStraights[0].id", equalTo(blockStraightId.intValue()))
            .extract()
            .jsonPath()
            .getLong("id");

        // Verify the platform -> blockStraight association is actually persisted, not just echoed back
        given()
            .pathParam("id", stationId)
            .when().get("/api/stations/{id}")
            .then()
            .statusCode(200)
            .body("platforms[0].blockStraights", hasSize(1))
            .body("platforms[0].blockStraights[0].id", equalTo(blockStraightId.intValue()));
    }

    @Test
    @Order(10)
    void testUpdateStation_UnknownBlockStraightId_ShouldFail() {
        Long stationId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-STATION-UNKNOWN-BLOCK-STRAIGHT\",\"platforms\":[]}")
            .when()
            .post("/api/stations")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-STATION-UNKNOWN-BLOCK-STRAIGHT\",\"platforms\":[{\"name\":\"Platform A\",\"blockStraightIds\":[999999]}]}")
            .pathParam("id", stationId)
            .when()
            .put("/api/stations/{id}")
            .then()
            .statusCode(500);
    }
}
