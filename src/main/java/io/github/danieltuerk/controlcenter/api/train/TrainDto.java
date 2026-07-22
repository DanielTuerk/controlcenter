package io.github.danieltuerk.controlcenter.api.train;

import io.github.danieltuerk.controlcenter.shared.train.TrainFunction;

/**
 * @author Daniel Tuerk
 */
public record TrainDto(String name, Integer address, TrainFunction[] functions) {
}
