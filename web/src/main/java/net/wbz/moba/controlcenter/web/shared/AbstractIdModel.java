package net.wbz.moba.controlcenter.web.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created by Daniel on 08.03.14.
 */
public class AbstractIdModel  implements IsSerializable {

    private long id=-1L;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractIdModel that = (AbstractIdModel) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
