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
import jakarta.ws.rs.core.Response.Status;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.service.track.TrackPartManager;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.service.track.TrackViewerService;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.track.model.Signal.FUNCTION;
import net.wbz.selectrix4java.device.DeviceAccessException;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Collection;

@Slf4j
@Tag(ref = "track")
@Path("/api/track")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Blocking
public class TrackResource {

    @Inject
    TrackProvider trackProvider;
    @Inject
    TrackViewerService trackViewerService;

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
            log.error("Failed to toggle track part {}", trackPartId, e);
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
    public Response update(@PathParam("id") Long id, AbstractTrackPart dto) {
        try {
            trackPartManager.update(id, dto);
            return Response.ok().build();
        } catch (Exception e) {
            log.error("Failed to update track part: {}", id, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response change(ChangeTrackDto dto) {
        try {
            trackPartManager.change(dto);
            return Response.ok().build();
        } catch (IllegalStateException e) {
            log.error("Failed to update track part: {}", dto, e);
            return Response.status(Status.CONFLICT).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (trackPartManager.deleteById(id)) {
            return Response.noContent().build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

}

