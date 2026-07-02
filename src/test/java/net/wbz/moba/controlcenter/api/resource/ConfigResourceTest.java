package net.wbz.moba.controlcenter.api.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfigResourceTest {

    private static final String TEST_KEY = "it.test.key";
    private static final String UNKNOWN_KEY = "unknown.key.for.404";

    @Test
    @Order(1)
    void testGetUnknownKey_ShouldReturn404() {
        given()
            .pathParam("key", UNKNOWN_KEY)
        .when()
            .get("/api/config/{key}")
        .then()
            .statusCode(404);
    }

    @Test
    @Order(2)
    void testListAllConfigs_Initial() {
        given()
        .when()
            .get("/api/config")
        .then()
            .statusCode(200)
            // ensure it's a JSON array (can be empty at start)
            .body("$", isA(java.util.List.class));
    }

    @Test
    @Order(3)
    void testCreateConfigValue_WithPut_ThenLoadByKey() {
        String initialValue = "initial-value";

        // create/save value via PUT
        given()
            .contentType(ContentType.TEXT)
            .pathParam("key", TEST_KEY)
            .body(initialValue)
        .when()
            .put("/api/config/{key}")
        .then()
            .statusCode(200);

        // verify GET by key returns the value as plain text
        given()
            .pathParam("key", TEST_KEY)
        .when()
            .get("/api/config/{key}")
        .then()
            .statusCode(200)
            .contentType(containsString("text/plain"))
            .body(equalTo(initialValue));
    }

    @Test
    @Order(4)
    void testListAllConfigs_ShouldContainInsertedItem() {
        given()
        .when()
            .get("/api/config")
        .then()
            .statusCode(200)
            .body("find { it.key == '" + TEST_KEY + "' }.key", equalTo(TEST_KEY))
            .body("find { it.key == '" + TEST_KEY + "' }.value", equalTo("initial-value"));
    }

    @Test
    @Order(5)
    void testUpdateExistingKey_WithPut_ThenVerify() {
        String updatedValue = "updated-value";

        given()
            .contentType(ContentType.TEXT)
            .pathParam("key", TEST_KEY)
            .body(updatedValue)
        .when()
            .put("/api/config/{key}")
        .then()
            .statusCode(200);

        given()
            .pathParam("key", TEST_KEY)
        .when()
            .get("/api/config/{key}")
        .then()
            .statusCode(200)
            .contentType(containsString("text/plain"))
            .body(equalTo(updatedValue));
    }
}
