package net.wbz.moba.controlcenter.web.server;

import net.wbz.moba.controlcenter.web.server.web.editor.block.BusAddressIdentifier;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

/**
 * @author Daniel Tuerk
 */
public class SelectrixHelper {
    public static FeedbackBlockModule getFeedbackBlockModule(Device device,
            BusAddressIdentifier entry) throws DeviceAccessException {
        return device.getFeedbackBlockModule(
                entry.getAddress(),
                (entry.getAddress() + 2),
                (entry.getAddress() + 1));
    }

}
