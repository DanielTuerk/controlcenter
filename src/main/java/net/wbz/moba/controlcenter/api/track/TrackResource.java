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

}

