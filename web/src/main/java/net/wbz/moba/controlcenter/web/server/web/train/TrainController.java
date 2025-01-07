package net.wbz.moba.controlcenter.web.server.web.train;

import com.sun.jersey.api.NotFoundException;
import java.util.Collection;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
@Path("/api/train")
public class TrainController {

    private final TrainEditorService trainEditorService;

    @Inject
    public TrainController(TrainEditorService trainEditorService) {
        this.trainEditorService = trainEditorService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Collection<Train> loadTrains() {
        return trainEditorService.getTrains();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Train loadTrain(@PathParam(value = "id") Long id) {
        return trainEditorService.getTrains()
            .stream()
            .filter(train -> train.getId().equals(id))
                .findFirst()
            .orElseThrow(() -> new NotFoundException("no train found for id " + id));
    }
}
