package net.wbz.moba.controlcenter.service.track;

import jakarta.enterprise.context.ApplicationScoped;
import net.wbz.moba.controlcenter.shared.bus.BusAddressBit;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.track.model.Signal.LIGHT;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class SignalStateService {

    Set<BusAddressBit> convertToLights(Signal signal, Signal.FUNCTION signalFunction) {
        Map<LIGHT, BusAddressBit> availableLightConfig = new HashMap<>();

        Signal.SIGNAL_TYPE signalType = signal.getType();
        // set all lights to 'off'
        for (Signal.LIGHT light : signalType.getLights()) {
            BusDataConfiguration lightFunction = signal.getSignalConfiguration(light);
            if (lightFunction != null && lightFunction.isValid()) {
                availableLightConfig.put(light,
                    new BusAddressBit(lightFunction.getBus(), lightFunction.getAddress(), lightFunction.getBit(),
                        !lightFunction.getBitState()));
            }
        }

        // set lights to 'on' for the signal function
        switch (signalType) {
            case BLOCK:
                switch (signalFunction) {
                    case HP0:
                        availableLightConfig.put(Signal.LIGHT.RED1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED1)));
                        break;
                    case HP1:
                    case HP2:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        break;
                }
            case BEFORE:
                switch (signalFunction) {
                    case HP0:
                        availableLightConfig.put(Signal.LIGHT.YELLOW1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW2)));
                        break;
                    case HP1:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.GREEN2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN2)));
                        break;
                    case HP2:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW2)));
                        break;
                }
            case EXIT:
                switch (signalFunction) {
                    case HP0:
                        availableLightConfig.put(Signal.LIGHT.RED1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED1)));
                        availableLightConfig.put(Signal.LIGHT.RED2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED2)));
                        break;
                    case HP1:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        break;
                    case HP2:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW1)));
                        break;
                    case HP0_SH1:
                        availableLightConfig.put(LIGHT.RED1,
                            convertFunctionConfig(signal.getSignalConfiguration(LIGHT.RED1)));
                        availableLightConfig.put(LIGHT.WHITE,
                            convertFunctionConfig(signal.getSignalConfiguration(LIGHT.WHITE)));
                        break;
                }
            case ENTER:
                switch (signalFunction) {
                    case HP0:
                        availableLightConfig.put(Signal.LIGHT.RED1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED1)));
                        break;
                    case HP1:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        break;
                    case HP2:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW1)));
                        break;
                }
        }
        return new HashSet<>(availableLightConfig.values());
    }

    private BusAddressBit convertFunctionConfig(BusDataConfiguration configuration) {
        if (configuration != null && configuration.isValid()) {
            return new BusAddressBit(configuration.getBus(), configuration.getAddress(), configuration.getBit(),
                configuration.getBitState());
        }
        return null;
    }
}
