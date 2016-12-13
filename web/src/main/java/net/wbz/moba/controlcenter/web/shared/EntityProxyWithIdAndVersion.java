package net.wbz.moba.controlcenter.web.shared;

import com.google.web.bindery.requestfactory.shared.EntityProxy;

import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
public interface EntityProxyWithIdAndVersion extends EntityProxy,Serializable {

    Long getId();

    Integer getVersion();
}
