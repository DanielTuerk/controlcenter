package io.github.danieltuerk.controlcenter.api.device;

/**
 * @author Daniel Tuerk
 */
public record BusBitDto(int bus, int address, int bit, boolean state) {

}
