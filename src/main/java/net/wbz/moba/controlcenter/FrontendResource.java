package net.wbz.moba.controlcenter;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/{path: (cc|welcome)(/.*)?}")
public class FrontendResource {

    @GET
    public Response redirect() {
        return Response
            .ok(getClass().getResourceAsStream("/META-INF/resources/index.html"))
            .build();
    }
}