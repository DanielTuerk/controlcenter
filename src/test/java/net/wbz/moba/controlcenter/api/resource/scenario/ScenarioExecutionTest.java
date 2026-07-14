package net.wbz.moba.controlcenter.api.resource.scenario;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.core.Vertx;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.api.BaseIt;
import net.wbz.moba.controlcenter.api.ItUtil;
import net.wbz.moba.controlcenter.service.bus.BusService;
import net.wbz.moba.controlcenter.service.scenario.ScenarioManager;
import net.wbz.moba.controlcenter.service.scenario.ScenarioService;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.RouteStateEvent;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStateEvent;
import net.wbz.moba.controlcenter.shared.train.TrainDrivingDirectionEvent;
import net.wbz.moba.controlcenter.shared.train.TrainDrivingLevelEvent;
import net.wbz.moba.controlcenter.shared.train.TrainLightStateEvent;
import net.wbz.moba.controlcenter.shared.viewer.TrainInBlockEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@QuarkusTest
public class ScenarioExecutionTest extends BaseIt {
    private static final Map<Integer, Set<ScenarioTestData.Scenario.Route.Block>> TRAIN_BLOCKS = new HashMap<>();

    @Inject
    Vertx vertx;

    @Inject
    BusService busService;

    @Inject
    ScenarioManager scenarioManager;

    @Inject
    ScenarioService scenarioService;

    @BeforeEach
    public void beforeEach() throws InterruptedException {
        ItUtil.setCurrentConstruction(40001);
        ItUtil.connectTestDevice();
        EVENT_RECEIVER.reset();
        ItUtil.enableRailvoltage(EVENT_RECEIVER);

        Thread.sleep(3000L);
        EVENT_RECEIVER.reset();
    }


    @AfterEach
    public void afterEach() {
        // remove the train from the block to ensure a clean state for the next test
        new HashMap<>(TRAIN_BLOCKS).forEach((trainAddress, blocks) ->
                new HashSet<>(blocks).forEach(block -> {
                    try {
                        trainLeaveBlock(trainAddress, block.address(), block.number(), block.trackPartId());
                    } catch (Exception e) {
                        log.error("Error during cleanup", e);
                    }
                }));

        TRAIN_BLOCKS.clear();

        stopScenario(ScenarioTestData.LEFT_TO_RIGHT.id());
        stopScenario(ScenarioTestData.RIGHT_TO_LEFT.id());
        stopScenario(ScenarioTestData.ONE_TO_ONE.id());
        stopScenario(ScenarioTestData.ONE_TO_FOUR.id());
        stopScenario(ScenarioTestData.ONE_TO_TWO_ROUNDTRIP.id());
    }

    @Test
    void testNoTrainInStartBlock() {
        final var scenario = ScenarioTestData.LEFT_TO_RIGHT;
        startScenario(scenario.id());

        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.RUNNING);

