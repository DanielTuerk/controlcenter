package net.wbz.moba.controlcenter.web.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.ArrayList;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@RemoteServiceRelativePath("bus")
public interface BusService extends RemoteService {

    public void connectBus();
    public void disonnectBus();
    public boolean isBusConnected();

    public void changeDevice(DeviceInfo deviceInfo);

    public void createDevice(DeviceInfo deviceInfo);

    public void deleteDevice(DeviceInfo deviceInfo);

    public ArrayList<DeviceInfo> getDevices();

    public boolean getRailVoltage();

    public void toggleRailVoltage();
}
