package net.wbz.moba.controlcenter.web.shared.scenario;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Daniel Tuerk
 */
public class ScenarioStatistic implements Serializable {

    private Scenario scenario;
    private int runs;
    private long averageRunTimeInMillis;
    private Date nextRun;
    private Date lastRun;

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public long getAverageRunTimeInMillis() {
        return averageRunTimeInMillis;
    }

    public void setAverageRunTimeInMillis(long averageRunTimeInMillis) {
        this.averageRunTimeInMillis = averageRunTimeInMillis;
    }

    public Date getNextRun() {
        return nextRun;
    }

    public void setNextRun(Date nextRun) {
        this.nextRun = nextRun;
    }

    public Date getLastRun() {
        return lastRun;
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }
}
