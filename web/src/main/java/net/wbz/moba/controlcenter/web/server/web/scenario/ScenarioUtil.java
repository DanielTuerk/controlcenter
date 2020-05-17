package net.wbz.moba.controlcenter.web.server.web.scenario;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import org.quartz.CronExpression;

/**
 * @author Daniel Tuerk
 */
public  final class ScenarioUtil {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm");

    public static String nextExecutionTime(Scenario scenario) {
        try {
            return FORMATTER.format(new CronExpression(scenario.getCron()).getNextValidTimeAfter(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
