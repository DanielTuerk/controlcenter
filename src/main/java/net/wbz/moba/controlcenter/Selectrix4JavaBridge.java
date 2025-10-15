package net.wbz.moba.controlcenter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import net.wbz.selectrix4java.device.DeviceManager;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class Selectrix4JavaBridge {

    @Produces
    @ApplicationScoped
    public DeviceManager deviceManager() {
        return new DeviceManager();
    }
}
