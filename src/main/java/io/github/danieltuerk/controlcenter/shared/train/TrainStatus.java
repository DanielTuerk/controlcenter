package io.github.danieltuerk.controlcenter.shared.train;

public class TrainStatus {
    private int drivingLevel;
    private Train.DRIVING_DIRECTION direction;

    public TrainStatus(int drivingLevel, Train.DRIVING_DIRECTION direction) {
        this.drivingLevel = drivingLevel;
        this.direction = direction;
    }

    public int getDrivingLevel() {
        return drivingLevel;
    }

    public void setDrivingLevel(int drivingLevel) {
        this.drivingLevel = drivingLevel;
    }

    public Train.DRIVING_DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(Train.DRIVING_DIRECTION direction) {
        this.direction = direction;
    }

}
