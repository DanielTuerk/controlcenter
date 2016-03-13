package net.wbz.moba.controlcenter.web.shared.bus;

import java.util.List;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingServiceLocator;
import net.wbz.moba.controlcenter.web.server.constrution.BusService;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

/**
 * @author Daniel Tuerk
 */
@Service(value = BusService.class, locator = InjectingServiceLocator.class)
public interface BusRequest extends RequestContext {

    Request<Void> connectBus();

    Request<Void> disconnectBus();

    Request<Boolean> isBusConnected();

    Request<Void> changeDevice(DeviceInfoProxy deviceInfo);

    Request<Void> createDevice(DeviceInfoProxy deviceInfo);

    Request<Void> deleteDevice(DeviceInfoProxy deviceInfo);

    Request<List<DeviceInfoProxy>> getDevices();

    Request<Boolean> getRailVoltage();

    Request<Void> toggleRailVoltage();

    /**
     * Start the listener for all buses.
     * Consumer will receive all changes from device and delegate to throw the
     * {@link net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent}s.
     */
    Request<Void> startTrackingBus();

    /**
     * Stop the listener for all buses.
     * Unregister the Consumer will stop throwing the {@link net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent}s.
     */
    Request<Void> stopTrackingBus();

    /**
     * Simple access to the selectrix bus. Set value for the bit of the current connected device.
     *
     * @param busNr number of bus
     * @param address address of bit
     * @param bit bit number (1-8)
     * @param state {@link java.lang.Boolean} new state for the bit
     */
    Request<Void> sendBusData(int busNr, int address, int bit, boolean state);

    /**
     * Send value for the given address of the bus number to the current connected device.
     *
     * @param busNr number of bus
     * @param address address
     * @param data data of address
     */
    Request<Void> sendBusData(int busNr, int address, int data);

    Request<Void> startRecording(String fileName);

    Request<Void> stopRecording();

    Request<Void> startPlayer(String absoluteFilePath);

    Request<Void> stopPlayer();

    Request<List<String>> getRecords();
}
