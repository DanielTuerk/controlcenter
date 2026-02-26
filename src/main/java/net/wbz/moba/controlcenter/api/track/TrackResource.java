package net.wbz.moba.controlcenter.api.track;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import jakarta.ws.rs.core.Response.Status;
import java.util.Collection;
import net.wbz.moba.controlcenter.service.track.TrackPartManager;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.service.track.TrackViewerService;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.shared.track.model.Turnout;
import net.wbz.moba.controlcenter.shared.track.model.Uncoupler;
import net.wbz.selectrix4java.device.DeviceAccessException;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Tag(ref = "track")
@Path("/api/track")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrackResource {

    @Inject
    TrackProvider trackProvider;
    @Inject
    TrackViewerService trackViewerService;
    @Inject
    Logger logger;
    @Inject
    TrackPartManager trackPartManager;

    @GET
    public Collection<? extends AbstractTrackPart> listAll() {
        return trackProvider.getTrack();
    }

    @GET
    @Path("/{id}")
    public Response loadTrackPart(@PathParam("id") Long trackPartId) {
        var $trackPart = trackProvider.getTrackPart(trackPartId);
        if ($trackPart.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok($trackPart.get()).build();
    }

    @POST
    @Path("/{id}/toggle")
    public Response toggleTrackPart(@PathParam("id") Long trackPartId) {
        var $trackPart = trackProvider.getTrackPart(trackPartId);
        if ($trackPart.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        try {
            trackViewerService.toggleTrackPart($trackPart.get());
            return Response.ok().build();
        } catch (DeviceAccessException e) {
            logger.errorf("Failed to toggle track part " + trackPartId, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/{id}/switch-signal")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response switchSignal(@PathParam("id") Long trackPartId, FUNCTION signalFunction) {
        var $trackPart = trackProvider.getTrackPart(trackPartId);
        if ($trackPart.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        if (!($trackPart.get() instanceof Signal)) {
            return Response.notModified().build();
        }
        trackViewerService.switchSignal((Signal) $trackPart.get(), signalFunction);
        return Response.ok().build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, String body) {
        try {
            final var mapper = new ObjectMapper();
            final var node = mapper.readTree(body);
            final var trackPartType = node.get("trackPartType").asText();

            var target = switch (trackPartType) {
                case "Signal" -> Signal.class;
                case "Turnout" -> Turnout.class;
                case "Uncoupler" -> Uncoupler.class;
                case "BlockStraight" -> BlockStraight.class;
                default -> throw new IllegalStateException("Unexpected value: " + trackPartType);
            };
            final var dto = mapper.treeToValue(node, target);

            trackPartManager.update(id, dto);
            return Response.ok().build();
        } catch (Exception e) {
            logger.error("Failed to update track part " + id, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/move")
    @Transactional
    public Response move(@PathParam("id") Long trackPartId, GridPosition newGridPosition) {
        var $trackPart = trackProvider.getTrackPart(trackPartId);
        if ($trackPart.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        try {
            trackPartManager.move($trackPart.get(), newGridPosition);
            return Response.ok().build();
        } catch (IllegalStateException e) {
            return Response.status(Status.CONFLICT).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        if (trackPartManager.deleteById(id)) {
            return Response.noContent().build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}

