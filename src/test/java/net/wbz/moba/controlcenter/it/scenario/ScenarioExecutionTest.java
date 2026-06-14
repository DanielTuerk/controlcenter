package net.wbz.moba.controlcenter.it.scenario;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.tuples.Tuple2;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.it.BaseIt;
import net.wbz.moba.controlcenter.it.ItUtil;
import net.wbz.moba.controlcenter.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.RouteStateEvent;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStateEvent;
import net.wbz.moba.controlcenter.shared.train.TrainDrivingDirectionEvent;
import net.wbz.moba.controlcenter.shared.train.TrainDrivingLevelEvent;
import net.wbz.moba.controlcenter.shared.train.TrainLightStateEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@QuarkusTest
public class ScenarioExecutionTest extends BaseIt {
    private static final Map<Integer, Set<Tuple2<Integer, Integer>>> TRAIN_BLOCKS = new HashMap<>();

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
        new HashMap<>(TRAIN_BLOCKS).forEach((trainAddress, addressTuple) ->
            new ArrayList<>(addressTuple).forEach(tuple -> {
                try {
                    trainLeaveBlock(trainAddress, tuple.getItem1(), tuple.getItem2());
                } catch (Exception e) {
                    log.error("Error during cleanup", e);
                }
            })
        );
        TRAIN_BLOCKS.clear();

        stopScenario(ScenarioTestData.LEFT_TO_RIGHT.id());
        stopScenario(ScenarioTestData.RIGHT_TO_LEFT.id());
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
        trainEnterBlock(scenario.train().address(), firstBlock.address(), firstBlock.number());

