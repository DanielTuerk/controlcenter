package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

/**
 * TODO refactor to routes from RouteSequenceEntity to be able to track the time also for intersections.
 *
 * @author Daniel Tuerk
 */
@Entity(name = "SCENARIO_HISTORY")
public class ScenarioHistoryEntity extends AbstractEntity {

    @ManyToOne
    private ScenarioEntity scenario;

    @Column
    private LocalDateTime startDateTime;

    @Column
    private LocalDateTime endDateTime;

    @Column
    private long elapsedTimeMillis;

    public ScenarioEntity getScenario() {
        return scenario;
    }

    public void setScenario(ScenarioEntity scenario) {
        this.scenario = scenario;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDate) {
        this.startDateTime = startDate;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDate) {
        this.endDateTime = endDate;
    }

    public long getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    public void setElapsedTimeMillis(long elapsedTime) {
        this.elapsedTimeMillis = elapsedTime;
    }
}
