package net.wbz.moba.controlcenter.api.track;


import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.service.track.TrackViewerService;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.track.model.Signal.FUNCTION;
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

    @GET
    public Collection<? extends AbstractTrackPart> listAll() {
        return trackProvider.getTrack();
    }

    @POST
    @Path("/{id}/toggle")
    public Response toggleTrackPart(@PathParam("id") Long trackPartId) {
        var $trackPart = trackProvider.getTrackPart(trackPartId);
        if ($trackPart.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            trackViewerService.toggleTrackPart($trackPart.get());
            return Response.ok().build();
        } catch (DeviceAccessException e) {
            logger.error("Failed to toggle track part " + trackPartId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/{id}/switch-signal")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response switchSignal(@PathParam("id") Long trackPartId, FUNCTION signalFunction) {
        var $trackPart = trackProvider.getTrackPart(trackPartId);
        if ($trackPart.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!($trackPart.get() instanceof Signal)) {
            return Response.notModified().build();
        }
        trackViewerService.switchSignal((Signal) $trackPart.get(), signalFunction);
        return Response.ok().build();
    }
}

