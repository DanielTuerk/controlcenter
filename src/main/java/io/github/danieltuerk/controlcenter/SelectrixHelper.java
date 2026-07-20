package io.github.danieltuerk.controlcenter;

import io.github.danieltuerk.controlcenter.shared.track.model.TrackBlock;
import io.github.danieltuerk.selectrix4java.block.BlockModule;
import io.github.danieltuerk.selectrix4java.block.FeedbackBlockModule;
import io.github.danieltuerk.selectrix4java.device.Device;
import io.github.danieltuerk.selectrix4java.device.DeviceAccessException;

/**
 * @author Daniel Tuerk
 */
public class SelectrixHelper {

    public static FeedbackBlockModule getFeedbackBlockModule(Device device,
            BusAddressIdentifier entry) throws DeviceAccessException {
        return device.getFeedbackBlockModule(
            entry.address(),
            (entry.address() + 2),
            (entry.address() + 1));
    }

    public static BlockModule getBlockModule(Device device, TrackBlock trackBlock) throws DeviceAccessException {
        BusAddressIdentifier entry = new BusAddressIdentifier(trackBlock.getBlockFunction());
        return getBlockModule(device, entry);
    }

    public static BlockModule getBlockModule(Device device,
        BusAddressIdentifier entry) throws DeviceAccessException {
        return device.getBlockModule(entry.address());
    }
}
