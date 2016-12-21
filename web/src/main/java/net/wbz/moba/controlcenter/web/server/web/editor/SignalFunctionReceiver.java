package net.wbz.moba.controlcenter.web.server.web.editor;

import com.google.common.collect.Maps;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.SignalEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartConfigurationEntity;
import net.wbz.moba.controlcenter.web.shared.viewer.SignalFunctionStateEvent;
import net.wbz.selectrix4java.bus.BusAddressBitListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Register busAddressListeners to update the current signal function by bus data.
 * Each signal function change will throw an new
 * {@link net.wbz.moba.controlcenter.web.shared.viewer.SignalFunctionStateEvent} by the
 * {@link net.wbz.moba.controlcenter.web.server.EventBroadcaster}.
 *
 * @author Daniel Tuerk
 */
public class SignalFunctionReceiver {
    private final SignalEntity signal;
    private final EventBroadcaster eventBroadcaster;
    private Map<BusAddressIdentifier, List<BusAddressBitListener>> busAddressListeners = Maps.newConcurrentMap();

    private Map<SignalEntity.LIGHT, Boolean> lightStates = Maps.newHashMap();

    /**
     * Flag to avoid multiple events for same function by received bit changes.
     */
    private SignalEntity.FUNCTION lastFiredFunction = null;

