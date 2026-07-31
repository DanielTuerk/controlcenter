package io.github.danieltuerk.controlcenter.api.resource;

import io.github.danieltuerk.controlcenter.api.BaseIt;
import io.github.danieltuerk.controlcenter.api.ItUtil;
import io.github.danieltuerk.controlcenter.service.scenario.statistic.ScenarioStatisticManager;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioDataChangedEvent;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioStateEvent;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ScenarioResourceTest extends BaseIt {

    @Inject
    ScenarioStatisticManager scenarioStatisticManager;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.port = 8081;
        ItUtil.setCurrentConstruction();
    }

    @Test
    @Order(1)
    void testListAllScenarios() {
        given()
            .when().get("/api/scenarios")
            .then()
            .statusCode(200)
            .body("$", isA(java.util.List.class));
    }

    @Test
    @Order(2)
    void testGetScenarioById_NotFound() {
        given()
            .pathParam("id", 999L)
            .when().get("/api/scenarios/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(3)
    void testCreateScenario() {
        int initialCount = given()
            .when().get("/api/scenarios")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$")
            .size();

        var scenarioId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-SCENARIO-1\",\"routeSequences\":[]}")
            .when()
            .post("/api/scenarios")
            .then()
            .statusCode(201)
            .body("name", equalTo("IT-SCENARIO-1"))
            .extract()
            .jsonPath()
            .getLong("id");

        // Verify WebSocket event was received with CREATE action type
        EVENT_RECEIVER.verifyReceivedEvent(ScenarioDataChangedEvent.class, "\"itemId\":" + scenarioId, "\"type\":\"CREATE\"");

        // Verify created
        given()
            .when().get("/api/scenarios")
            .then()
            .statusCode(200)
            .body("$", hasSize(initialCount + 1))
            .body("find { it.name == 'IT-SCENARIO-1' }.name", equalTo("IT-SCENARIO-1"));
    }

    @Test
    @Order(4)
    void testGetScenarioById_Found() {
        Long scenarioId = given()
            .when().get("/api/scenarios")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("[0].id");

        given()
            .pathParam("id", scenarioId)
            .when().get("/api/scenarios/{id}")
            .then()
            .statusCode(200)
            .body("id", equalTo(scenarioId.intValue()))
            .body("name", notNullValue());
    }

    @Test
    @Order(5)
    void testUpdateScenario() {
        // Create dedicated scenario for update
        Long scenarioId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-SCENARIO-TO-UPDATE\",\"mode\":\"MANUAL\",\"routeSequences\":[]}")
            .when()
            .post("/api/scenarios")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-SCENARIO-UPDATED\",\"mode\":\"MANUAL\",\"cron\":\"0 0 0 * * ?\",\"routeSequences\":[],\"train\":{\"id\":1,\"name\":\"TEST\",\"address\":1}}")
            .pathParam("id", scenarioId)
            .when()
            .put("/api/scenarios/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("IT-SCENARIO-UPDATED"));

        // Verify WebSocket event was received for update with UPDATE action type
        EVENT_RECEIVER.verifyReceivedEvent(ScenarioDataChangedEvent.class, "\"itemId\":" + scenarioId, "\"type\":\"UPDATE\"");

        // Verify update
        given()
            .pathParam("id", scenarioId)
            .when().get("/api/scenarios/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("IT-SCENARIO-UPDATED"));
    }

    @Test
    @Order(6)
    void testUpdateScenario_NotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"NOT-EXIST\",\"mode\":\"MANUAL\",\"routeSequences\":[]}")
            .pathParam("id", 999L)
            .when()
            .put("/api/scenarios/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(7)
    void testDeleteScenario() {
        // Create a scenario to delete
        Long scenarioId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-SCENARIO-TO-DELETE\",\"mode\":\"MANUAL\",\"routeSequences\":[]}")
            .when()
            .post("/api/scenarios")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        // Delete
        given()
            .pathParam("id", scenarioId)
            .when()
            .delete("/api/scenarios/{id}")
            .then()
            .statusCode(204);

        // Verify delete event with DELETE action type
        EVENT_RECEIVER.verifyReceivedEvent(ScenarioDataChangedEvent.class, "\"itemId\":" + scenarioId, "\"type\":\"DELETE\"");

        // Verify it is gone
        given()
            .pathParam("id", scenarioId)
            .when().get("/api/scenarios/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(8)
    void testDeleteScenario_NotFound() {
        given()
            .pathParam("id", 999L)
            .when()
            .delete("/api/scenarios/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(9)
    void testScenarioStatistic_NotFound() {
        given()
                .pathParam("id", 999L)
                .when().get("/api/scenarios/{id}/statistic")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(10)
    void testScenarioStatistic_NotFound_NoRuns() {
        Long scenarioId = given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"IT-SCENARIO-STATISTIC-NO-RUNS\",\"routeSequences\":[]}")
                .when()
                .post("/api/scenarios")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .pathParam("id", scenarioId)
                .when().get("/api/scenarios/{id}/statistic")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(11)
    void testScenarioStatistic_Found() {
        final var scenarioId = given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"IT-SCENARIO-STATISTIC\",\"routeSequences\":[]}")
                .when()
                .post("/api/scenarios")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        // drive the statistic aggregation the same way a real scenario run does,
        // without having to simulate the full hardware/route execution flow
        scenarioStatisticManager.onScenarioStateEvent(
                new ScenarioStateEvent(scenarioId, Scenario.RUN_STATE.RUNNING));
        scenarioStatisticManager.onScenarioStateEvent(
                new ScenarioStateEvent(scenarioId, Scenario.RUN_STATE.SUCCESS));

        given()
                .pathParam("id", scenarioId)
                .when().get("/api/scenarios/{id}/statistic")
                .then()
                .statusCode(200)
                .body("scenarioId", equalTo((int) scenarioId))
                .body("total", equalTo(1))
                .body("successful", equalTo(1))
                .body("failed", equalTo(0))
                .body("runs", hasSize(1))
                .body("runs[0].state", equalTo("SUCCESS"));
    }

}

