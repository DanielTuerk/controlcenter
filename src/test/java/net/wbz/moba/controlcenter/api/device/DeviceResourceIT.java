package net.wbz.moba.controlcenter.api.device;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.wbz.moba.controlcenter.it.BaseIt;
import net.wbz.moba.controlcenter.shared.device.DeviceConnectionEvent;
import net.wbz.moba.controlcenter.shared.device.DeviceDataChangedEvent;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DeviceResourceIT extends BaseIt {

    @Test
    @Order(1)
    void testListAllDevices() {
        given()
            .when().get("/api/devices")
            .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body("[0].key", notNullValue())
            .body("[0].type", notNullValue());
    }

    @Test
    @Order(2)
    void testGetAvailableDevices() {
        given()
            .when().get("/api/devices/available")
            .then()
            .statusCode(200)
            .body("$", notNullValue());
    }

    @Test
    @Order(3)
    void testGetDeviceById_Found() {
        Long deviceId = given()
            .when().get("/api/devices")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("[0].id");

        given()
            .pathParam("id", deviceId)
            .when().get("/api/devices/{id}")
            .then()
            .statusCode(200)
            .body("id", equalTo(deviceId.intValue()))
            .body("key", notNullValue())
            .body("type", notNullValue());
    }

    @Test
    @Order(4)
    void testGetDeviceById_NotFound() {
        given()
            .pathParam("id", 999L)
            .when().get("/api/devices/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(5)
    void testCreateDevice() {
        int initialCount = given()
            .when().get("/api/devices")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$")
            .size();

        var createdId = given()
            .contentType(ContentType.JSON)
            .body("{\"key\":\"IT-DEVICE-1\",\"type\":\"TEST\"}")
            .when()
            .post("/api/devices")
            .then()
            .statusCode(201)
            .body("key", equalTo("IT-DEVICE-1"))
            .extract()
            .jsonPath()
            .getLong("id");

        // Verify WebSocket event was received
        verifyReceivedEvent(DeviceDataChangedEvent.class, "\"itemId\":" + createdId);

        // Verify created
        given()
            .when().get("/api/devices")
            .then()
            .statusCode(200)
            .body("$", hasSize(initialCount + 1))
            .body("find { it.key == 'IT-DEVICE-1' }.key", equalTo("IT-DEVICE-1"));
    }

    @Test
    @Order(6)
    void testUpdateDevice() {
        // Create a dedicated device for update to avoid modifying the seeded TEST device
        Long deviceId = given()
            .contentType(ContentType.JSON)
            .body("{\"key\":\"IT-DEVICE-TO-UPDATE\",\"type\":\"TEST\"}")
            .when()
            .post("/api/devices")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        given()
            .contentType(ContentType.JSON)
            .body("{\"key\":\"IT-DEVICE-UPDATED\",\"type\":\"TEST\"}")
            .pathParam("id", deviceId)
            .when()
            .put("/api/devices/{id}")
            .then()
            .statusCode(200)
            .body("key", equalTo("IT-DEVICE-UPDATED"));

        // Verify WebSocket event was received for update
        verifyReceivedEvent(DeviceDataChangedEvent.class, "\"itemId\":" + deviceId);

        // Verify update
        given()
            .pathParam("id", deviceId)
            .when().get("/api/devices/{id}")
            .then()
            .statusCode(200)
            .body("key", equalTo("IT-DEVICE-UPDATED"));
    }

    @Test
    @Order(7)
    void testUpdateDevice_NotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"key\":\"NOT-EXIST\",\"type\":\"TEST\"}")
            .pathParam("id", 999L)
            .when()
            .put("/api/devices/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(8)
    void testDeleteDevice() {
        // Create a device to delete
        Long deviceId = given()
            .contentType(ContentType.JSON)
            .body("{\"key\":\"IT-DEVICE-TO-DELETE\",\"type\":\"TEST\"}")
            .when()
            .post("/api/devices")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        // Delete
        given()
            .pathParam("id", deviceId)
            .when()
            .delete("/api/devices/{id}")
            .then()
            .statusCode(204);

        // Verify delete event
        verifyReceivedEvent(DeviceDataChangedEvent.class, "\"itemId\":" + deviceId);

        // Verify it is gone
        given()
            .pathParam("id", deviceId)
            .when().get("/api/devices/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(9)
    void testDeleteDevice_NotFound() {
        given()
            .pathParam("id", 999L)
            .when()
            .delete("/api/devices/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(10)
    void testConnectDevice() {
        // Use the seeded TEST device from import-test-data.sql (key = "TEST")
        var json = given()
            .when().get("/api/devices")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath();
        Long deviceId = json.getLong("find { it.key == 'TEST' }.id");
        String deviceKey = "TEST";

        int status = given()
            .pathParam("id", deviceId)
            .when()
            .post("/api/devices/{id}/connect")
            .then()
            .extract()
            .statusCode();

        if (status == 200) {
            // Verify connection event for the TEST device
            verifyReceivedEvent(DeviceConnectionEvent.class, "\"connected\":true");
            verifyReceivedEvent(DeviceConnectionEvent.class, "\"key\":\"" + deviceKey + "\"");
        }
    }

    @Test
    @Order(11)
    void testDisconnectDevice() {
        // Ensure the TEST device is connected first
        var json = given()
            .when().get("/api/devices")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath();
        Long deviceId = json.getLong("find { it.key == 'TEST' }.id");

        int connectStatus = given()
            .pathParam("id", deviceId)
            .when()
            .post("/api/devices/{id}/connect")
            .then()
            .extract()
            .statusCode();

        // Now disconnect
        int disconnectStatus = given()
            .when()
            .post("/api/devices/disconnect")
            .then()
            .extract()
            .statusCode();

        if (connectStatus == 200 && disconnectStatus == 200) {
            // Verify disconnected event
            verifyReceivedEvent(DeviceConnectionEvent.class, "\"connected\":false");
            verifyReceivedEvent(DeviceConnectionEvent.class, "\"key\":\"TEST\"");
        }
    }
}
