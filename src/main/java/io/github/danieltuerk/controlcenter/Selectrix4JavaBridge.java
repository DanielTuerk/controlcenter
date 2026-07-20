package io.github.danieltuerk.controlcenter;

import io.github.danieltuerk.selectrix4java.device.DeviceManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

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
