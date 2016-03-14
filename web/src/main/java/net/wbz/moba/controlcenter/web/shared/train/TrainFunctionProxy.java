package net.wbz.moba.controlcenter.web.shared.train;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = TrainFunction.class, locator = InjectingEntityLocator.class)
public interface TrainFunctionProxy extends EntityProxyWithIdAndVersion {

    void setId(long id);

    // public List<Train> getTrains() {
    // return trains;
    // }
    //
    // public void setTrains(List<Train> trains) {
    // this.trains = trains;
    // }

    TrainFunction.FUNCTION getFunction();

    void setFunction(TrainFunction.FUNCTION function);

    boolean isState();

    void setState(boolean state);
}
