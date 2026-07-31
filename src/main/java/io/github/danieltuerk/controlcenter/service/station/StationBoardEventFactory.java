package io.github.danieltuerk.controlcenter.service.station;

import io.github.danieltuerk.controlcenter.service.scenario.statistic.ScenarioStatisticManager;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario;
import io.github.danieltuerk.controlcenter.shared.station.StationBoardArrivalEvent;
import io.github.danieltuerk.controlcenter.shared.station.StationBoardDepartureEvent;
import io.github.danieltuerk.controlcenter.shared.station.StationBoardEvent;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
abstract class StationBoardEventFactory<T extends StationBoardEvent> {

    abstract T create(Scenario scenario, long stationId, String platformName, String train, LocalTime startTime,
                      String targetStation, String information);

    protected static long createTtlFromStartTime(LocalTime startTime) {
        return Duration.between(LocalTime.now(), startTime).plusMinutes(1).toMillis();
    }

    static class DepartureEventFactory extends StationBoardEventFactory<StationBoardDepartureEvent> {
        @Override
        public StationBoardDepartureEvent create(Scenario scenario, long stationId, String platformName, String train, LocalTime startTime, String targetStation, String information) {
            return new StationBoardDepartureEvent(new StationBoardEvent.StationInfo(stationId, platformName,
                    train, timeText(startTime), information),
                    targetStation, createTtlFromStartTime(startTime)
            );
        }
    }

    static class ArrivalEventFactory extends StationBoardEventFactory<StationBoardArrivalEvent> {
        private final ScenarioStatisticManager scenarioStatisticManager;

        ArrivalEventFactory(ScenarioStatisticManager scenarioStatisticManager) {
            this.scenarioStatisticManager = scenarioStatisticManager;
        }

        @Override
        public StationBoardArrivalEvent create(Scenario scenario, long stationId, String platformName, String train,
                                               LocalTime startTime, String targetStation, String information) {
            return new StationBoardArrivalEvent(new StationBoardEvent.StationInfo(stationId, platformName,
                    train, timeText(plusAverageExecutionTime(scenario.getId(), startTime)), information),
                    targetStation, createTtlFromStartTime(arrivalTime(scenario))
            );
        }

        private LocalTime plusAverageExecutionTime(long scenarioId, LocalTime startTime) {
            return startTime.plus(scenarioStatisticManager.averageExecutionTimeInMs(scenarioId), ChronoUnit.MILLIS);
        }

        private LocalTime arrivalTime(Scenario scenario) {
            final var zonedDateTime = timeFromCron(scenario.getCron());
            if (zonedDateTime == null) return null;
            return plusAverageExecutionTime(scenario.getId(), zonedDateTime);
        }

        private LocalTime timeFromCron(String cron) {
            try {
                final var nextFireTime = new CronExpression(cron).getNextValidTimeAfter(new Date());
                if (nextFireTime == null) {
                    return null;
                }
                return nextFireTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            } catch (ParseException e) {
                log.warn("invalid cron expression: {}", cron, e);
                return null;
            }
        }
    }

    private static String timeText(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
