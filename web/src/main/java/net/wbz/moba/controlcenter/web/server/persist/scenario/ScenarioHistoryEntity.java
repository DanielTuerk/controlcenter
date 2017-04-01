package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.googlecode.jmapper.annotations.JMap;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import org.joda.time.DateTime;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "SCENARIO_HISTORY")
public class ScenarioHistoryEntity extends AbstractEntity {

    @JMap
    @ManyToOne
    private ScenarioEntity scenario;

    @JMap
    @Column
    private DateTime runDate;

    @JMap
    @Column
    private long elapsedTime;

    public ScenarioEntity getScenario() {
        return scenario;
    }

    public void setScenario(ScenarioEntity scenario) {
        this.scenario = scenario;
    }

    public DateTime getRunDate() {
        return runDate;
    }

    public void setRunDate(DateTime runDate) {
        this.runDate = runDate;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
