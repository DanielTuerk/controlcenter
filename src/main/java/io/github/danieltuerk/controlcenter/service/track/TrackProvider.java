package io.github.danieltuerk.controlcenter.service.track;

import io.github.danieltuerk.controlcenter.service.constrution.ConstructionService;
import io.github.danieltuerk.controlcenter.shared.constrution.Construction;
import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import io.github.danieltuerk.controlcenter.shared.track.model.AbstractTrackPart;
import io.github.danieltuerk.controlcenter.shared.track.model.Signal;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class TrackProvider {

    private final ConstructionService constructionService;
    private final TrackDataProvider trackDataProvider;
    @Inject
    public TrackProvider(ConstructionService constructionService, TrackDataProvider trackDataProvider) {
        this.constructionService = constructionService;
        this.trackDataProvider = trackDataProvider;
    }

    public Collection<AbstractTrackPart> getTrack() {
        return constructionService.getCurrentConstruction()
            .map(Construction::getId)
            .map(trackDataProvider::loadTrack)
            .orElse(Collections.emptyList());
    }

    public Optional<AbstractTrackPart> getTrackPart(Long trackPartId) {
        return getTrack().stream().filter(x-> x.getId().equals(trackPartId)).findAny();
    }

    /**
     * Find start signal of {@link Route}.
     *
     * @param route {@link Route}
     * @return {@link Signal}
     */
    public Optional<Signal> findStartSignal(Route route) {
        var signals = getTrack().stream()
            .filter(trackPart -> trackPart instanceof Signal)
            .map(trackPart -> (Signal) trackPart)
            .collect(Collectors.toSet());
        for (Signal availableSignal : signals) {
            if (availableSignal.getStopBlock() != null && route.getStart() != null) {
                if (route.getStart().getAllTrackBlocks().contains(availableSignal.getStopBlock())) {
                    return Optional.of(availableSignal);
                }
            }
        }
        return Optional.empty();
    }
}
