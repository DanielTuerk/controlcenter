package net.wbz.moba.controlcenter.web.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created by Daniel on 08.03.14.
 */
abstract public class AbstractIdModel implements IsSerializable {

    abstract public long getId();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AbstractIdModel that = (AbstractIdModel) o;

        return getId() == that.getId();

    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }
}
