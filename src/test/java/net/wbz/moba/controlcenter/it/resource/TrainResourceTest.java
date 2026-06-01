package net.wbz.moba.controlcenter.it.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.it.WebSocketEventReceiver;
import net.wbz.moba.controlcenter.shared.train.TrainDataChangedEvent;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainResourceTest {
    private static final WebSocketEventReceiver EVENT_RECEIVER = new WebSocketEventReceiver();

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
        EVENT_RECEIVER.verifyReceivedEvent(TrainDataChangedEvent.class, "\"itemId\":" + trainId);

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
        EVENT_RECEIVER.verifyReceivedEvent(TrainDataChangedEvent.class, "\"itemId\":" + trainId);

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
        EVENT_RECEIVER.verifyReceivedEvent(TrainDataChangedEvent.class, "\"itemId\":" + trainId);

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

}
