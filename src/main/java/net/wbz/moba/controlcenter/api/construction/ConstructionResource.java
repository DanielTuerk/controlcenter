package net.wbz.moba.controlcenter.api.construction;


import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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
import java.util.List;
import net.wbz.moba.controlcenter.service.constrution.ConstructionManager;
import net.wbz.moba.controlcenter.shared.constrution.Construction;

@Path("/api/constructions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConstructionResource {

    @Inject
    ConstructionManager constructionManager;

    @GET
    public List<Construction> listAll() {
        return constructionManager.load();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        var byId = constructionManager.getById(id);
        if(byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(byId.get()).build();
    }

    @POST
    @Transactional
    public Response create(ConstructionDto created) {
        var construction = constructionManager.create(created);
        return Response.status(Response.Status.CREATED)
            .entity(construction)
            .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, ConstructionDto updated) {
        if (!constructionManager.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        constructionManager.update(id, updated);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = constructionManager.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}

