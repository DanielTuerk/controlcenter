package net.wbz.moba.controlcenter.web.shared;

import com.google.web.bindery.requestfactory.shared.EntityProxy;

/**
 * @author Daniel Tuerk
 */
public interface EntityProxyWithIdAndVersion extends EntityProxy {

    Long getId();

    Integer getVersion();
}
