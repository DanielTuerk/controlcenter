package io.github.danieltuerk.controlcenter.service.track;

import io.github.danieltuerk.controlcenter.BusAddressIdentifier;
import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.persist.entity.track.BusDataConfigurationEntity;
import io.github.danieltuerk.controlcenter.shared.track.model.BusDataConfiguration;
import io.github.danieltuerk.controlcenter.shared.track.model.Signal;
import io.github.danieltuerk.controlcenter.shared.track.model.Signal.LIGHT;
import io.github.danieltuerk.controlcenter.shared.viewer.SignalFunctionStateEvent;
import io.github.danieltuerk.selectrix4java.bus.BusAddressBitListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.danieltuerk.controlcenter.shared.track.model.Signal.LIGHT.*;

/**
 * Register busAddressListeners to update the current signal function by bus data. Each signal function change will
 * throw a new {@link SignalFunctionStateEvent} by the {@link
 * EventBroadcaster}.
 *
 * @author Daniel Tuerk
 */
class SignalFunctionReceiver {

    private final Signal signal;
    private final EventBroadcaster eventBroadcaster;
    private final Map<BusAddressIdentifier, List<BusAddressBitListener>> busAddressListeners = new ConcurrentHashMap<>();

    private final Map<LIGHT, Boolean> lightStates = new HashMap<>();

    /**
     * Flag to avoid multiple events for same function by received bit changes.
     */
    private Signal.FUNCTION lastFiredFunction = null;

    SignalFunctionReceiver(final Signal signal, EventBroadcaster eventBroadcaster) {
        this.signal = signal;
        this.eventBroadcaster = eventBroadcaster;

        for (final Map.Entry<LIGHT, BusDataConfiguration> lightConfigs : signal
            .getSignalLightsConfigurations(signal.getType()).entrySet()) {
            // initial state 'off' for each light
            lightStates.put(lightConfigs.getKey(), false);

            // register consumer for each light to update the state
            if (lightConfigs.getValue() != null && lightConfigs.getValue().isValid()) {

                BusAddressIdentifier busAddressIdentifier = new BusAddressIdentifier(lightConfigs.getValue().getBus(),
                    lightConfigs.getValue().getAddress());
                if (!busAddressListeners.containsKey(busAddressIdentifier)) {
                    busAddressListeners.put(busAddressIdentifier, new ArrayList<>());
                }

                busAddressListeners.get(busAddressIdentifier)
                    .add(new BusAddressBitListener(lightConfigs.getValue().getBit()) {

                        @Override
                        public synchronized void bitChanged(boolean oldValue, boolean newValue) {
                            lightStates.put(lightConfigs.getKey(),
                                (newValue) == lightConfigs.getValue().getBitState());

                            // check for new function by active lights
                            switch (signal.getType()) {
                                case BLOCK:
                                    if (on(RED1)
                                        && off(GREEN1)) {
                                        fireFunction(Signal.FUNCTION.HP0);
                                    } else if (on(GREEN1)
                                        && off(RED1)) {
                                        fireFunction(Signal.FUNCTION.HP1);
                                    }
                                    break;
                                case ENTER:
                                    if (on(RED1)
                                        && off(GREEN1)
                                        && off(YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP0);
                                    } else if (off(RED1)
                                        && on(GREEN1)
                                        && off(YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP1);
                                    } else if (off(RED1)
                                        && on(GREEN1)
                                        && on(YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP2);
                                    }
                                    break;
                                case EXIT:
                                    if (on(RED1)
                                        && on(RED2)
                                        && off(WHITE)
                                        && off(GREEN1)
                                        && off(YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP0);
                                    } else if (on(GREEN1)
                                        && off(RED1)
                                        && off(RED2)
                                        && off(WHITE)
                                        && off(YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP1);
                                    } else if (on(GREEN1)
                                        && on(YELLOW1)
                                        && off(RED1)
                                        && off(RED2)
                                        && off(WHITE)) {
                                        fireFunction(Signal.FUNCTION.HP2);
                                    } else if (on(RED1)
                                        && on(WHITE)
                                        && off(RED2)
                                        && off(GREEN1)
                                        && off(YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP0_SH1);
                                    }
                                    break;
                                case BEFORE:
                                    if (on(YELLOW1) && on(YELLOW2) && off(
                                        GREEN1) && off(GREEN2)) {
                                        fireFunction(Signal.FUNCTION.HP0);
                                    } else if (off(YELLOW1) && off(YELLOW2) && on(
                                        GREEN1) && on(GREEN2)) {
                                        fireFunction(Signal.FUNCTION.HP1);
                                    } else if (off(YELLOW1) && on(YELLOW2) && on(
                                        GREEN1) && off(GREEN2)) {
                                        fireFunction(Signal.FUNCTION.HP2);
                                    }
                                    break;
                            }
                        }
                    });
            }
        }
    }

    /**
     * Each {@link io.github.danieltuerk.selectrix4java.bus.BusAddressBitListener} for all addresses of the
     * {@link BusDataConfigurationEntity} of the signal.
     *
     * @return listeners for each address
     */
    Map<BusAddressIdentifier, List<BusAddressBitListener>> getBusAddressListeners() {
        return busAddressListeners;
    }

    /**
     * Check the active state of the given light.
     *
     * @param light {@link LIGHT}
     * @return {@code true} if on
     */
    private boolean on(LIGHT light) {
        return lightStates.get(light);
    }

    /**
     * Check inactive state of the given light.
     *
     * @param light {@link LIGHT}
     * @return {@code true} if off
     */
    private boolean off(LIGHT light) {
        return !lightStates.get(light);
    }

    /**
     * Fire event for changes {@link Signal.FUNCTION}. Multiple call by same function is ignored. Synchronized method to
     * update the state member {@see lastFiredFunction}.
     *
     * @param function {@link Signal.FUNCTION}
     */
    private synchronized void fireFunction(Signal.FUNCTION function) {
        if (lastFiredFunction != function) {
            eventBroadcaster.fireEvent(new SignalFunctionStateEvent(signal.getId(), function));
            lastFiredFunction = function;
        }
    }
}
