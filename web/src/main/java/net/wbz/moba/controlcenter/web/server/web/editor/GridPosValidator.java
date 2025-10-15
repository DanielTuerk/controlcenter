package net.wbz.moba.controlcenter.web.server.web.editor;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.wbz.moba.controlcenter.shared.editor.ValidationException;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.GridPosition;

/**
 * Check for same {@link net.wbz.moba.controlcenter.web.shared.track.model.GridPosition} in {@link AbstractTrackPart}s.
 *
 * @author Daniel Tuerk
 */
@Singleton
class GridPosValidator implements TrackPartValidator {

    @Override
    public void validate(Collection<AbstractTrackPart> trackParts) throws ValidationException {
        final ValidationException validationException = new ValidationException();




        for (AbstractTrackPart abstractTrackPart : trackParts) {

            // compare with the other track parts
            List<AbstractTrackPart> otherTrackParts = Lists.newArrayList(trackParts);
            otherTrackParts.remove(abstractTrackPart);

            for (GridPosition position : getGridPositions(otherTrackParts)) {

                for (GridPosition gridPosition : getGridPositions(Lists.newArrayList(abstractTrackPart))) {

                    if (gridPosition.isSame(position)) {
                        validationException.addError(abstractTrackPart, "same grid position");
                    }
                }

            }
        }
        if (validationException.hasError()) {
            throw validationException;
        }
    }

    private Set<GridPosition> getGridPositions(Collection<AbstractTrackPart> trackParts) {
        return Stream.concat(
            trackParts.stream()
                .filter(trackPart -> !(trackPart instanceof BlockStraight))
                .map(AbstractTrackPart::getGridPosition),
            trackParts.stream()
                .filter(trackPart -> trackPart instanceof BlockStraight)
                .flatMap(trackPart -> (((BlockStraight) trackPart).getAllGridPositions()).stream())
        ).collect(Collectors.toSet());
    }

}
