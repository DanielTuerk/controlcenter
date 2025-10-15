package net.wbz.moba.controlcenter.shared;


/**
 * TODO replace for websockets
 *
 * @author Daniel Tuerk
 */
@Deprecated
public interface StateEvent extends Event {

    /**
     * TODO
     *
     * @return key
     */
    default String getCacheKey(){
        return getClass().getName();
    }
}
