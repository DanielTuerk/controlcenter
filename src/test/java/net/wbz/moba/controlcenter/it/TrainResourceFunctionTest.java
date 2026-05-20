package net.wbz.moba.controlcenter.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.shared.train.TrainDrivingDirectionEvent;
import net.wbz.moba.controlcenter.shared.train.TrainDrivingLevelEvent;
import net.wbz.moba.controlcenter.shared.train.TrainHornStateEvent;
import net.wbz.moba.controlcenter.shared.train.TrainLightStateEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainResourceFunctionTest {
    private static final WebSocketEventReceiver EVENT_RECEIVER = new WebSocketEventReceiver();

    @BeforeAll
    public static void beforeAll() {
        ItUtil.connectTestDevice();
    }

    @Test
    void testUpdateDrivingDirection() {
        // ensure at least one train exists
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-DIR\",\"address\":5}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

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
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-LVL\",\"address\":6}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

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
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-LIGHT\",\"address\":7}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

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
        Long trainId = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"IT-TRAIN-HORN\",\"address\":8}")
            .when()
            .post("/api/trains")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

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
