package net.wbz.moba.controlcenter.api.track;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.GridPosition;

/**
 * @author Daniel Tuerk
 */
public record ChangeTrackDto(
    List<Add> addActions,
    List<Move> moveActions,
    List<Rotate> rotateActions) {

    public boolean isEmpty() {
        return addActions.isEmpty() && moveActions.isEmpty() && rotateActions.isEmpty();
    }

    public sealed interface Action permits Add, Move, Rotate {

    }

    public record Add(@NotNull AbstractTrackPart trackPart) implements Action {

    }

    public record Move(@NotNull long trackPartId, @NotNull GridPosition gridPosition) implements Action {

    }

    public record Rotate(@NotNull long trackPartId, String value) implements Action {

    }
}
