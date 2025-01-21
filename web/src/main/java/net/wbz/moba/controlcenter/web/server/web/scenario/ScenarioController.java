package net.wbz.moba.controlcenter.web.server.web.scenario;

import java.util.Collection;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.wbz.moba.controlcenter.web.resource.RestResource;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
@RestResource
@Path("/api/scenario")
public class ScenarioController {

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Collection<FakeScenario> getSecnarios() {
        var fakeScenario = new FakeScenario(2);
        fakeScenario.setId2(3);
        return List.of(new FakeScenario(1), fakeScenario);
    }

    public class FakeScenario {
        private final int id;
        private int id2;


        public FakeScenario(int id) {
            this.id = id;
        }

        public void setId2(int id2) {
            this.id2 = id2;
        }

        public int getId() {
            return id;
        }

        public int getId2() {
            return id2;
        }
    }
}
