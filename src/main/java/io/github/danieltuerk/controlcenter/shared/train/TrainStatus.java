package io.github.danieltuerk.controlcenter.shared.train;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrainStatus {
    private int drivingLevel;
    private Train.DRIVING_DIRECTION direction;

    public TrainStatus(int drivingLevel, Train.DRIVING_DIRECTION direction) {
        this.drivingLevel = drivingLevel;
        this.direction = direction;
    }

}
