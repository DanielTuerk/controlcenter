package net.wbz.moba.controlcenter.service.scenario;

import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import org.mapstruct.ap.internal.util.Strings;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Daniel Tuerk
 */
public final class ScenarioUtil {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm");

    public static String nextExecutionTime(Scenario scenario) {
        if (Strings.isNotEmpty(scenario.getCron())) {
            try {
                return FORMATTER.format(new CronExpression(scenario.getCron()).getNextValidTimeAfter(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String arrivalTimeOfNextExecution(Scenario scenario, long addMillis) {
        if (Strings.isNotEmpty(scenario.getCron())) {
            try {
                Date nextValidTimeAfter = new CronExpression(scenario.getCron()).getNextValidTimeAfter(new Date());
                nextValidTimeAfter.setTime(nextValidTimeAfter.getTime() + addMillis);
                return FORMATTER.format(nextValidTimeAfter);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Date getDateFromTimeText(String timeText) {
        if (Strings.isEmpty(timeText)) {
            return null;
        }
        try {
            return FORMATTER.parse(timeText);
        } catch (ParseException e) {
            return null;
        }
    }
}
