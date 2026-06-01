package net.wbz.moba.controlcenter.it;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ItUtil {

    public static Long setCurrentConstruction() {
        Long constructionId = given()
            .when().get("/api/constructions")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("[0].id");

        given()
            .contentType(ContentType.TEXT)
            .body(constructionId)
            .when()
            .post("/api/current-construction")
            .then()
            .statusCode(200)
            .body("id", equalTo(constructionId.intValue()));
        return constructionId;
    }

    public static void connectTestDevice() {
        disconnectTestDevice(false);
        var deviceId = given()
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

    public static void disconnectTestDevice() {
        disconnectTestDevice(true);
    }

    public static void disconnectTestDevice(boolean verify) {
        final var then = given()
            .contentType(ContentType.JSON)
            .when()
            .post("/api/devices/disconnect")
            .then();
        if (verify) {
            then.statusCode(200);
        }
    }
}
