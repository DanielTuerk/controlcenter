package net.wbz.moba.controlcenter.api.track;


import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(ref = "track")
@Path("/api/track")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrackResource {

    @Inject
    TrackProvider trackProvider;

    @GET
    public Collection<? extends AbstractTrackPart> listAll() {
        return trackProvider.getTrack();
    }

//    @GET
//    @Path("/{id}")
//    public Response getById(@PathParam("id") Long id) {
//        var byId = constructionManager.getById(id);
//        if(byId.isEmpty()) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//        return Response.ok(byId.get()).build();
//    }

//    @POST
//    @Transactional
//    public Response create(ConstructionDto created) {
//        var construction = constructionManager.create(created);
//        return Response.status(Response.Status.CREATED)
//            .entity(construction)
//            .build();
//    }
//
//    @PUT
//    @Path("/{id}")
//    public Response update(@PathParam("id") Long id, ConstructionDto updated) {
//        if (!constructionManager.existsById(id)) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//        constructionManager.update(id, updated);
//        return Response.ok().build();
//    }
//
//    @DELETE
//    @Path("/{id}")
//    public Response delete(@PathParam("id") Long id) {
//        boolean deleted = constructionManager.deleteById(id);
//        if (deleted) {
//            return Response.noContent().build();
//        } else {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//    }

}

