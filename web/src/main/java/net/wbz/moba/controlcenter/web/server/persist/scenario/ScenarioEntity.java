package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.googlecode.jmapper.annotations.JMap;

import javax.persistence.OneToMany;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainEntity;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "SCENARIO")
public class ScenarioEntity extends AbstractEntity {

    @JMap
    @Column
    private String name;

    @JMap
    @Column
    private String cron;

    /**
     * TODO also looking for position on track - drive to possible start point, or resume on actual pos
     * TODO check existing on track
     */
    @JMap
    @ManyToOne
    private TrainEntity train;

    /**
     * Route to drive from start to end station.
     * TODO: interstations aren't supported yet
     */
    @JMap
    @OneToMany
    private List<RouteEntity> routes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public TrainEntity getTrain() {
        return train;
    }

    public void setTrain(TrainEntity train) {
        this.train = train;
    }

    public List<RouteEntity> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteEntity> routes) {
        this.routes = routes;
    }
}
