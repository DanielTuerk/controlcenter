package net.wbz.moba.controlcenter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import net.wbz.selectrix4java.device.DeviceManager;

/**
 * Quarkus bridge to allow injection of beans from library 'selectrix4java'.
 *
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
