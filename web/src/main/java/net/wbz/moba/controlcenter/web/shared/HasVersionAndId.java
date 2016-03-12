package net.wbz.moba.controlcenter.web.shared;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
public interface HasVersionAndId extends Serializable {

    Integer getVersion();

    long getId();



}
