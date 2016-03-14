package net.wbz.moba.controlcenter.web.shared.train;

import java.util.Set;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = Train.class, locator = InjectingEntityLocator.class)
public interface TrainProxy extends EntityProxyWithIdAndVersion {

    int getAddress();

    void setAddress(int address);

    int getDrivingLevel();

    void setDrivingLevel(int drivingLevel);

    String getName();

    void setName(String name);

    Train.DIRECTION getDrivingDirection();

    void setDrivingDirection(Train.DIRECTION drivingDirection);

    Set<TrainFunctionProxy> getFunctions();

    void setFunctions(Set<TrainFunctionProxy> functions);

//    TrainFunctionProxy getFunction(TrainFunction.FUNCTION function);
}
