package net.wbz.moba.controlcenter.it.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import net.wbz.moba.controlcenter.it.ITDatabaseTestProfile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@link net.wbz.moba.controlcenter.FrontendResource} serves the Angular
 * {@code index.html} for the single-page-application routes ({@code /cc} and {@code /welcome})
 * and their sub-paths, so that deep links are handled by the frontend router.
 */
@QuarkusIntegrationTest
@TestProfile(ITDatabaseTestProfile.class)
class FrontendResourceTestIT {

    @BeforeAll
    public static void beforeAll() {
        RestAssured.port = 8081;
    }

    @Test
    void testCcRootServesIndexHtml() {
        given()
            .when().get("/cc")
            .then()
            .statusCode(200)
            .body(containsString("<title>Control Center</title>"));
    }

    @Test
    void testWelcomeRootServesIndexHtml() {
        given()
            .when().get("/welcome")
            .then()
            .statusCode(200)
            .body(containsString("<title>Control Center</title>"));
    }

    @Test
    void testCcSubPathServesIndexHtml() {
        given()
            .when().get("/cc/train/42")
            .then()
            .statusCode(200)
            .body(containsString("<title>Control Center</title>"));
    }

    @Test
    void testWelcomeSubPathServesIndexHtml() {
        given()
            .when().get("/welcome/foo/bar")
            .then()
            .statusCode(200)
            .body(containsString("<title>Control Center</title>"));
    }

    @Test
    void testUnmatchedPathIsNotHandledByFrontendResource() {
        // A path that does not match the (cc|welcome) regex must not be served the SPA shell.
        given()
            .when().get("/some-other-path")
            .then()
            .statusCode(404);
    }
}
