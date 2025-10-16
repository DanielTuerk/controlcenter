package net.wbz.moba.controlcenter.api.train;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import net.wbz.moba.controlcenter.service.train.TrainManager;
import net.wbz.moba.controlcenter.shared.train.Train;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TrainResourceTest {

    @InjectMock
    TrainManager trainManager;

    @Test
    void testListAll() {
        var train = new Train();
        train.setId(1L);
        train.setName("ICE");

        when(trainManager.load()).thenReturn(List.of(train));

        given()
            .when().get("/trains")
            .then()
            .statusCode(200)
            .body("$.size()", is(1))
            .body("[0].name", equalTo("ICE"));
    }

    @Test
    void testGetById_found() {
        var train = new Train();
        train.setId(1L);
        train.setName("ICE");

        when(trainManager.getById(1L)).thenReturn(Optional.of(train));

        given()
            .when().get("/trains/1")
            .then()
            .statusCode(200)
            .body("name", equalTo("ICE"));
    }

    @Test
    void testGetById_notFound() {
        when(trainManager.getById(99L)).thenReturn(Optional.empty());

        given()
            .when().get("/trains/99")
            .then()
            .statusCode(404);
    }

    @Test
    void testCreate() {
        var dto = new TrainDto("ICE", 1);

        var created = new Train();
        created.setId(1L);
        created.setName("ICE");

        when(trainManager.create(any())).thenReturn(created);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .when().post("/trains")
            .then()
            .statusCode(201)
            .body("id", equalTo(1))
            .body("name", equalTo("ICE"));
    }

    @Test
    void testUpdate_found() {
        var dto = new TrainDto("Updated", 11);

        when(trainManager.existsById(1L)).thenReturn(true);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .when().put("/trains/1")
            .then()
            .statusCode(200);

        verify(trainManager).update(1L, dto);
    }

    @Test
    void testUpdate_notFound() {
        var dto = new TrainDto("Updated", 12);

        when(trainManager.existsById(99L)).thenReturn(false);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .when().put("/trains/99")
            .then()
            .statusCode(404);
    }

    @Test
    void testDelete_found() {
        when(trainManager.deleteById(1L)).thenReturn(true);

        given()
            .when().delete("/trains/1")
            .then()
            .statusCode(204);
    }

    @Test
    void testDelete_notFound() {
        when(trainManager.deleteById(99L)).thenReturn(false);

        given()
            .when().delete("/trains/99")
            .then()
            .statusCode(404);
    }
}
