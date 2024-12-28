package net.wbz.moba.controlcenter.web.server.web.train;

import com.google.gson.GsonBuilder;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorService;

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
    public String loadTrains() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(
            trainEditorService.getTrains());
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String loadTrain(@PathParam(value = "id") Long id) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(
            trainEditorService.getTrains().stream().filter(train -> train.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("no train found for id " + id)));
    }
}
