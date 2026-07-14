package net.wbz.moba.controlcenter.shared;


/**
 * @author Daniel Tuerk
 */
public interface StateEvent extends Event {

    default String getCacheKey(){
        return getClass().getName();
    }

}
