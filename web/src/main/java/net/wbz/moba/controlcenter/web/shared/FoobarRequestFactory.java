package net.wbz.moba.controlcenter.web.shared;

import com.google.web.bindery.requestfactory.shared.RequestFactory;
import net.wbz.moba.controlcenter.web.shared.bus.BusRequest;
import net.wbz.moba.controlcenter.web.shared.config.ConfigRequest;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionRequest;

/**
 * TODO
 *
 * @author Daniel Tuerk
 */
public interface FoobarRequestFactory extends RequestFactory {

    ConstructionRequest constructionRequest();
    BusRequest busRequest();
    ConfigRequest configRequest();
}
