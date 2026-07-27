package io.github.danieltuerk.controlcenter.api.station;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
public record StationDto(String name, List<StationPlatformDto> platforms) {

    public record StationPlatformDto(Long id, String name, List<Long> trackBlockIds) {
    }
}
