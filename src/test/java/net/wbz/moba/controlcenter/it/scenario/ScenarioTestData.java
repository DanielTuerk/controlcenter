package net.wbz.moba.controlcenter.it.scenario;

import net.wbz.moba.controlcenter.shared.train.TrainDrivingDirectionEvent;

import java.util.List;

public class ScenarioTestData {


    public static Scenario LEFT_TO_RIGHT = new Scenario(9601L,
        new Scenario.Train(10001, 5), 10, TrainDrivingDirectionEvent.DRIVING_DIRECTION.FORWARD,
        List.of(
            new Scenario.Route(6401L,
                List.of(
                    new Scenario.Route.Block(50, 1),
                    new Scenario.Route.Block(60, 1)
                )
            ),
            new Scenario.Route(6402L,
                List.of(
                    new Scenario.Route.Block(60, 1),
                    new Scenario.Route.Block(70, 2)
                )
            )
        )
    );
    public static Scenario RIGHT_TO_LEFT = new Scenario(9602L,
        new Scenario.Train(10002, 6), 12, TrainDrivingDirectionEvent.DRIVING_DIRECTION.BACKWARD,
        List.of(
            new Scenario.Route(3201L,
                List.of(
                    new Scenario.Route.Block(70, 2),
                    new Scenario.Route.Block(60, 2)
                )
            ),
            new Scenario.Route(4801L,
                List.of(
                    new Scenario.Route.Block(60, 2),
                    new Scenario.Route.Block(50, 1)
                )
            )
        )
    );

    public record Scenario(long id,
                           Train train,
                           int drivingLevel,
                           TrainDrivingDirectionEvent.DRIVING_DIRECTION drivingDirection,
                           List<Route> routes
    ) {
        public record Train(int id, int address) {
        }

        public record Route(long routeSequenceId, List<Block> blocks
        ) {
            public record Block(int address, int number) {
            }
        }
    }
}


