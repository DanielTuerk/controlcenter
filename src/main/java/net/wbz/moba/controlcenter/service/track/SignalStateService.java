package net.wbz.moba.controlcenter.service.track;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import net.wbz.moba.controlcenter.shared.bus.BusAddressBit;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.track.model.Signal.LIGHT;
import net.wbz.moba.controlcenter.shared.track.model.Signal.TYPE;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class SignalStateService {

    List<BusAddressBit> convertToLights(Signal signal, Signal.FUNCTION signalFunction) {
        Map<LIGHT, BusAddressBit> availableLightConfig = Maps.newHashMap();

        Signal.TYPE signalType = signal.getType();
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
        switch (signalFunction) {
            case HP0:
                switch (signalType) {
                    case BLOCK:
                        availableLightConfig.put(Signal.LIGHT.RED1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED1)));
                        break;
                    case BEFORE:
                        availableLightConfig.put(Signal.LIGHT.YELLOW1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW2)));
                        break;
                    case EXIT:
                        availableLightConfig.put(Signal.LIGHT.RED1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED1)));
                        availableLightConfig.put(Signal.LIGHT.RED2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED2)));
                        break;
                    case ENTER:
                        availableLightConfig.put(Signal.LIGHT.RED1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED1)));
                        break;
                }
                break;
            case HP1:
                switch (signalType) {
                    case BLOCK:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        break;
                    case BEFORE:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.GREEN2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN2)));
                        break;
                    case EXIT:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        break;
                    case ENTER:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        break;
                }
                break;
            case HP2:
                switch (signalType) {
                    case BEFORE:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW2)));
                        break;
                    case EXIT:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW1)));
                        break;
                    case ENTER:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW1)));
                        break;
                }
                break;
            case HP0_SH1:
                if (signalType == TYPE.EXIT) {
                    availableLightConfig.put(LIGHT.RED1,
                        convertFunctionConfig(signal.getSignalConfiguration(LIGHT.RED1)));
                    availableLightConfig.put(LIGHT.WHITE,
                        convertFunctionConfig(signal.getSignalConfiguration(LIGHT.WHITE)));
                }
                break;
        }
        return Lists.newArrayList(availableLightConfig.values());
    }

    private BusAddressBit convertFunctionConfig(BusDataConfiguration configuration) {
        if (configuration != null && configuration.isValid()) {
            return new BusAddressBit(configuration.getBus(), configuration.getAddress(), configuration.getBit(),
                configuration.getBitState());
        }
        return null;
    }
}
