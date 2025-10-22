package net.wbz.moba.controlcenter.api;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Tag(name = "Websocket DTOs")
@Path("/docs")
public class WebsocketDoc {

    @GET
    @Operation(summary = "WebSocket DTO example", description = "This documents the TrainUpdate DTO used in WebSocket messages.")
    public Response getExample() {
        return Response.noContent().build();
    }
}
