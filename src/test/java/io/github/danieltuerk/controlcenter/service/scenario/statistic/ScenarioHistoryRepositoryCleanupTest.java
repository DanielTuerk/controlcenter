package io.github.danieltuerk.controlcenter.service.scenario.statistic;

import io.github.danieltuerk.controlcenter.api.ItUtil;
import io.github.danieltuerk.controlcenter.persist.entity.ScenarioHistoryEntity;
import io.github.danieltuerk.controlcenter.persist.repository.ScenarioHistoryRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class ScenarioHistoryRepositoryCleanupTest {

    @Inject
    ScenarioHistoryRepository repository;

    @BeforeAll
    static void beforeAll() {
        RestAssured.port = 8081;
        ItUtil.setCurrentConstruction();
    }

    @Test
    @Transactional
    void testDeleteOldestExceedingPerScenario() {
        var scenario1Id = createScenario("IT-SCENARIO-HISTORY-CLEANUP-1");
        var scenario2Id = createScenario("IT-SCENARIO-HISTORY-CLEANUP-2");

        addHistoryEntries(scenario1Id, 5);
        addHistoryEntries(scenario2Id, 3);
        repository.flush();

        assertEquals(5, repository.listByScenario(scenario1Id).size());
        assertEquals(3, repository.listByScenario(scenario2Id).size());

        // runs against the whole (suite-shared) table, so other scenarios' history may be cleaned up too;
        // only the per-scenario outcome for our own scenarios is asserted here.
        repository.deleteOldestExceedingPerScenario(2);

        assertEquals(2, repository.listByScenario(scenario1Id).size());
        assertEquals(2, repository.listByScenario(scenario2Id).size());
    }

    private static long createScenario(String name) {
        return given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"%s\",\"routeSequences\":[]}".formatted(name))
            .when()
            .post("/api/scenarios")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");
    }

    private void addHistoryEntries(long scenarioId, int count) {
        for (int i = 0; i < count; i++) {
            var entity = new ScenarioHistoryEntity();
            entity.scenarioId = scenarioId;
            entity.startDateTime = LocalDateTime.now().minusMinutes(i);
            entity.endDateTime = LocalDateTime.now().minusMinutes(i).plusSeconds(1);
            entity.elapsedTimeMillis = 1000;
            entity.type = ScenarioHistoryEntity.TYPE.SUCCESS;
            repository.persist(entity);
        }
    }
}
