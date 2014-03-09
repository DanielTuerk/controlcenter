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

}