    public SignalFunctionReceiver(final SignalEntity signal, EventBroadcaster eventBroadcaster) {
        this.signal = signal;
        this.eventBroadcaster = eventBroadcaster;


        for (final Map.Entry<SignalEntity.LIGHT, TrackPartConfigurationEntity> lightConfigs : signal.getSignalConfiguration().entrySet()) {
            // initial state 'off' for each light
            lightStates.put(lightConfigs.getKey(), false);

            // register consumer for each light to update the state
            if (lightConfigs.getValue().isValid()) {

                BusAddressIdentifier busAddressIdentifier = new BusAddressIdentifier(lightConfigs.getValue().getBus(), lightConfigs.getValue().getAddress());
                if (!busAddressListeners.containsKey(busAddressIdentifier)) {
                    busAddressListeners.put(busAddressIdentifier, new ArrayList<BusAddressBitListener>());
                }

                busAddressListeners.get(busAddressIdentifier).add(new BusAddressBitListener(lightConfigs.getValue().getBit()) {

                    @Override
                    public synchronized void bitChanged(boolean oldValue, boolean newValue) {
                        lightStates.put(lightConfigs.getKey(), (newValue) == lightConfigs.getValue().isBitState());

                        // check for new function by active lights
                        switch (signal.getType()) {
                            case BLOCK:
                                if (on(SignalEntity.LIGHT.RED1)
                                        && off(SignalEntity.LIGHT.GREEN1)) {
                                    fireFunction(SignalEntity.FUNCTION.HP0);
                                } else if (off(SignalEntity.LIGHT.RED1)
                                        && on(SignalEntity.LIGHT.GREEN1)) {
                                    fireFunction(SignalEntity.FUNCTION.HP1);
                                }
                                break;
                            case ENTER:
                                if (on(SignalEntity.LIGHT.RED1)
                                        && off(SignalEntity.LIGHT.GREEN1)
                                        && off(SignalEntity.LIGHT.YELLOW1)) {
                                    fireFunction(SignalEntity.FUNCTION.HP0);
                                } else if (off(SignalEntity.LIGHT.RED1)
                                        && on(SignalEntity.LIGHT.GREEN1)
                                        && off(SignalEntity.LIGHT.YELLOW1)) {
                                    fireFunction(SignalEntity.FUNCTION.HP1);
                                } else if (off(SignalEntity.LIGHT.RED1)
                                        && on(SignalEntity.LIGHT.GREEN1)
                                        && on(SignalEntity.LIGHT.YELLOW1)) {
                                    fireFunction(SignalEntity.FUNCTION.HP2);
                                }
                                break;
                            case EXIT:
                                if (on(SignalEntity.LIGHT.RED1)
                                        && on(SignalEntity.LIGHT.RED2)
                                        && off(SignalEntity.LIGHT.WHITE)
                                        && off(SignalEntity.LIGHT.GREEN1)
                                        && off(SignalEntity.LIGHT.YELLOW1)) {
                                    fireFunction(SignalEntity.FUNCTION.HP0);
                                } else if (off(SignalEntity.LIGHT.RED1)
                                        && off(SignalEntity.LIGHT.RED2)
                                        && off(SignalEntity.LIGHT.WHITE)
                                        && on(SignalEntity.LIGHT.GREEN1)
                                        && off(SignalEntity.LIGHT.YELLOW1)) {
                                    fireFunction(SignalEntity.FUNCTION.HP1);
                                } else if (off(SignalEntity.LIGHT.RED1)
                                        && off(SignalEntity.LIGHT.RED2)
                                        && off(SignalEntity.LIGHT.WHITE)
                                        && on(SignalEntity.LIGHT.GREEN1)
                                        && on(SignalEntity.LIGHT.YELLOW1)) {
                                    fireFunction(SignalEntity.FUNCTION.HP2);
                                } else if (on(SignalEntity.LIGHT.RED1)
                                        && off(SignalEntity.LIGHT.RED2)
                                        && on(SignalEntity.LIGHT.WHITE)
                                        && off(SignalEntity.LIGHT.GREEN1)
                                        && off(SignalEntity.LIGHT.YELLOW1)) {
                                    fireFunction(SignalEntity.FUNCTION.HP0_SH1);
                                }
                                break;
                            case BEFORE:
                                if (on(SignalEntity.LIGHT.YELLOW1)
                                        && on(SignalEntity.LIGHT.YELLOW2)
                                        && off(SignalEntity.LIGHT.GREEN1)
                                        && off(SignalEntity.LIGHT.GREEN2)) {
                                    fireFunction(SignalEntity.FUNCTION.HP0);
                                } else if (off(SignalEntity.LIGHT.YELLOW1)
                                        && off(SignalEntity.LIGHT.YELLOW2)
                                        && on(SignalEntity.LIGHT.GREEN1)
                                        && on(SignalEntity.LIGHT.GREEN2)) {
                                    fireFunction(SignalEntity.FUNCTION.HP1);
                                } else if (off(SignalEntity.LIGHT.YELLOW1)
                                        && on(SignalEntity.LIGHT.YELLOW2)
                                        && on(SignalEntity.LIGHT.GREEN1)
                                        && off(SignalEntity.LIGHT.GREEN2)) {
                                    fireFunction(SignalEntity.FUNCTION.HP2);
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
     * @param light {@link SignalEntity.LIGHT}
     * @return {@code true} if on
     */
    private boolean on(SignalEntity.LIGHT light) {
        return lightStates.get(light);
    }

    /**
     * Check inactive state of the given light.
     *
     * @param light {@link SignalEntity.LIGHT}
     * @return {@code true} if off
     */
    private boolean off(SignalEntity.LIGHT light) {
        return !lightStates.get(light);
    }

    /**
     * Fire event for changes {@link SignalEntity.FUNCTION}.
     * Multiple call by same function is ignored. Synchronized method to update the state
     * member {@see lastFiredFunction}.
     *
     * @param function {@link SignalEntity.FUNCTION}
     */
    private synchronized void fireFunction(SignalEntity.FUNCTION function) {
        if (lastFiredFunction != function) {
            eventBroadcaster.fireEvent(new SignalFunctionStateEvent(signal.getSignalConfiguration(), function));
            lastFiredFunction = function;
        }
    }

    /**
     * Each {@link net.wbz.selectrix4java.bus.BusAddressBitListener} for all addresses of the
     * {@link TrackPartConfigurationEntity} of the signal.
     *
     * @return listeners for each address
     */
    public Map<BusAddressIdentifier, List<BusAddressBitListener>> getBusAddressListeners() {
        return busAddressListeners;
    }
}
