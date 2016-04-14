package net.wbz.moba.controlcenter.web.shared.track.model;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = Configuration.class, locator = InjectingEntityLocator.class)
public interface ConfigurationProxy extends EntityProxyWithIdAndVersion {

    void setId(long id);

    int getAddress();

    void setAddress(int address);

    int getBit();

    void setBit(int bit);

    boolean isValid();

    boolean isBitState();

    void setBitState(boolean bitState);

    int getBus();

    void setBus(int bus);
}
