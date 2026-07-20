package io.github.danieltuerk.controlcenter.api.resource;

import io.github.danieltuerk.controlcenter.api.BaseIt;
import io.github.danieltuerk.controlcenter.api.BaseTestData;
import io.github.danieltuerk.controlcenter.api.ItUtil;
import io.github.danieltuerk.controlcenter.shared.train.TrainDrivingDirectionEvent;
import io.github.danieltuerk.controlcenter.shared.train.TrainDrivingLevelEvent;
import io.github.danieltuerk.controlcenter.shared.train.TrainHornStateEvent;
import io.github.danieltuerk.controlcenter.shared.train.TrainLightStateEvent;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainResourceFunctionTest extends BaseIt {

    @BeforeEach
    public void beforeAll() {
        ItUtil.connectTestDevice();
    }

    @Test
    void testUpdateDrivingDirection() {
        // ensure at least one train exists
        final var trainId = BaseTestData.TRAIN1.id();
        given()
            .contentType(ContentType.TEXT)
            .body("FORWARD")
            .pathParam("id", trainId)
            .when()
            .post("/api/trains/{id}/direction")
            .then()
            .statusCode(200);

        // Verify that reregistered consumer emitted a direction event (only checks type + itemId)
        EVENT_RECEIVER.verifyReceivedEvent(TrainDrivingDirectionEvent.class, "\"itemId\":" + trainId);
    }

    @Test
    void testUpdateDrivingLevel() {
        var trainId = BaseTestData.TRAIN1.id();

        given()
            .contentType(ContentType.TEXT)
            .body("10")
            .pathParam("id", trainId)
            .when()
            .post("/api/trains/{id}/level")
            .then()
            .statusCode(200);

        // Verify that reregistered consumer emitted a driving level event (only checks type + itemId)
        EVENT_RECEIVER.verifyReceivedEvent(TrainDrivingLevelEvent.class, "\"itemId\":" + trainId);
    }

    @Test
    void testToggleLight() {
        var trainId = BaseTestData.TRAIN1.id();

        given()
            .contentType(ContentType.TEXT)
            .body("true")
            .pathParam("id", trainId)
            .when()
            .post("/api/trains/{id}/light")
            .then()
            .statusCode(200);

        // Verify that reregistered consumer emitted a light state event (only checks type + itemId)
        EVENT_RECEIVER.verifyReceivedEvent(TrainLightStateEvent.class, "\"itemId\":" + trainId);
    }

    @Test
    void testToggleHorn() {
        var trainId = BaseTestData.TRAIN1.id();

        given()
            .contentType(ContentType.TEXT)
            .body("true")
            .pathParam("id", trainId)
            .when()
            .post("/api/trains/{id}/horn")
            .then()
            .statusCode(200);

        // Verify that reregistered consumer emitted a horn state event (only checks type + itemId)
        EVENT_RECEIVER.verifyReceivedEvent(TrainHornStateEvent.class, "\"itemId\":" + trainId);
    }
}
