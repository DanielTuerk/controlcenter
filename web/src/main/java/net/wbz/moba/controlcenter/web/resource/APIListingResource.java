package net.wbz.moba.controlcenter.web.resource;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.Operation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("openapi.{type:json|yaml}")
public class APIListingResource {

    @Inject
    OpenApiResource resource;

    @GET
    @Produces({"application/json", "application/yaml"})
    @Operation(hidden = true)
    public Response getResource(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("type") String type) throws Exception {
        return resource.getOpenApi(headers, uriInfo, type);
    }
}
