package net.wbz.moba.controlcenter.web.server.constrution;

import com.db4o.ObjectSet;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sun.istack.internal.Nullable;
import net.wbz.moba.controlcenter.communication.api.Device;
import net.wbz.moba.controlcenter.communication.com1.Com1Device;
import net.wbz.moba.controlcenter.communication.manager.DeviceManager;
import net.wbz.moba.controlcenter.db.Database;
import net.wbz.moba.controlcenter.db.DatabaseFactory;
import net.wbz.moba.controlcenter.db.StorageException;
import net.wbz.moba.controlcenter.web.shared.BusService;
import net.wbz.moba.controlcenter.web.shared.DeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Singleton
public class BusServiceImpl extends RemoteServiceServlet implements BusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusServiceImpl.class);
    public static final String BUS_DB_KEY = "bus";

    private Device activeDevice;
    private final Database settingsDatabase;
    private final DeviceManager deviceManager;

    @Inject
    public BusServiceImpl(DeviceManager deviceManager, @Named("settings") DatabaseFactory databaseFactory) {
        this.deviceManager = deviceManager;
        if (!databaseFactory.getExistingDatabaseNames().contains(BUS_DB_KEY)) {
            try {
                settingsDatabase = databaseFactory.addDatabase(BUS_DB_KEY);
            } catch (IOException e) {
                throw new RuntimeException("can't init database for the 'bus' settings", e);
            }
        } else {
            try {
                settingsDatabase = databaseFactory.getStorage(BUS_DB_KEY);
                ObjectSet<DeviceInfo> storedDevices = settingsDatabase.getObjectContainer().query(DeviceInfo.class);
                for(DeviceInfo deviceInfo : storedDevices) {
                    deviceManager.registerDevice(DeviceManager.DEVICE_TYPE.valueOf(
                            deviceInfo.getType().name()), deviceInfo.getKey());
                }

            } catch (StorageException e) {
                throw new RuntimeException("no DB found for BUS key: " + BUS_DB_KEY);
            }
        }
    }

    @Override
    public void changeDevice(DeviceInfo deviceInfo) {
        activeDevice = deviceManager.getDeviceById(deviceInfo.getKey());
    }

    @Override
    public synchronized void createDevice(DeviceInfo deviceInfo) {
        deviceManager.registerDevice(DeviceManager.DEVICE_TYPE.valueOf(
                deviceInfo.getType().name()), deviceInfo.getKey());
        settingsDatabase.getObjectContainer().store(deviceInfo);
        settingsDatabase.getObjectContainer().commit();
    }

    @Override
    public synchronized void deleteDevice(DeviceInfo deviceInfo) {
        Device device = deviceManager.getDeviceById(deviceInfo.getKey());
        deviceManager.removeDevice(device);
        settingsDatabase.getObjectContainer().delete(deviceInfo);
        settingsDatabase.getObjectContainer().commit();
    }

    @Override
    public ArrayList<DeviceInfo> getDevices() {
        return Lists.newArrayList(Lists.transform(deviceManager.getDevices(), new Function<Device, DeviceInfo>() {
            @Override
            public DeviceInfo apply(@Nullable Device input) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setKey(deviceManager.getDeviceId(input));
                if (input instanceof Com1Device) {
                    deviceInfo.setType(DeviceInfo.DEVICE_TYPE.COM1);
                }
                return deviceInfo;
            }
        }));
    }

    @Override
    public void connectBus() {
        if (activeDevice != null) {
            activeDevice.connect();
        }
    }

    @Override
    public void disonnectBus() {
        if (activeDevice != null) {
            activeDevice.disconnect();
        }
    }

    @Override
    public boolean isBusConnected() {
        return activeDevice != null && activeDevice.isConnected();
    }

}
