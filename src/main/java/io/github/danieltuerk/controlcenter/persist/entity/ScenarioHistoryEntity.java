package io.github.danieltuerk.controlcenter.persist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;


/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "SCENARIO_HISTORY")
public class ScenarioHistoryEntity extends AbstractEntity {

    @Column(name = "SCENARIO_ID")
    public Long scenarioId;

    public LocalDateTime startDateTime;

    public LocalDateTime endDateTime;

    public long elapsedTimeMillis;

    public TYPE type;

    public enum TYPE {SUCCESS, FAILED}
}
