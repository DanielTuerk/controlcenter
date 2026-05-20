package net.wbz.moba.controlcenter.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.shared.scenario.RouteDataChangedEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RouteResourceTest {
    private static final WebSocketEventReceiver EVENT_RECEIVER = new WebSocketEventReceiver();

    @BeforeAll
    public static void beforeAll() {
        RestAssured.port = 8081;
        ItUtil.setCurrentConstruction();
    }

    @Test
    @Order(1)
    void testListAllRoutes() {
        given()
            .when().get("/api/routes")
            .then()
            .statusCode(200)
            .body("$", isA(java.util.List.class));
    }

    @Test
    @Order(2)
    void testGetRouteById_NotFound() {
        given()
            .pathParam("id", 999L)
            .when().get("/api/routes/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(3)
    void testCreateRoute() {
        int initialCount = given()
            .when().get("/api/routes")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$")
            .size();

        var routeId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-ROUTE-1\",\"oneway\":true,\"waypoints\":[]}")
            .when()
            .post("/api/routes")
            .then()
            .statusCode(201)
            .body("name", equalTo("IT-ROUTE-1"))
            .extract()
            .jsonPath()
            .getLong("id");

        // Verify WebSocket event was received with CREATE action type
        EVENT_RECEIVER.verifyReceivedEvent(RouteDataChangedEvent.class, "\"itemId\":" + routeId, "\"type\":\"CREATE\"");

        // Verify created
        given()
            .when().get("/api/routes")
            .then()
            .statusCode(200)
            .body("$", hasSize(initialCount + 1))
            .body("find { it.name == 'IT-ROUTE-1' }.name", equalTo("IT-ROUTE-1"));
    }

    @Test
    @Order(4)
    void testGetRouteById_Found() {
        Long routeId = given()
            .when().get("/api/routes")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("[0].id");

        given()
            .pathParam("id", routeId)
            .when().get("/api/routes/{id}")
            .then()
            .statusCode(200)
            .body("id", equalTo(routeId.intValue()))
            .body("name", notNullValue());
    }

    @Test
    @Order(5)
    void testUpdateRoute() {
        // Create dedicated route for update
        Long routeId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-ROUTE-TO-UPDATE\",\"oneway\":true,\"waypoints\":[]}")
            .when()
            .post("/api/routes")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-ROUTE-UPDATED\",\"oneway\":false,\"waypoints\":[]}")
            .pathParam("id", routeId)
            .when()
            .put("/api/routes/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("IT-ROUTE-UPDATED"))
            .body("oneway", equalTo(false));

        // Verify WebSocket event was received for update with UPDATE action type
        EVENT_RECEIVER.verifyReceivedEvent(RouteDataChangedEvent.class, "\"itemId\":" + routeId, "\"type\":\"UPDATE\"");

        // Verify update
        given()
            .pathParam("id", routeId)
            .when().get("/api/routes/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("IT-ROUTE-UPDATED"))
            .body("oneway", equalTo(false));
    }

    @Test
    @Order(6)
    void testUpdateRoute_NotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"NOT-EXIST\",\"oneway\":true,\"waypoints\":[]}")
            .pathParam("id", 999L)
            .when()
            .put("/api/routes/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(7)
    void testDeleteRoute() {
        // Create a route to delete
        Long routeId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-ROUTE-TO-DELETE\",\"oneway\":true,\"waypoints\":[]}")
            .when()
            .post("/api/routes")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        // Delete
        given()
            .pathParam("id", routeId)
            .when()
            .delete("/api/routes/{id}")
            .then()
            .statusCode(204);

        // Verify delete event with DELETE action type
        EVENT_RECEIVER.verifyReceivedEvent(RouteDataChangedEvent.class, "\"itemId\":" + routeId, "\"type\":\"DELETE\"");

        // Verify it is gone
        given()
            .pathParam("id", routeId)
            .when().get("/api/routes/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(8)
    void testDeleteRoute_NotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", 999L)
            .when()
            .delete("/api/routes/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(9)
    void testBuildTrack_RouteNotFound() {
        // Try to build track for a non-existent route
        given()
            .contentType(ContentType.JSON)
            .body("{\"id\":999,\"name\":\"ROUTE-NOT-EXIST\",\"oneway\":true,\"waypoints\":[]}")
            .when()
            .post("/api/routes/build-track")
            .then()
            .statusCode(406);
    }
}

