package net.wbz.moba.controlcenter.it.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.it.BaseIt;
import net.wbz.moba.controlcenter.shared.constrution.CurrentConstructionChangeEvent;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CurrentConstructionResourceTest extends BaseIt {

    @Test
    @Order(1)
    void testGetCurrent_NotSet_ShouldReturn404() {
        given()
        .when()
            .get("/api/current-construction")
        .then()
            .statusCode(404);
    }

    @Test
    @Order(2)
    void testSetCurrent_WithValidId_ThenVerifyGet() {
        // obtain an existing construction routeSequenceId
        Long firstId = given()
            .when().get("/api/constructions")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("[0].id");

        // set current construction via POST body (text/plain)
        given()
            .contentType(ContentType.TEXT)
            .body(firstId)
        .when()
            .post("/api/current-construction")
        .then()
            .statusCode(200)
            .contentType(containsString("application/json"))
            .body("id", equalTo(firstId.intValue()))
            .body("name", notNullValue());

        // verify websocket event for current construction change has been emitted
        EVENT_RECEIVER.verifyReceivedEvent(CurrentConstructionChangeEvent.class);

        // verify GET returns the same current construction
        given()
        .when()
            .get("/api/current-construction")
        .then()
            .statusCode(200)
            .body("id", equalTo(firstId.intValue()))
            .body("name", notNullValue());
    }

    @Test
    @Order(3)
    void testSetCurrent_WithUnknownId_ShouldReturn404() {
        long unknownId = 999999L;
        given()
            .contentType(ContentType.TEXT)
            .body(unknownId)
        .when()
            .post("/api/current-construction")
        .then()
            .statusCode(404);
    }

    @Test
    @Order(4)
    void testChangeCurrent_ToAnotherValidId() {
        // get another existing construction routeSequenceId (different from the first)
        Long secondId = given()
            .when().get("/api/constructions")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("[1].id");

        given()
            .contentType(ContentType.TEXT)
            .body(secondId)
        .when()
            .post("/api/current-construction")
        .then()
            .statusCode(200)
            .body("id", equalTo(secondId.intValue()));

        // verify GET now returns the updated current construction
        // and a change event was broadcasted over websocket
        EVENT_RECEIVER.verifyReceivedEvent(CurrentConstructionChangeEvent.class);
        given()
        .when()
            .get("/api/current-construction")
        .then()
            .statusCode(200)
            .body("id", equalTo(secondId.intValue()))
            .body("name", notNullValue());
    }
}