        // all skipped because no train in start block
        verifyRouteStateEvent(scenario.id(), scenario.routes().get(0).routeSequenceId(),
            Route.ROUTE_RUN_STATE.SKIPPED, "no train in start block");
        verifyRouteStateEvent(scenario.id(), scenario.routes().get(1).routeSequenceId(),
            Route.ROUTE_RUN_STATE.SKIPPED, "no train in start block");

        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.FAILED);
    }

    @Test
    void testStopRunningScenario() {
        // init
        var scenario = ScenarioTestData.LEFT_TO_RIGHT;
        final var firstRoute = scenario.routes().getFirst();
        final var firstBlock = firstRoute.blocks().getFirst();
        trainEnterBlock(scenario.train().address(), firstBlock.address(), firstBlock.number(), firstBlock.trackPartId());

        // start
        startScenario(scenario.id());
        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.RUNNING);

        verifyRouteStateEvent(scenario.id(), firstRoute.routeSequenceId(),
            Route.ROUTE_RUN_STATE.PREPARED);

        verifyRouteStateEvent(scenario.id(), firstRoute.routeSequenceId(),
            Route.ROUTE_RUN_STATE.RUNNING);

        verifyRouteStateEvent(scenario.id(), scenario.routes().get(1).routeSequenceId(),
                Route.ROUTE_RUN_STATE.RESERVED);

        verifyTrainSpeed(scenario.train().id(), scenario.drivingLevel());

        // train on its way to the next block
        trainLeaveBlock(scenario.train().address(), firstBlock.address(), firstBlock.number(), firstBlock.trackPartId());

        // stop
        stopScenario(scenario.id());

        verifyTrainSpeed(scenario.train().id(), 0);

        // reserved route canceled
        verifyRouteStateEvent(scenario.id(), scenario.routes().get(1).routeSequenceId(),
                Route.ROUTE_RUN_STATE.CANCELED);

        // current route canceled
        verifyRouteStateEvent(scenario.id(), firstRoute.routeSequenceId(),
                Route.ROUTE_RUN_STATE.CANCELED);

        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.STOPPED);
    }

    @Test
    void testRunScenario_blockBetweenOccupied() {
        final var scenario = ScenarioTestData.LEFT_TO_RIGHT;

        updateBlockState(60, 1, true);
        updateBlockState(70, 2, true);

        // place train in start block
        trainEnterBlock(scenario.train().address(), 50, 1, 20801);

        startScenario(scenario.id());

        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.RUNNING);
        verifyRouteStateEvent(scenario.id(), 6401L, Route.ROUTE_RUN_STATE.PREPARED);

        // free the next block
        updateBlockState(60, 1, false);

        verifyRouteStateEvent(scenario.id(), 6401L, Route.ROUTE_RUN_STATE.RUNNING);

        // train on its way to the next block
        verifyTrainSpeed(scenario.train().id(), scenario.drivingLevel());
        trainLeaveBlock(scenario.train().address(), 50, 1, 20801);
        trainEnterBlock(scenario.train().address(), 60, 1, 20806);

        verifyRouteStateEvent(scenario.id(), 6401L, Route.ROUTE_RUN_STATE.FINISHED);

        // train stopped in last block
        verifyTrainSpeed(scenario.train().id(), 0);

        // 2nd route
        verifyRouteStateEvent(scenario.id(), 6402L, Route.ROUTE_RUN_STATE.PREPARED);

        updateBlockState(70, 2, false);

        verifyRouteStateEvent(scenario.id(), 6402L, Route.ROUTE_RUN_STATE.RUNNING);

        // train on its way to the next block
        verifyTrainSpeed(scenario.train().id(), scenario.drivingLevel());
        trainLeaveBlock(scenario.train().address(), 60, 1, 20806);
        trainEnterBlock(scenario.train().address(), 70, 2, 20816);

        verifyRouteStateEvent(scenario.id(), 6402L, Route.ROUTE_RUN_STATE.FINISHED);

        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.SUCCESS);
    }

    /**
     * A and C starts a scenario, A will drive to B and wait for clear C.
     * C starts at same time and drive to D. D will be reached after B was reached.
     * <pre>
     *   /-D-\
     * A---B---C
     * </pre>
     */
    @Test
    void testRunScenario_twoScenariosRunningOnSameRoute() {
        final var firstScenario = ScenarioTestData.LEFT_TO_RIGHT;
        final var secondScenario = ScenarioTestData.RIGHT_TO_LEFT;

        // place trains in start blocks (A and C)
        trainEnterBlock(firstScenario.train().address(), 50, 1, 20801);
        trainEnterBlock(secondScenario.train().address(), 70, 2, 20816);

        // start from A
        startScenario(firstScenario.id());
        verifyScenarioStateEvent(firstScenario.id(), Scenario.RUN_STATE.RUNNING);
        verifyRouteStateEvent(firstScenario.id(), 6401L, Route.ROUTE_RUN_STATE.PREPARED);
        verifyRouteStateEvent(firstScenario.id(), 6401L, Route.ROUTE_RUN_STATE.RUNNING);
        verifyTrainSpeed(firstScenario.train().id(), firstScenario.drivingLevel());

        // start from C
        startScenario(secondScenario.id());
        verifyScenarioStateEvent(secondScenario.id(), Scenario.RUN_STATE.RUNNING);
        verifyRouteStateEvent(secondScenario.id(), 3201L, Route.ROUTE_RUN_STATE.PREPARED);
        verifyRouteStateEvent(secondScenario.id(), 3201L, Route.ROUTE_RUN_STATE.RUNNING);
        verifyTrainSpeed(secondScenario.train().id(), secondScenario.drivingLevel());

        trainLeaveBlock(secondScenario.train().address(), 70, 2, 20816);

        // train on its way to B
        trainLeaveBlock(firstScenario.train().address(), 50, 1, 20801);
        // train on B
        trainEnterBlock(firstScenario.train().address(), 60, 1, 20806);
        verifyRouteStateEvent(firstScenario.id(), 6401L, Route.ROUTE_RUN_STATE.FINISHED);
        verifyTrainSpeed(firstScenario.train().id(), 0);
        // waiting for blocked route
        verifyRouteStateEvent(firstScenario.id(), 6402L, Route.ROUTE_RUN_STATE.PREPARED);

        // train from C arrives at D
        trainEnterBlock(secondScenario.train().address(), 60, 2, 20805);
        verifyRouteStateEvent(secondScenario.id(), 3201L, Route.ROUTE_RUN_STATE.FINISHED);
        // train 2 stops, track is clear, but was no block before the end block to reserve the next route
        verifyTrainSpeed(secondScenario.train().id(), 0);

        verifyRouteStateEvent(secondScenario.id(), 4801L, Route.ROUTE_RUN_STATE.PREPARED);
        verifyRouteStateEvent(secondScenario.id(), 4801L, Route.ROUTE_RUN_STATE.RUNNING);
        // train 2 continue
        verifyTrainSpeed(secondScenario.train().id(), secondScenario.drivingLevel());

        verifyRouteStateEvent(firstScenario.id(), 6402L, Route.ROUTE_RUN_STATE.RUNNING);
        verifyTrainSpeed(firstScenario.train().id(), firstScenario.drivingLevel());

        // train1 in end block
        trainEnterBlock(firstScenario.train().address(), 70, 2, 20816);

        verifyRouteStateEvent(firstScenario.id(), 6402L, Route.ROUTE_RUN_STATE.FINISHED);
        verifyScenarioStateEvent(firstScenario.id(), Scenario.RUN_STATE.SUCCESS);
        verifyTrainSpeed(firstScenario.train().id(), 0);

        // train on its way to A
        trainLeaveBlock(secondScenario.train().address(), 60, 2, 20805);
        trainEnterBlock(secondScenario.train().address(), 50, 1, 20801);

        // train arrived in A
        verifyRouteStateEvent(secondScenario.id(), 4801L, Route.ROUTE_RUN_STATE.FINISHED);
        verifyTrainSpeed(secondScenario.train().id(), 0);

        verifyScenarioStateEvent(secondScenario.id(), Scenario.RUN_STATE.SUCCESS);
    }

    @Test
    void testRunScenario_OneToFour() {
        runScenario(ScenarioTestData.ONE_TO_FOUR);
    }

    @Test
    void testRunScenario_OneToOne() {
        runScenario(ScenarioTestData.ONE_TO_ONE);
    }

    @Test
    void testRunScenario_OneToTwoRoundtrip() {
        runScenario(ScenarioTestData.ONE_TO_TWO_ROUNDTRIP);
    }

    @Test
    void testRunScenario_RightToLeft() {
        runScenario(ScenarioTestData.RIGHT_TO_LEFT);
    }

    @Test
    void testRunScenario_LeftToRight() {
        runScenario(ScenarioTestData.LEFT_TO_RIGHT);
    }

    private void runScenario(ScenarioTestData.Scenario scenario) {
        // place train in start block
        final var firstBlock = scenario.routes().getFirst().blocks().getFirst();
        trainEnterBlock(scenario.train().address(), firstBlock.address(), firstBlock.number(), firstBlock.trackPartId());

        startScenario(scenario.id());

        verifyTrainLight(scenario.train().id());
        if (scenario.drivingDirection() == TrainDrivingDirectionEvent.DRIVING_DIRECTION.FORWARD) {
            verifyTrainDrivingDirection(scenario.train().id(), scenario.drivingDirection());
        }

        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.RUNNING);

        List<ScenarioTestData.Scenario.Route> routes = scenario.routes();
        for (int i = 0; i < routes.size(); i++) {
            final ScenarioTestData.Scenario.Route route = routes.get(i);

            verifyRouteStateEvent(scenario.id(), route.routeSequenceId(),
                Route.ROUTE_RUN_STATE.PREPARED);

            verifyRouteStateEvent(scenario.id(), route.routeSequenceId(),
                Route.ROUTE_RUN_STATE.RUNNING);

            final var isFirst = i == 0;
            if (isFirst) {
                verifyTrainSpeed(scenario.train().id(), scenario.drivingLevel());
            }

            var blocks = route.blocks();
            for (int j = 0; j < blocks.size() - 1; j++) {
                var block = blocks.get(j);

                // train on its way to the next block
                trainLeaveBlock(scenario.train().address(), block.address(), block.number(), block.trackPartId());

                if (i + 1 < routes.size() && j == 0) {
                    // check that next route is reserved
                    verifyRouteStateEvent(scenario.id(), routes.get(i + 1).routeSequenceId(),
                        Route.ROUTE_RUN_STATE.RESERVED);
                }

                if (j + 1 < blocks.size()) {
                    // train arrived in next block
                    var nextBlock = blocks.get(j + 1);
                    trainEnterBlock(scenario.train().address(), nextBlock.address(), nextBlock.number(), nextBlock.trackPartId());
                }
            }

            verifyRouteStateEvent(scenario.id(), route.routeSequenceId(),
                Route.ROUTE_RUN_STATE.FINISHED);
        }

        // train stopped in last block
        verifyTrainSpeed(scenario.train().id(), 0);

        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.SUCCESS);
    }

    private void trainEnterBlock(int trainAddress, int blockAddress, int blockNumber, long trackPartId) {
        if (!TRAIN_BLOCKS.containsKey(trainAddress)) {
            TRAIN_BLOCKS.put(trainAddress, new HashSet<>());
        }
        TRAIN_BLOCKS.get(trainAddress).add(new ScenarioTestData.Scenario.Route.Block(trackPartId, blockAddress, blockNumber));
        placeTrainInBlock(trainAddress, blockAddress, blockNumber, true, trackPartId);
    }

    private void trainLeaveBlock(int trainAddress, int blockAddress, int blockNumber, long trackPartId) {
        TRAIN_BLOCKS.get(trainAddress).removeIf(x -> x.trackPartId() == trackPartId && x.address() == blockAddress && x.number() == blockNumber);
        placeTrainInBlock(trainAddress, blockAddress, blockNumber, false, trackPartId);
    }

    private void placeTrainInBlock(int trainAddress, int blockAddress, int blockNumber, boolean enter, long trackPartId) {

        updateBlockState(blockAddress, blockNumber, enter);

        // block number
        final var bigInteger = BigInteger.valueOf(blockNumber - 1)
            // driving direction
            .setBit(4);
        final var directionValue = enter ? bigInteger.setBit(3).intValue() : bigInteger.intValue();
        // send on the Vert.x event-loop thread: the real Selectrix device delivers bus feedback there too,
        // and only there (not on a @Blocking worker thread) does a blocking call further down the reactive
        // route-execution chain trip Quarkus' BlockingOperationNotAllowedException
        runOnEventLoop(() -> busService.sendBusData(1, blockAddress + 1, directionValue));
        runOnEventLoop(() -> busService.sendBusData(1, blockAddress + 2, trainAddress));



        final var trainInBlockEvent = EVENT_RECEIVER.catchEvent(TrainInBlockEvent.class);
        assertEquals(trackPartId, trainInBlockEvent.trackPartId());
        assertEquals(trainAddress, trainInBlockEvent.trainAddress());
        assertEquals(enter, trainInBlockEvent.enter());
    }

    private void updateBlockState(int blockAddress, int blockNumber, boolean occupied) {
        final var currentBlockValue = BigInteger.valueOf(ItUtil.fetchBusData(1, blockAddress));
        final var newBlockValue = occupied ? currentBlockValue.setBit(blockNumber - 1).intValue() :
                currentBlockValue.clearBit(blockNumber - 1).intValue();
        // see comment in placeTrainInBlock: must run on the event-loop thread to match production threading
        runOnEventLoop(() -> busService.sendBusData(1, blockAddress, newBlockValue));
    }

    /**
     * Runs the given action on the Vert.x event-loop thread and blocks until it completes, propagating any
     * exception (e.g. {@code BlockingOperationNotAllowedException} from a reactive chain triggered synchronously
     * by the action) back to the calling test thread.
     */
    private void runOnEventLoop(Runnable action) {
        final var result = new CompletableFuture<Void>();
        vertx.runOnContext(v -> {
            try {
                action.run();
                result.complete(null);
            } catch (Throwable t) {
                result.completeExceptionally(t);
            }
        });
        try {
            result.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException(e.getCause());
        } catch (InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyTrainSpeed(int trainId, int expected) {
        final var trainDrivingLevelEvent = EVENT_RECEIVER.catchEvent(TrainDrivingLevelEvent.class, s -> s.contains("\"itemId\":%d".formatted(trainId)));
        assertEquals(trainId, trainDrivingLevelEvent.getItemId());
        assertEquals(expected, trainDrivingLevelEvent.getSpeed(), "wrong speed for train: %s".formatted(trainId));
    }

    private void verifyTrainLight(int trainId) {
        final var trainDrivingLevelEvent = EVENT_RECEIVER.catchEvent(TrainLightStateEvent.class, s -> s.contains("{\"itemId\":%d".formatted(trainId)));
        assertEquals(trainId, trainDrivingLevelEvent.getItemId());
        assertTrue(trainDrivingLevelEvent.isState(), "wrong light state for train: %s".formatted(trainId));
    }

    private void verifyTrainDrivingDirection(int trainId, TrainDrivingDirectionEvent.DRIVING_DIRECTION expected) {
        final var trainDrivingLevelEvent = EVENT_RECEIVER.catchEvent(TrainDrivingDirectionEvent.class, s -> s.contains("{\"itemId\":%d".formatted(trainId)));
        assertEquals(trainId, trainDrivingLevelEvent.getItemId());
        assertEquals(expected, trainDrivingLevelEvent.getDirection(), "wrong direction of train: %s".formatted(trainId));
    }

    private static void startScenario(long scenarioId) {
        given()
            .contentType(ContentType.JSON)
            .when()
            .post("/api/scenarios/%d/start".formatted(scenarioId))
            .then()
            .statusCode(200);
    }

    private void stopScenario(long scenarioId) {
        // Stopping cancels the running route execution Uni, whose .onCancellation() handler calls
        // stopTrain(...) synchronously on whatever thread calls cancel(). Going through the @Blocking
        // REST endpoint (as ItUtil would) forces that onto a worker thread and hides the bug where
        // production cancels from the real (Vert.x event-loop) device-feedback thread instead - so we
        // trigger the cancellation directly on the event loop here.
        // (fetching the scenario itself is a blocking cached DB lookup, so that stays on this thread)
        scenarioManager.getScenarioById(scenarioId)
                .ifPresent(scenario -> runOnEventLoop(() -> scenarioService.stop(scenario)));
    }

    private void verifyScenarioStateEvent(long scenarioId, Scenario.RUN_STATE runState) {
        final var scenarioStateEvent = EVENT_RECEIVER.catchEvent(ScenarioStateEvent.class);
        assertEquals(scenarioId, scenarioStateEvent.itemId);
        assertEquals(runState, scenarioStateEvent.state);
    }

    private void verifyRouteStateEvent(long scenarioId, long routeSequenceId, Route.ROUTE_RUN_STATE runState) {
        verifyRouteStateEvent(scenarioId, routeSequenceId, runState, null);
    }

    private void verifyRouteStateEvent(long scenarioId, long routeSequenceId, Route.ROUTE_RUN_STATE runState, String message) {
        final var scenarioStateEvent = EVENT_RECEIVER.catchEvent(RouteStateEvent.class, s -> s.contains("\"scenarioId\":%d".formatted(scenarioId)));
        assertEquals(scenarioId, scenarioStateEvent.scenarioId(),
                "scenario not equal for route state event with state: %s".formatted(runState));
        assertEquals(routeSequenceId, scenarioStateEvent.routeSequenceId(),
                "route sequence id not equal for route state event with state: %s".formatted(runState));
        assertEquals(runState, scenarioStateEvent.state(),
                "run state not equal for route state event");
        if (message != null) {
            assertEquals(message, scenarioStateEvent.message(),
                    "message not equal for route state event with state: %s".formatted(runState));
        }
    }
}
