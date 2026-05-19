package net.wbz.moba.controlcenter.api.train;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.it.BaseIt;
import net.wbz.moba.controlcenter.shared.train.TrainDataChangedEvent;
import net.wbz.moba.controlcenter.shared.train.TrainDrivingDirectionEvent;
import net.wbz.moba.controlcenter.shared.train.TrainDrivingLevelEvent;
import net.wbz.moba.controlcenter.shared.train.TrainHornStateEvent;
import net.wbz.moba.controlcenter.shared.train.TrainLightStateEvent;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainResourceIT extends BaseIt {

    @Test
    @Order(1)
    void testListAllTrains() {
        given()
            .when().get("/api/trains")
            .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test
    @Order(2)
    void testGetTrainById_NotFound() {
        given()
            .pathParam("id", 999L)
            .when().get("/api/trains/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(3)
    void testCreateTrain() {
        int initialCount = given()
            .when().get("/api/trains")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$")
            .size();

        var trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-1\",\"address\":1}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .body("name", equalTo("IT-TRAIN-1"))
            .extract()
            .jsonPath()
            .getLong("id");

        // Verify WebSocket event was received
        verifyReceivedEvent(TrainDataChangedEvent.class, "\"itemId\":" + trainId);

        // Verify created
        given()
            .when().get("/api/trains")
            .then()
            .statusCode(200)
            .body("$", hasSize(initialCount + 1))
            .body("find { it.name == 'IT-TRAIN-1' }.name", equalTo("IT-TRAIN-1"));
    }

    @Test
    @Order(4)
    void testGetTrainById_Found() {
        Long trainId = given()
            .when().get("/api/trains")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("[0].id");

        given()
            .pathParam("id", trainId)
            .when().get("/api/trains/{id}")
            .then()
            .statusCode(200)
            .body("id", equalTo(trainId.intValue()))
            .body("name", notNullValue())
            .body("address", notNullValue());
    }

    @Test
    @Order(5)
    void testUpdateTrain() {
        // Create dedicated train for update
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-TO-UPDATE\",\"address\":2}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-UPDATED\",\"address\":3}")
            .pathParam("id", trainId)
            .when()
            .put("/api/trains/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("IT-TRAIN-UPDATED"))
            .body("address", equalTo(3));

        // Verify WebSocket event was received for update
        verifyReceivedEvent(TrainDataChangedEvent.class, "\"itemId\":" + trainId);

        // Verify update
        given()
            .pathParam("id", trainId)
            .when().get("/api/trains/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("IT-TRAIN-UPDATED"))
            .body("address", equalTo(3));
    }

    @Test
    @Order(6)
    void testUpdateTrain_NotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"NOT-EXIST\",\"address\":10}")
            .pathParam("id", 999L)
            .when()
            .put("/api/trains/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(7)
    void testDeleteTrain() {
        // Create a train to delete
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-TO-DELETE\",\"address\":4}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        // Delete
        given()
            .pathParam("id", trainId)
            .when()
            .delete("/api/trains/{id}")
            .then()
            .statusCode(204);

        // Verify delete event
        verifyReceivedEvent(TrainDataChangedEvent.class, "\"itemId\":" + trainId);

        // Verify it is gone
        given()
            .pathParam("id", trainId)
            .when().get("/api/trains/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(8)
    void testDeleteTrain_NotFound() {
        given()
            .pathParam("id", 999L)
            .when()
            .delete("/api/trains/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(9)
    void testConnectTestDevice() {
        // connect the TEST device to enable TrainService#reregisterConsumer listeners
        Long deviceId = given()
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

    @Test
    @Order(10)
    void testUpdateDrivingDirection() {
        // ensure at least one train exists
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-DIR\",\"address\":5}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        given()
            .contentType(ContentType.TEXT)
            .body("FORWARD")
            .pathParam("id", trainId)
            .when()
            .post("/api/trains/{id}/direction")
            .then()
            .statusCode(200);

        // Verify that reregistered consumer emitted a direction event (only checks type + itemId)
        verifyReceivedEvent(TrainDrivingDirectionEvent.class, "\"itemId\":" + trainId);
    }

    @Test
    @Order(11)
    void testUpdateDrivingLevel() {
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-LVL\",\"address\":6}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        given()
            .contentType(ContentType.TEXT)
            .body("10")
            .pathParam("id", trainId)
            .when()
            .post("/api/trains/{id}/level")
            .then()
            .statusCode(200);

        // Verify that reregistered consumer emitted a driving level event (only checks type + itemId)
        verifyReceivedEvent(TrainDrivingLevelEvent.class, "\"itemId\":" + trainId);
    }

    @Test
    @Order(12)
    void testToggleLight() {
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-LIGHT\",\"address\":7}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        given()
            .contentType(ContentType.TEXT)
            .body("true")
            .pathParam("id", trainId)
            .when()
            .post("/api/trains/{id}/light")
            .then()
            .statusCode(200);

        // Verify that reregistered consumer emitted a light state event (only checks type + itemId)
        verifyReceivedEvent(TrainLightStateEvent.class, "\"itemId\":" + trainId);
    }

    @Test
    @Order(13)
    void testToggleHorn() {
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-HORN\",\"address\":8}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        given()
            .contentType(ContentType.TEXT)
            .body("true")
            .pathParam("id", trainId)
            .when()
            .post("/api/trains/{id}/horn")
            .then()
            .statusCode(200);

        // Verify that reregistered consumer emitted a horn state event (only checks type + itemId)
        verifyReceivedEvent(TrainHornStateEvent.class, "\"itemId\":" + trainId);
    }
}
