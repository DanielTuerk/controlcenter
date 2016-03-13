package net.wbz.moba.controlcenter.web.shared.bus;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = DeviceInfo.class, locator = InjectingEntityLocator.class)
public interface DeviceInfoProxy extends EntityProxyWithIdAndVersion {

    String getKey();

    void setKey(String key);

    DeviceInfo.DEVICE_TYPE getType();

    void setType(DeviceInfo.DEVICE_TYPE type);

    boolean isConnected();

    void setConnected(boolean connected);

}
