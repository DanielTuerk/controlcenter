package io.github.danieltuerk.controlcenter.shared.train;

import io.github.danieltuerk.controlcenter.shared.AbstractItemEvent;
import io.github.danieltuerk.controlcenter.shared.Event;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for modified data of {@link Train} or created/deleted entity.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "Trains update sent via WebSocket")
@Tag(ref = "websocket")
public record TrainDataChangedEvent(long trainId, AbstractItemEvent.ACTION_TYPE type) implements Event {

}
