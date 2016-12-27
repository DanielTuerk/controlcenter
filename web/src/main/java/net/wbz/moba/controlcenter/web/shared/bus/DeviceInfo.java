package net.wbz.moba.controlcenter.web.shared.bus;

import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
public class DeviceInfo extends AbstractDto {

    public enum DEVICE_TYPE {
        SERIAL, TEST
    }

    @JMap
    private String key;
    @JMap
    private DEVICE_TYPE type;
    private boolean connected;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DEVICE_TYPE getType() {
        return type;
    }

    public void setType(DEVICE_TYPE type) {
        this.type = type;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }


}
