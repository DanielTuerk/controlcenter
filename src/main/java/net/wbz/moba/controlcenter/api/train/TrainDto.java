package net.wbz.moba.controlcenter.api.train;

import net.wbz.moba.controlcenter.shared.train.TrainFunction;

/**
 * @author Daniel Tuerk
 */
public record TrainDto(String name, Integer address, TrainFunction[] functions) {
}
