package net.wbz.moba.controlcenter.service.scenario;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job which is triggered for the {@link net.wbz.moba.controlcenter.shared.scenario.Scenario} run by cron.
 * TODO use {@link ScenarioService} by guice injector
 * 
 * @author Daniel Tuerk
 */
public class ScheduleScenarioJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ScenarioService.getInstance().foobarStartScheduledScenario(
                (Long) jobExecutionContext.getJobDetail().getJobDataMap().get("scenario"));
    }
}
