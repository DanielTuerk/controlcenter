package net.wbz.moba.controlcenter.persist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;


/**
 * TODO refactor to routes from RouteSequenceEntity to be able to track the time also for intersections.
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "SCENARIO_HISTORY")
public class ScenarioHistoryEntity extends AbstractEntity {

    @ManyToOne
    public ScenarioEntity scenario;

    public LocalDateTime startDateTime;

    public LocalDateTime endDateTime;

    public long elapsedTimeMillis;

}
