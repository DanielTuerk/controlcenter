package net.wbz.moba.controlcenter.api.resource.scenario;

import net.wbz.moba.controlcenter.api.BaseTestData;
import net.wbz.moba.controlcenter.shared.train.TrainDrivingDirectionEvent;

import java.util.List;

import static net.wbz.moba.controlcenter.api.BaseTestData.TRAIN1;
import static net.wbz.moba.controlcenter.api.BaseTestData.TRAIN2;

public class ScenarioTestData {

    public static Scenario LEFT_TO_RIGHT = new Scenario(9601L,
        TRAIN1, 10, TrainDrivingDirectionEvent.DRIVING_DIRECTION.FORWARD,
        List.of(
            new Scenario.Route(6401L,
                List.of(
                    new Scenario.Route.Block(20801,50, 1),
                    new Scenario.Route.Block(20806,60, 1)
                )
            ),
            new Scenario.Route(6402L,
                List.of(
                    new Scenario.Route.Block(20806,60, 1),
                    new Scenario.Route.Block(20816,70, 2)
                )
            )
        )
    );

    public static Scenario RIGHT_TO_LEFT = new Scenario(9602L,
        TRAIN2, 12, TrainDrivingDirectionEvent.DRIVING_DIRECTION.BACKWARD,
        List.of(
            new Scenario.Route(3201L,
                List.of(
                    new Scenario.Route.Block(20816,70, 2),
                    new Scenario.Route.Block(20805,60, 2)
                )
            ),
            new Scenario.Route(4801L,
                List.of(
                    new Scenario.Route.Block(20805,60, 2),
                    new Scenario.Route.Block(20801,50, 1)
                )
            )
        )
    );


    public static Scenario ONE_TO_FOUR = new Scenario(77001L,
            TRAIN1, 5, TrainDrivingDirectionEvent.DRIVING_DIRECTION.FORWARD,
            List.of(
                    new Scenario.Route(7001L,
                            List.of(
                                    new Scenario.Route.Block(4801L,80, 1),
                                    new Scenario.Route.Block(4802L,80, 2)
                            )
                    ),
                    new Scenario.Route(7002L,
                            List.of(
                                    new Scenario.Route.Block(4802L,80, 2),
                                    new Scenario.Route.Block(4804L,90, 2)
                            )
                    ),
                    new Scenario.Route(7003L,
                            List.of(
                                    new Scenario.Route.Block(4804L,90, 2),
                                    new Scenario.Route.Block(4803L,90, 1)
                            )
                    )
            )
    );

    public static Scenario ONE_TO_ONE = new Scenario(77002,
            TRAIN1, 6, TrainDrivingDirectionEvent.DRIVING_DIRECTION.FORWARD,
            List.of(
                    new Scenario.Route(7201L,
                            List.of(
                                    new Scenario.Route.Block(4801L,80, 1),
                                    new Scenario.Route.Block(4802L,80, 2)
                            )
                    ),
                    new Scenario.Route(7202L,
                            List.of(
                                    new Scenario.Route.Block(4802L,80, 2),
                                    new Scenario.Route.Block(4804L,90, 2)
                            )
                    ),
                    new Scenario.Route(7203L,
                            List.of(
                                    new Scenario.Route.Block(4804L,90, 2),
                                    new Scenario.Route.Block(4803L,90, 1)
                            )
                    ),
                    new Scenario.Route(7204L,
                            List.of(
                                    new Scenario.Route.Block(4803L,90, 1),
                                    new Scenario.Route.Block(4801L,80, 1)
                            )
                    )
            )
    );

    public static Scenario ONE_TO_TWO_ROUNDTRIP = new Scenario(77003L,
            TRAIN1, 7, TrainDrivingDirectionEvent.DRIVING_DIRECTION.FORWARD,
            List.of(
                    new Scenario.Route(7301L,
                            List.of(
                                    new Scenario.Route.Block(4801L,80, 1),
                                    new Scenario.Route.Block(4802L,80, 2)
                            )
                    ),
                    new Scenario.Route(7302L,
                            List.of(
                                    new Scenario.Route.Block(4802L,80, 2),
                                    new Scenario.Route.Block(4804L,90, 2)
                            )
                    ),
                    new Scenario.Route(7303L,
                            List.of(
                                    new Scenario.Route.Block(4804L,90, 2),
                                    new Scenario.Route.Block(4803L,90, 1)
                            )
                    ),
                    new Scenario.Route(7304L,
                            List.of(
                                    new Scenario.Route.Block(4803L,90, 1),
                                    new Scenario.Route.Block(4801L,80, 1)
                            )
                    ),
                    new Scenario.Route(7305L,
                            List.of(
                                    new Scenario.Route.Block(4801L, 80, 1),
                                    new Scenario.Route.Block(4802L, 80, 2)
                            )
                    )
            )
    );

    public record Scenario(long id,
                           BaseTestData.Train train,
                           int drivingLevel,
                           TrainDrivingDirectionEvent.DRIVING_DIRECTION drivingDirection,
                           List<Route> routes
    ) {

        public record Route(long routeSequenceId, List<Block> blocks
        ) {
            public record Block(long trackPartId, int address, int number) {
            }
        }
    }
}


