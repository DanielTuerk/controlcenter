package net.wbz.moba.controlcenter.api.track;


import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.wbz.moba.controlcenter.service.constrution.ConstructionService;
import net.wbz.moba.controlcenter.service.track.block.TrackBlockManager;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Collection;

@Tag(ref = "track")
@Path("/api/track-block")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Blocking
public class TrackBlockResource {

    @Inject
    TrackBlockManager trackBlockManager;
    @Inject
    ConstructionService constructionService;

    @GET
    public Collection<TrackBlock> listAll() {
        return trackBlockManager.fetch(currentConstruction().getId());
    }

    @GET
    @Path("/{id}")
    @APIResponseSchema(TrackBlock.class)
    public Response getById(@PathParam("id") Long id) {
        var byId = trackBlockManager.getById(id);
        if (byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(byId.get()).build();
    }

    @POST
    @APIResponseSchema(value = TrackBlock.class, responseCode = "201")
    public Response create(TrackBlock dto) {
        return Response.status(Response.Status.CREATED)
            .entity(trackBlockManager.create(currentConstruction().getId(), dto))
            .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, TrackBlock dto) {
        if (!trackBlockManager.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        trackBlockManager.update(id, dto);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = trackBlockManager.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private Construction currentConstruction() {
        return constructionService.getCurrentConstruction()
            .orElseThrow(() -> new IllegalStateException("No current construction found"));
    }
}

