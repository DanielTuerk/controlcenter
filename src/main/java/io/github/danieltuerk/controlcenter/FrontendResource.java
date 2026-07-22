package io.github.danieltuerk.controlcenter;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/")
public class FrontendResource {

    @GET
    @Operation(hidden = true)
    @Path("/{path: (cc|welcome)(/.*)?}")
    public Response redirect() {
        return Response
            .ok(getClass().getResourceAsStream("/META-INF/resources/index.html"))
            .build();
    }
}