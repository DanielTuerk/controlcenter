package net.wbz.moba.controlcenter.shared.device;


import lombok.Getter;
import lombok.Setter;
import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
@Setter
@Getter
public class DeviceInfo extends AbstractDto {

    private String key;
    private DEVICE_TYPE type;

    public enum DEVICE_TYPE {
        SERIAL, TEST
    }

}
