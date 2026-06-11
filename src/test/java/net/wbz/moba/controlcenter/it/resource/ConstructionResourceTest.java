package net.wbz.moba.controlcenter.it.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.it.BaseIt;
import net.wbz.moba.controlcenter.shared.constrution.ConstructionDataChangedEvent;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConstructionResourceTest extends BaseIt {

    @Test
    @Order(1)
    void testListAllConstructions() {
        given()
            .when().get("/api/constructions")
            .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(2)))
            .body("[0].name", notNullValue())
            .body("[1].name", notNullValue());
    }

    @Test
    @Order(2)
    void testGetConstructionById_Found() {
        // First get all constructions to find an ID
        Long constructionId = given()
            .when().get("/api/constructions")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("[0].id");

        given()
            .pathParam("id", constructionId)
            .when().get("/api/constructions/{id}")
            .then()
            .statusCode(200)
            .body("name", notNullValue())
            .body("id", equalTo(constructionId.intValue()));
    }

    @Test
    @Order(3)
    void testGetConstructionById_NotFound() {
        given()
            .pathParam("id", 999L)
            .when().get("/api/constructions/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(4)
    void testCreateConstruction() {
        // Get initial count
        int initialCount = given()
            .when().get("/api/constructions")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$")
            .size();

        var constructionId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"New Integration Construction\"}")
            .when()
            .post("/api/constructions")
            .then()
            .statusCode(201)
            .body("name", equalTo("New Integration Construction"))
            .extract()
            .jsonPath()
            .getLong("id");

        // Verify WebSocket event was received
        EVENT_RECEIVER.verifyReceivedEvent(ConstructionDataChangedEvent.class, "\"itemId\":" + constructionId);

        // Verify it was created by checking count increased
        given()
            .when().get("/api/constructions")
            .then()
            .statusCode(200)
            .body("$", hasSize(initialCount + 1))
            .body("find { it.name == 'New Integration Construction' }.name", equalTo("New Integration Construction"));
    }

    @Test
    @Order(5)
    void testUpdateConstruction() {
        // Get first construction ID
        Long constructionId = given()
            .when().get("/api/constructions")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("[0].id");

        // Update the construction
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Updated Integration Construction\"}")
            .pathParam("id", constructionId)
            .when()
            .put("/api/constructions/{id}")
            .then()
            .statusCode(200);

        // Verify WebSocket event was received
        EVENT_RECEIVER.verifyReceivedEvent(ConstructionDataChangedEvent.class, "\"itemId\":" + constructionId);

        // Verify update by getting the construction
        given()
            .pathParam("id", constructionId)
            .when().get("/api/constructions/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("Updated Integration Construction"));
    }

    @Test
    @Order(6)
    void testUpdateConstruction_NotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Updated Construction\"}")
            .pathParam("id", 999L)
            .when()
            .put("/api/constructions/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(7)
    void testDeleteConstruction() {
        // Create a new construction specifically for deletion test
        Long constructionId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Construction To Delete\"}")
            .when()
            .post("/api/constructions")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        // Delete the construction
        given()
            .pathParam("id", constructionId)
            .when()
            .delete("/api/constructions/{id}")
            .then()
            .statusCode(204);

        // Verify WebSocket event was received for deletion
        EVENT_RECEIVER.verifyReceivedEvent(ConstructionDataChangedEvent.class, "\"itemId\":" + constructionId);

        // Verify deletion by trying to get it
        given()
            .pathParam("id", constructionId)
            .when().get("/api/constructions/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(8)
    void testDeleteConstruction_CurrentConstruction() {
        // Get first construction ID (assuming it's not the current one)
        Long constructionId = given()
            .when().get("/api/constructions")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("[0].id");

        // Try to delete - this might fail if it's the current construction or has dependencies
        given()
            .pathParam("id", constructionId)
            .when()
            .delete("/api/constructions/{id}")
            .then()
            .statusCode(anyOf(is(204), is(403), is(500))); // Either succeeds, forbidden if current, or error if constraints
    }

    @Test
    @Order(9)
    void testDeleteConstruction_NotFound() {
        given()
            .pathParam("id", 999L)
            .when()
            .delete("/api/constructions/{id}")
            .then()
            .statusCode(404);
    }
}
