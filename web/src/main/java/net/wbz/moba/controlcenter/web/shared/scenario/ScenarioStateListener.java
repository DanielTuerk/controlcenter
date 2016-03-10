package net.wbz.moba.controlcenter.web.shared.scenario;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

/**
 * @author Daniel Tuerk
 */
public class ScenarioStateListener implements RemoteEventListener {
    /**
     * This function gets called by EventService
     */
    public void apply(Event anEvent) {
        /**
         * Check if the incoming Event is from the type MyEvent
         * and if so, call the corresponding function
         */
        if (anEvent instanceof ScenarioStateEvent) {
            onMyEvent((ScenarioStateEvent) anEvent);
        }
    }

    /**
     * This function gets called when the incomming Event is
     * from the Type MyEvent
     *
     * @param event
     */
    public void onMyEvent(ScenarioStateEvent event) {
    }
}
