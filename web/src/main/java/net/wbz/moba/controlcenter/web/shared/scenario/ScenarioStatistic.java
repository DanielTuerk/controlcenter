package net.wbz.moba.controlcenter.web.shared.scenario;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Daniel Tuerk
 */
public class ScenarioStatistic implements Serializable {

    private Scenario scenario;
    private long runs;
    private double averageRunTimeInMillis;
    private Date nextRun;
    private Date lastRun;

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public long getRuns() {
        return runs;
    }

    public void setRuns(long runs) {
        this.runs = runs;
    }

    public double getAverageRunTimeInMillis() {
        return averageRunTimeInMillis;
    }

    public void setAverageRunTimeInMillis(double averageRunTimeInMillis) {
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
