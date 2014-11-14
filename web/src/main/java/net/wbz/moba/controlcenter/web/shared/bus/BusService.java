package net.wbz.moba.controlcenter.web.shared.bus;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.ArrayList;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@RemoteServiceRelativePath("bus")
public interface BusService extends RemoteService {

    public void connectBus();
    public void disconnectBus();
    public boolean isBusConnected();

    public void changeDevice(DeviceInfo deviceInfo);

    public void createDevice(DeviceInfo deviceInfo);

    public void deleteDevice(DeviceInfo deviceInfo);

    public ArrayList<DeviceInfo> getDevices();

    public boolean getRailVoltage();

    public void toggleRailVoltage();

    public BusData[] readBusData(int busNr);

    /**
     * Simple access to the selectrix bus. Set value for the bit of the current connected device.
     *
     * @param busNr number of bus
     * @param address address of bit
     * @param bit bit number (1-8)
     * @param state {@link java.lang.Boolean} new state for the bit
     */
    public void sendBusData(int busNr, int address, int bit, boolean state);
}
