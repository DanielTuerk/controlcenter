package io.github.danieltuerk.controlcenter.service.station;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.service.constrution.ConstructionService;
import io.github.danieltuerk.controlcenter.service.scenario.ScenarioManager;
import io.github.danieltuerk.controlcenter.service.scenario.statistic.ScenarioStatisticManager;
import io.github.danieltuerk.controlcenter.service.track.TrackDataProvider;
import io.github.danieltuerk.controlcenter.shared.constrution.Construction;
import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import io.github.danieltuerk.controlcenter.shared.scenario.RouteStateEvent;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioScheduleEvent;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioStateEvent;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenariosChangedEvent;
import io.github.danieltuerk.controlcenter.shared.station.Station;
import io.github.danieltuerk.controlcenter.shared.station.StationBoardArrivalEvent;
import io.github.danieltuerk.controlcenter.shared.station.StationBoardDepartureEvent;
import io.github.danieltuerk.controlcenter.shared.station.StationBoardEvent;
import io.github.danieltuerk.controlcenter.shared.station.StationDataChangedEvent;
import io.github.danieltuerk.controlcenter.shared.station.StationPlatform;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class StationBoardService {

    private static final String INFO_TEXT_TRAIN_CANCELLED = "Train cancelled";
    private final Map<Long, LocalTime> scenarioStartTimes = new ConcurrentHashMap<>();
    private final Map<Long, ScenarioPlatform> scenarioIdToPlatform = new ConcurrentHashMap<>();

    public record ScenarioPlatform(Set<StationPlatform> departurePlatforms,
                                   Set<StationPlatform> arrivalPlatforms) {

    }

    private final ScenarioManager scenarioManager;
    private final TrackDataProvider trackDataProvider;
    private final ConstructionService constructionService;
    private final StationManager stationManager;
    private final EventBroadcaster eventBroadcaster;
    private final StationBoardEventFactory<StationBoardDepartureEvent> departureEventFactory;
    private final StationBoardEventFactory<StationBoardArrivalEvent> arrivalEventFactory;

    public StationBoardService(ScenarioManager scenarioManager, TrackDataProvider trackDataProvider,
                               ConstructionService constructionService, StationManager stationManager,
                               EventBroadcaster eventBroadcaster, ScenarioStatisticManager scenarioStatisticManager) {
        this.scenarioManager = scenarioManager;
        this.trackDataProvider = trackDataProvider;
        this.constructionService = constructionService;
        this.stationManager = stationManager;
        this.eventBroadcaster = eventBroadcaster;
        this.departureEventFactory = new StationBoardEventFactory.DepartureEventFactory();
        this.arrivalEventFactory = new StationBoardEventFactory.ArrivalEventFactory(scenarioStatisticManager);
    }

    public void onScenariosChangedEventChanged(@Observes ScenariosChangedEvent event) {
        init();
    }

    public void onStationDataChangedEventChanged(@Observes StationDataChangedEvent event) {
        init();
    }

    private void init() {
        final var scenarios = scenarioManager.getScenarios();
        final var stations = stationManager.load();

        scenarios.forEach(scenario -> {
            final var start = scenario.getRouteSequences().getFirst().getRoute().getStart();
            final var end = trackDataProvider.findBlockStraightOfTrackBlock(
                            constructionService.getCurrentConstruction()
                                    .map(Construction::getId)
                                    .orElseThrow(() -> new IllegalArgumentException("no current construction")),
                            scenario.getRouteSequences().getLast().getRoute().getEnd())
                    .orElseThrow(() -> new IllegalArgumentException("no end for route"));

            var departurePlatforms = stations.stream()
                    .flatMap(station -> station.getPlatforms().stream())
                    .filter(stationPlatform -> stationPlatform.getBlockStraights().contains(start))
                    .collect(Collectors.toSet());

            var arrivalPlatforms = stations.stream()
                    .flatMap(station -> station.getPlatforms().stream())
                    .filter(stationPlatform -> stationPlatform.getBlockStraights().contains(end))
                    .collect(Collectors.toSet());

            scenarioIdToPlatform.put(scenario.getId(), new ScenarioPlatform(departurePlatforms, arrivalPlatforms));
        });
    }

    public void onScenarioScheduleEventChanged(@Observes ScenarioScheduleEvent event) {
        if (!event.on()) {
            final var scenarioId = event.scenarioId();
            final var scenarioById = scenarioManager.getScenarioById(scenarioId)
                    .orElseThrow(() -> new IllegalArgumentException("no scenario for id: " + scenarioId));
            final var train = scenarioById.getTrain().getName();
            final var scheduledStartTime = timeFromCron(scenarioById.getCron());

            fireStationBoardEvents(scenarioById, scenarioIdToPlatform.get(scenarioId).departurePlatforms(),
                    train, scheduledStartTime, arrivalStationsOfScenario(scenarioById), departureEventFactory, INFO_TEXT_TRAIN_CANCELLED);

            fireStationBoardEvents(scenarioById, scenarioIdToPlatform.get(scenarioId).arrivalPlatforms(),
                    train, scheduledStartTime, departureStationsOfScenario(scenarioById), arrivalEventFactory, INFO_TEXT_TRAIN_CANCELLED);
        }
    }

    private String arrivalStationsOfScenario(Scenario scenario) {
        final var end = trackDataProvider.findBlockStraightOfTrackBlock(
                        constructionService.getCurrentConstruction()
                                .map(Construction::getId)
                                .orElseThrow(() -> new IllegalArgumentException("no current construction")),
                        scenario.getRouteSequences().getLast().getRoute().getEnd())
                .orElseThrow(() -> new IllegalArgumentException("no end for route"));

        return stationManager.load().stream()
                .flatMap(station -> station.getPlatforms().stream())
                .filter(stationPlatform -> stationPlatform.getBlockStraights().contains(end))
                .flatMap(x -> stationManager.getByPlatform(x).stream()
                        .map(Station::getName))
                .collect(Collectors.joining(", "));
    }

    private String departureStationsOfScenario(Scenario scenario) {
        final var start = scenario.getRouteSequences().getFirst().getRoute().getStart();
        return stationManager.load().stream()
                .filter(station -> station.getPlatforms().stream()
                        .anyMatch(platform -> platform.getBlockStraights().contains(start)))
                .map(Station::getName)
                .collect(Collectors.joining(", "));
    }

    public void onScenarioStateEventChanged(@Observes ScenarioStateEvent event) {
        if (!scenarioIdToPlatform.containsKey(event.getItemId())) return;

        final var scenarioById = scenarioManager.getScenarioById(event.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("no scenario for id: " + event.getItemId()));
        final var train = scenarioById.getTrain().getName();

        switch (event.getState()) {
            case NONE, PAUSED -> {
            }
            case SCHEDULED -> {
                final var scheduledStartTime = timeFromCron(scenarioById.getCron());
                scenarioStartTimes.put(scenarioById.getId(), scheduledStartTime);

                fireStationBoardEvents(scenarioById, scenarioIdToPlatform.get(event.itemId).departurePlatforms(),
                        train, scheduledStartTime, arrivalStationsOfScenario(scenarioById), departureEventFactory, "");
                fireStationBoardEvents(scenarioById, scenarioIdToPlatform.get(event.itemId).arrivalPlatforms(),
                        train, scheduledStartTime, departureStationsOfScenario(scenarioById), arrivalEventFactory, "");
            }
            case RUNNING -> {
                if (!scenarioStartTimes.containsKey(scenarioById.getId())) {
                    scenarioStartTimes.put(scenarioById.getId(), LocalTime.now());
                }

                var startTime = scenarioStartTimes.get(scenarioById.getId());

                fireStationBoardEvents(scenarioById, scenarioIdToPlatform.get(event.itemId).departurePlatforms(),
                        train, startTime, arrivalStationsOfScenario(scenarioById), departureEventFactory, informationByStart(startTime));
                fireStationBoardEvents(scenarioById, scenarioIdToPlatform.get(event.itemId).arrivalPlatforms(),
                        train, startTime, departureStationsOfScenario(scenarioById), arrivalEventFactory, informationByStart(startTime));
            }
            case STOPPED, FAILED -> {
                var startTime = scenarioStartTimes.get(scenarioById.getId());
                fireStationBoardEvents(scenarioById, scenarioIdToPlatform.get(event.itemId).departurePlatforms(),
                        train, startTime, arrivalStationsOfScenario(scenarioById), departureEventFactory, INFO_TEXT_TRAIN_CANCELLED);
                fireStationBoardEvents(scenarioById, scenarioIdToPlatform.get(event.itemId).arrivalPlatforms(),
                        train, startTime, departureStationsOfScenario(scenarioById), arrivalEventFactory, INFO_TEXT_TRAIN_CANCELLED);

                scenarioStartTimes.remove(scenarioById.getId());
            }
            case SUCCESS -> scenarioStartTimes.remove(scenarioById.getId());
        }
    }

    private String informationByStart(LocalTime startTime) {
        final var seconds = Duration.between(startTime, LocalTime.now()).getSeconds();
        if (seconds >= 60) {
            return "Delayed by " + (seconds % 60) + " min";
        }
        if (seconds > 30) {
            return "Delayed";
        } else return "";
    }

    public void onRouteStateEventChanged(@Observes RouteStateEvent event) {
        if (Objects.requireNonNull(event.state()) == Route.ROUTE_RUN_STATE.RUNNING) {
            // send station event for the arrival station by the running scenario by each route to get a delay calculated
            final var scenarioId = event.scenarioId();
            if (scenarioStartTimes.containsKey(scenarioId)) {
                final var startTime = scenarioStartTimes.get(scenarioId);
                scenarioManager.getScenarioById(scenarioId).ifPresent(scenario -> {
                    final var train = scenario.getTrain().getName();
                    fireStationBoardEvents(scenario, scenarioIdToPlatform.get(scenarioId).arrivalPlatforms(),
                            train, startTime, departureStationsOfScenario(scenario), arrivalEventFactory, informationByStart(startTime));
                });
            }
        }
    }

    private void fireStationBoardEvents(Scenario scenario, Set<StationPlatform> platforms, String train,
                                        LocalTime time, String targetStation,
                                        StationBoardEventFactory<? extends StationBoardEvent> eventFactory, String information) {
        platforms.forEach(platform ->
                stationManager.load().stream()
                        .filter(station -> station.getPlatforms().stream()
                                .anyMatch(p -> Objects.equals(p.getId(), platform.getId())))
                        .findFirst()
                        .ifPresent(station -> {
                            var event = eventFactory.create(scenario, station.getId(),
                                    platform.getName(), train, time, targetStation, information);
                            eventBroadcaster.fireEvent(event);
                        })
        );
    }

    private LocalTime timeFromCron(String cron) {
        try {
            final var nextFireTime = new CronExpression(cron).getNextValidTimeAfter(new Date());
            if (nextFireTime == null) {
                throw new IllegalStateException("cannot parse cron: " + cron);
            }
            return nextFireTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        } catch (ParseException e) {
            log.warn("invalid cron expression: {}", cron, e);
            throw new IllegalStateException("cannot parse cron: " + cron);
        }
    }

}
