package net.wbz.moba.controlcenter.web.server.web.scenario;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job which is triggered for the {@link net.wbz.moba.controlcenter.web.shared.scenario.Scenario} run by cron.
 * TODO use {@link ScenarioServiceImpl} by guice injector
 * 
 * @author Daniel Tuerk
 */
public class ScheduleScenarioJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ScenarioServiceImpl.getInstance().foobarStartScheduledScenario(
                (Long) jobExecutionContext.getJobDetail().getJobDataMap().get("scenario"));
    }
}
