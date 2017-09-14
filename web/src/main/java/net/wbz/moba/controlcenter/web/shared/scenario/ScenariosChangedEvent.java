package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.common.base.Objects;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.AbstractStateEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;

/**
 * @author Daniel Tuerk
 */
public class ScenariosChangedEvent implements Event {

    public ScenariosChangedEvent() {
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .toString();
    }
}
