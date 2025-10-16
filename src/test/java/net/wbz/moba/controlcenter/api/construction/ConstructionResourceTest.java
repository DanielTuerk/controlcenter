package net.wbz.moba.controlcenter.api.construction;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ConstructionResourceTest {

    @Test
    public void testCreateAndGetConstruction() {
        // Create
        Number id = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Bridge Project\"}")
            .when()
            .post("/constructions")
            .then()
            .statusCode(201)
            .body("name", equalTo("Bridge Project"))
            .extract()
            .path("id");

        // Get
        given()
            .pathParam("id", id.longValue())
            .when()
            .get("/constructions/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("Bridge Project"));
    }

    @Test
    public void testUpdateConstruction() {
        // Create
        Number id = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Old Name\"}")
            .when()
            .post("/constructions")
            .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Update
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Updated Name\"}")
            .pathParam("id", id)
            .when()
            .put("/constructions/{id}")
            .then()
            .statusCode(200);


        // Get
        given()
            .pathParam("id", id.longValue())
            .when()
            .get("/constructions/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("Updated Name"));
    }

    @Test
    public void testDeleteConstruction() {
        // Create
        Number id = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"To Be Deleted\"}")
            .when()
            .post("/constructions")
            .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Delete
        given()
            .pathParam("id", id)
            .when()
            .delete("/constructions/{id}")
            .then()
            .statusCode(204);

        // Verify deletion
        given()
            .pathParam("id", id)
            .when()
            .get("/constructions/{id}")
            .then()
            .statusCode(404);
    }
}