        // start
        startScenario(scenario.id());
        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.RUNNING);

        verifyRouteStateEvent(scenario.id(), firstRoute.routeSequenceId(),
            Route.ROUTE_RUN_STATE.PREPARED);

        verifyRouteStateEvent(scenario.id(), firstRoute.routeSequenceId(),
            Route.ROUTE_RUN_STATE.RESERVED);

        verifyRouteStateEvent(scenario.id(), firstRoute.routeSequenceId(),
            Route.ROUTE_RUN_STATE.RUNNING);

        verifyTrainSpeed(scenario.train().id(), scenario.drivingLevel());

        // train on its way to the next block
        trainLeaveBlock(scenario.train().address(), firstBlock.address(), firstBlock.number());

        // reserve next route for free track after exiting the start block
        verifyRouteStateEvent(scenario.id(), scenario.routes().get(1).routeSequenceId(),
            Route.ROUTE_RUN_STATE.RESERVED);

        // stop
        stopScenario(scenario.id());

        verifyTrainSpeed(scenario.train().id(), 0);
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
        trainEnterBlock(scenario.train().address(), 50, 1);

        startScenario(scenario.id());

        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.RUNNING);
        verifyRouteStateEvent(scenario.id(), 6401L, Route.ROUTE_RUN_STATE.PREPARED);

        // free the next block
        updateBlockState(60, 1, false);

        verifyRouteStateEvent(scenario.id(), 6401L, Route.ROUTE_RUN_STATE.RESERVED);
        verifyRouteStateEvent(scenario.id(), 6401L, Route.ROUTE_RUN_STATE.RUNNING);

        // train on its way to the next block
        verifyTrainSpeed(scenario.train().id(), scenario.drivingLevel());
        trainLeaveBlock(scenario.train().address(), 50, 1);
        trainEnterBlock(scenario.train().address(), 60, 1);

        verifyRouteStateEvent(scenario.id(), 6401L, Route.ROUTE_RUN_STATE.FINISHED);

        // train stopped in last block
        verifyTrainSpeed(scenario.train().id(), 0);

        // 2nd route
        verifyRouteStateEvent(scenario.id(), 6402L, Route.ROUTE_RUN_STATE.PREPARED);

        updateBlockState(70, 2, false);

        verifyRouteStateEvent(scenario.id(), 6402L, Route.ROUTE_RUN_STATE.RESERVED);
        verifyRouteStateEvent(scenario.id(), 6402L, Route.ROUTE_RUN_STATE.RUNNING);

        // train on its way to the next block
        verifyTrainSpeed(scenario.train().id(), scenario.drivingLevel());
        trainLeaveBlock(scenario.train().address(), 60, 1);
        trainEnterBlock(scenario.train().address(), 70, 2);

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
        trainEnterBlock(firstScenario.train().address(), 50, 1);
        trainEnterBlock(secondScenario.train().address(), 70, 2);

        // start from A
        startScenario(firstScenario.id());
        verifyScenarioStateEvent(firstScenario.id(), Scenario.RUN_STATE.RUNNING);
        verifyRouteStateEvent(firstScenario.id(), 6401L, Route.ROUTE_RUN_STATE.PREPARED);
        verifyRouteStateEvent(firstScenario.id(), 6401L, Route.ROUTE_RUN_STATE.RESERVED);
        verifyRouteStateEvent(firstScenario.id(), 6401L, Route.ROUTE_RUN_STATE.RUNNING);
        verifyTrainSpeed(firstScenario.train().id(), firstScenario.drivingLevel());

        // start from C
        startScenario(secondScenario.id());
        verifyScenarioStateEvent(secondScenario.id(), Scenario.RUN_STATE.RUNNING);
        verifyRouteStateEvent(secondScenario.id(), 3201L, Route.ROUTE_RUN_STATE.PREPARED);
        verifyRouteStateEvent(secondScenario.id(), 3201L, Route.ROUTE_RUN_STATE.RESERVED);
        verifyRouteStateEvent(secondScenario.id(), 3201L, Route.ROUTE_RUN_STATE.RUNNING);
        verifyTrainSpeed(secondScenario.train().id(), secondScenario.drivingLevel());

        trainLeaveBlock(secondScenario.train().address(), 70, 2);

        // train on its way to B
        trainLeaveBlock(firstScenario.train().address(), 50, 1);
        // train on B
        trainEnterBlock(firstScenario.train().address(), 60, 1);
        verifyRouteStateEvent(firstScenario.id(), 6401L, Route.ROUTE_RUN_STATE.FINISHED);
        verifyTrainSpeed(firstScenario.train().id(), 0);
        // waiting for blocked route
        verifyRouteStateEvent(firstScenario.id(), 6402L, Route.ROUTE_RUN_STATE.PREPARED);

        // train from C arrives at D
        trainEnterBlock(secondScenario.train().address(), 60, 2);
        verifyRouteStateEvent(secondScenario.id(), 3201L, Route.ROUTE_RUN_STATE.FINISHED);
        // train 2 stops, track is clear, but was no block before the end block to reserve the next route
        verifyTrainSpeed(secondScenario.train().id(), 0);

        verifyRouteStateEvent(secondScenario.id(), 4801L, Route.ROUTE_RUN_STATE.PREPARED);
        verifyRouteStateEvent(secondScenario.id(), 4801L, Route.ROUTE_RUN_STATE.RESERVED);
        verifyRouteStateEvent(secondScenario.id(), 4801L, Route.ROUTE_RUN_STATE.RUNNING);
        // train 2 continue
        verifyTrainSpeed(secondScenario.train().id(), secondScenario.drivingLevel());

        verifyRouteStateEvent(firstScenario.id(), 6402L, Route.ROUTE_RUN_STATE.RESERVED);
        verifyRouteStateEvent(firstScenario.id(), 6402L, Route.ROUTE_RUN_STATE.RUNNING);
        verifyTrainSpeed(firstScenario.train().id(), firstScenario.drivingLevel());

        // train1 in end block
        trainEnterBlock(firstScenario.train().address(), 70, 2);

        verifyRouteStateEvent(firstScenario.id(), 6402L, Route.ROUTE_RUN_STATE.FINISHED);
        verifyScenarioStateEvent(firstScenario.id(), Scenario.RUN_STATE.SUCCESS);
        verifyTrainSpeed(firstScenario.train().id(), 0);

        // train on its way to A
        trainLeaveBlock(secondScenario.train().address(), 60, 2);
        trainEnterBlock(secondScenario.train().address(), 50, 1);

        // train arrived in A
        verifyRouteStateEvent(secondScenario.id(), 4801L, Route.ROUTE_RUN_STATE.FINISHED);
        verifyTrainSpeed(secondScenario.train().id(), 0);

        verifyScenarioStateEvent(secondScenario.id(), Scenario.RUN_STATE.SUCCESS);
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
        trainEnterBlock(scenario.train().address(), firstBlock.address(), firstBlock.number());

        startScenario(scenario.id());

        verifyTrainLight(scenario.train().id(), true);
        if (scenario.drivingDirection() == TrainDrivingDirectionEvent.DRIVING_DIRECTION.FORWARD) {
            verifyTrainDrivingDirection(scenario.train().id(), scenario.drivingDirection());
        }

        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.RUNNING);

        List<ScenarioTestData.Scenario.Route> routes = scenario.routes();
        for (int i = 0; i < routes.size(); i++) {
            final ScenarioTestData.Scenario.Route route = routes.get(i);

            verifyRouteStateEvent(scenario.id(), route.routeSequenceId(),
                Route.ROUTE_RUN_STATE.PREPARED);

            final var isFirst = i == 0;
            if (isFirst) {
                verifyRouteStateEvent(scenario.id(), route.routeSequenceId(),
                    Route.ROUTE_RUN_STATE.RESERVED);
            }

            verifyRouteStateEvent(scenario.id(), route.routeSequenceId(),
                Route.ROUTE_RUN_STATE.RUNNING);

            if (isFirst) {
                verifyTrainSpeed(scenario.train().id(), scenario.drivingLevel());
            }

            var blocks = route.blocks();
            for (int j = 0; j < blocks.size() - 1; j++) {
                var block = blocks.get(j);

                // train on its way to the next block
                trainLeaveBlock(scenario.train().address(), block.address(), block.number());

                if (i + 1 < routes.size() && j == 0) {
                    // check that next route is reserved
                    verifyRouteStateEvent(scenario.id(), routes.get(i + 1).routeSequenceId(),
                        Route.ROUTE_RUN_STATE.RESERVED);
                }

                if (j + 1 < blocks.size()) {
                    // train arrived in next block
                    var nextBlock = blocks.get(j + 1);
                    trainEnterBlock(scenario.train().address(), nextBlock.address(), nextBlock.number());
                }
            }

            verifyRouteStateEvent(scenario.id(), route.routeSequenceId(),
                Route.ROUTE_RUN_STATE.FINISHED);
        }

        // train stopped in last block
        verifyTrainSpeed(scenario.train().id(), 0);

        verifyScenarioStateEvent(scenario.id(), Scenario.RUN_STATE.SUCCESS);
    }

    private void trainEnterBlock(int trainAddress, int blockAddress, int blockNumber) {
        if (!TRAIN_BLOCKS.containsKey(trainAddress)) {
            TRAIN_BLOCKS.put(trainAddress, new HashSet<>());
        }
        TRAIN_BLOCKS.get(trainAddress).add(Tuple2.of(blockAddress, blockNumber));
        placeTrainInBlock(trainAddress, blockAddress, blockNumber, FeedbackBlockEvent.STATE.ENTER);
    }

    private void trainLeaveBlock(int trainAddress, int blockAddress, int blockNumber) {
        TRAIN_BLOCKS.get(trainAddress).remove(Tuple2.of(blockAddress, blockNumber));
        placeTrainInBlock(trainAddress, blockAddress, blockNumber, FeedbackBlockEvent.STATE.EXIT);
    }

    private void placeTrainInBlock(int trainAddress, int blockAddress, int blockNumber, FeedbackBlockEvent.STATE state) {
        updateBlockState(blockAddress, blockNumber, state == FeedbackBlockEvent.STATE.EXIT);

        // block number
        final var bigInteger = BigInteger.valueOf(blockNumber - 1)
            // driving direction
            .setBit(4);
        ItUtil.sendBusData(1, blockAddress + 1, state == FeedbackBlockEvent.STATE.ENTER
            ? bigInteger.setBit(3).intValue() : bigInteger.intValue());
        ItUtil.sendBusData(1, blockAddress + 2, trainAddress);

        final var feedbackBlockEvent = EVENT_RECEIVER.catchEvent(FeedbackBlockEvent.class);
        assertEquals(trainAddress, feedbackBlockEvent.getTrain());
        assertEquals(state, feedbackBlockEvent.getState());
        assertEquals(blockAddress, feedbackBlockEvent.getAddress());
        assertEquals(blockNumber, feedbackBlockEvent.getBlock());
    }

    private void updateBlockState(int blockAddress, int blockNumber, boolean free) {
        final var currentBlockValue = BigInteger.valueOf(ItUtil.fetchBusData(1, blockAddress));
        ItUtil.sendBusData(1, blockAddress, free ? currentBlockValue.clearBit(blockNumber - 1).intValue() :
            currentBlockValue.setBit(blockNumber - 1).intValue()
        );
    }

    private void verifyTrainSpeed(int trainId, int expected) {
        final var trainDrivingLevelEvent = EVENT_RECEIVER.catchEvent(TrainDrivingLevelEvent.class, s -> s.contains("\"itemId\":%d".formatted(trainId)));
        assertEquals(trainId, trainDrivingLevelEvent.getItemId());
        assertEquals(expected, trainDrivingLevelEvent.getSpeed(), "wrong speed for train: %s".formatted(trainId));
    }

    private void verifyTrainLight(int trainId, boolean expected) {
        final var trainDrivingLevelEvent = EVENT_RECEIVER.catchEvent(TrainLightStateEvent.class, "{\"itemId\":%d".formatted(trainId));
        assertEquals(trainId, trainDrivingLevelEvent.getItemId());
        assertEquals(expected, trainDrivingLevelEvent.isState(), "wrong light state for train: %s".formatted(trainId));
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

    private static void stopScenario(long scenarioId) {
        given()
            .contentType(ContentType.JSON)
            .when()
            .post("/api/scenarios/%d/stop".formatted(scenarioId))
            .then()
            .statusCode(200);
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
        assertEquals(scenarioId, scenarioStateEvent.getScenarioId());
        assertEquals(routeSequenceId, scenarioStateEvent.getRouteSequenceId());
        assertEquals(runState, scenarioStateEvent.getState());
        if (message != null) {
            assertEquals(message, scenarioStateEvent.getMessage());
        }
    }
}
