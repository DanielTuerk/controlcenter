package net.wbz.moba.controlcenter.web.server.web.editor;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.BusDataConfigurationEntity;
import net.wbz.moba.controlcenter.web.server.web.editor.block.BusAddressIdentifier;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.viewer.SignalFunctionStateEvent;
import net.wbz.selectrix4java.bus.BusAddressBitListener;

/**
 * Register busAddressListeners to update the current signal function by bus data. Each signal function change will
 * throw an new {@link net.wbz.moba.controlcenter.web.shared.viewer.SignalFunctionStateEvent} by the {@link
 * EventBroadcaster}.
 *
 * @author Daniel Tuerk
 */
public class SignalFunctionReceiver {

    private final Signal signal;
    private final EventBroadcaster eventBroadcaster;
    private Map<BusAddressIdentifier, List<BusAddressBitListener>> busAddressListeners = Maps.newConcurrentMap();

    private Map<Signal.LIGHT, Boolean> lightStates = Maps.newHashMap();

    /**
     * Flag to avoid multiple events for same function by received bit changes.
     */
    private Signal.FUNCTION lastFiredFunction = null;

    public SignalFunctionReceiver(final Signal signal, EventBroadcaster eventBroadcaster) {
        this.signal = signal;
        this.eventBroadcaster = eventBroadcaster;

        // TODO refactor?
        for (final Map.Entry<Signal.LIGHT, BusDataConfiguration> lightConfigs : signal
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
                                    if (on(Signal.LIGHT.RED1) && off(Signal.LIGHT.GREEN1)) {
                                        fireFunction(Signal.FUNCTION.HP0);
                                    } else if (off(Signal.LIGHT.RED1) && on(Signal.LIGHT.GREEN1)) {
                                        fireFunction(Signal.FUNCTION.HP1);
                                    }
                                    break;
                                case ENTER:
                                    if (on(Signal.LIGHT.RED1) && off(Signal.LIGHT.GREEN1) && off(
                                        Signal.LIGHT.YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP0);
                                    } else if (off(Signal.LIGHT.RED1) && on(Signal.LIGHT.GREEN1) && off(
                                        Signal.LIGHT.YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP1);
                                    } else if (off(Signal.LIGHT.RED1) && on(Signal.LIGHT.GREEN1) && on(
                                        Signal.LIGHT.YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP2);
                                    }
                                    break;
                                case EXIT:
                                    if (on(Signal.LIGHT.RED1) && on(Signal.LIGHT.RED2) && off(Signal.LIGHT.WHITE)
                                        && off(Signal.LIGHT.GREEN1) && off(Signal.LIGHT.YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP0);
                                    } else if (off(Signal.LIGHT.RED1) && off(Signal.LIGHT.RED2) && off(
                                        Signal.LIGHT.WHITE) && on(Signal.LIGHT.GREEN1) && off(
                                        Signal.LIGHT.YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP1);
                                    } else if (off(Signal.LIGHT.RED1) && off(Signal.LIGHT.RED2) && off(
                                        Signal.LIGHT.WHITE) && on(Signal.LIGHT.GREEN1) && on(
                                        Signal.LIGHT.YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP2);
                                    } else if (on(Signal.LIGHT.RED1) && off(Signal.LIGHT.RED2) && on(
                                        Signal.LIGHT.WHITE) && off(Signal.LIGHT.GREEN1) && off(
                                        Signal.LIGHT.YELLOW1)) {
                                        fireFunction(Signal.FUNCTION.HP0_SH1);
                                    }
                                    break;
                                case BEFORE:
                                    if (on(Signal.LIGHT.YELLOW1) && on(Signal.LIGHT.YELLOW2) && off(
                                        Signal.LIGHT.GREEN1) && off(Signal.LIGHT.GREEN2)) {
                                        fireFunction(Signal.FUNCTION.HP0);
                                    } else if (off(Signal.LIGHT.YELLOW1) && off(Signal.LIGHT.YELLOW2) && on(
                                        Signal.LIGHT.GREEN1) && on(Signal.LIGHT.GREEN2)) {
                                        fireFunction(Signal.FUNCTION.HP1);
                                    } else if (off(Signal.LIGHT.YELLOW1) && on(Signal.LIGHT.YELLOW2) && on(
                                        Signal.LIGHT.GREEN1) && off(Signal.LIGHT.GREEN2)) {
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
     * Check active state of the given light.
     *
     * @param light {@link Signal.LIGHT}
     * @return {@code true} if on
     */
    private boolean on(Signal.LIGHT light) {
        return lightStates.get(light);
    }

    /**
     * Check inactive state of the given light.
     *
     * @param light {@link Signal.LIGHT}
     * @return {@code true} if off
     */
    private boolean off(Signal.LIGHT light) {
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

    /**
     * Each {@link net.wbz.selectrix4java.bus.BusAddressBitListener} for all addresses of the {@link
     * BusDataConfigurationEntity} of the signal.
     *
     * @return listeners for each address
     */
    public Map<BusAddressIdentifier, List<BusAddressBitListener>> getBusAddressListeners() {
        return busAddressListeners;
    }
}
