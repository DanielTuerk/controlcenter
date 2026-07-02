package net.wbz.moba.controlcenter.api.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.api.BaseIt;
import net.wbz.moba.controlcenter.shared.train.TrainDataChangedEvent;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainResourceTest extends BaseIt {

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
            .body("address", notNullValue())
            .body("functions", anyOf(nullValue(), empty()));
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

    @Test
    @Order(9)
    void testUpdateTrain_SetFunctionsToNull() {
        // create train with one function
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name":"IT-TRAIN-FUNC-NULL","address":20,
                    "functions":[{"alias":"F1","configuration":{"bus":0,"address":10,"bit":1,"bitState":true}}]
                }""")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        // verify created with function
        given()
            .pathParam("id", trainId)
            .when().get("/api/trains/{id}")
            .then()
            .statusCode(200)
            .body("functions.size()", equalTo(1));

        // update with functions = null
        given()
            .contentType(ContentType.JSON)
            .body(String.format("{\"id\":%d,\"name\":\"IT-TRAIN-FUNC-NULL-UPDATED\",\"address\":21,\"functions\":null}", trainId))
            .pathParam("id", trainId)
            .when()
            .put("/api/trains/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("IT-TRAIN-FUNC-NULL-UPDATED"))
            .body("functions.size()", equalTo(1));

        // WebSocket/event check
        EVENT_RECEIVER.verifyReceivedEvent(TrainDataChangedEvent.class, "\"itemId\":" + trainId);

        // finally verify persisted state
        given()
            .pathParam("id", trainId)
            .when().get("/api/trains/{id}")
            .then()
            .statusCode(200)
            .body("functions.size()", equalTo(1));
    }

    @Test
    @Order(10)
    void testUpdateTrain_AddFunctions() {
        // create train without functions
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-FUNC-CREATE\",\"address\":22}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        // update with two functions
        String updateBody = String.format("""
            {
                "id":%d,"name":"IT-TRAIN-FUNC-UPDATED","address":23,
                "functions":[
                    {"alias":"F1","configuration":{"bus":0,"address":11,"bit":2,"bitState":true}},
                    {"alias":"F2","configuration":{"bus":0,"address":11,"bit":3,"bitState":true}}
                ]
            }""", trainId);

        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
            .pathParam("id", trainId)
            .when()
            .put("/api/trains/{id}")
            .then()
            .statusCode(200)
            .body("functions.size()", equalTo(2));

        // WebSocket/event check
        EVENT_RECEIVER.verifyReceivedEvent(TrainDataChangedEvent.class, "\"itemId\":" + trainId);

        // finally verify persisted state
        given()
            .pathParam("id", trainId)
            .when().get("/api/trains/{id}")
            .then()
            .statusCode(200)
            .body("functions.size()", equalTo(2));
    }

    @Test
    @Order(11)
    void testRemoveFunction() {
        // create train with two functions
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("""
                {
                "name":"IT-TRAIN-REMOVE-FUNC",
                "address":30,
                "functions":[
                            {"alias":"F1","configuration":{"bus":0,"address":31,"bit":1,"bitState":true}},
                            {"alias":"F2","configuration":{"bus":0,"address":31,"bit":2,"bitState":true}}
                            ]
                }""")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        // verify created with two functions and extract their ids
        var functionIds = given()
            .pathParam("id", trainId)
            .when().get("/api/trains/{id}")
            .then()
            .statusCode(200)
            .body("functions.size()", equalTo(2))
            .extract()
            .jsonPath()
            .getList("functions.id");

        Integer idToKeep = (Integer) functionIds.getFirst();

        // update train keeping only one function (remove the other)
        String updateBody = String.format("""
            {"id":%d,"name":"IT-TRAIN-REMOVE-FUNC-UPDATED","address":31,
            "functions":[{"id":%d,"alias":"F_KEEP","configuration":{"bus":0,"address":31,"bit":1,"bitState":true}}]
            }""", trainId, idToKeep);

        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
            .pathParam("id", trainId)
            .when()
            .put("/api/trains/{id}")
            .then()
            .statusCode(200)
            .body("functions.size()", equalTo(1))
            .body("functions[0].id", equalTo(idToKeep));

        // WebSocket/event check
        EVENT_RECEIVER.verifyReceivedEvent(TrainDataChangedEvent.class, "\"itemId\":" + trainId);

        // finally, verify persisted state
        given()
            .pathParam("id", trainId)
            .when().get("/api/trains/{id}")
            .then()
            .statusCode(200)
            .body("functions.size()", equalTo(1))
            .body("functions[0].id", equalTo(idToKeep));
    }

}
