package io.github.danieltuerk.controlcenter.shared.train;

public record TrainInstance(Train train, TrainStatus trainStatus) {

    public static TrainInstance of(Train train) {
        return new TrainInstance(train, new TrainStatus(0, Train.DRIVING_DIRECTION.FORWARD));
    }
}
