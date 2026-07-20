package io.github.danieltuerk.controlcenter.shared.device;


import io.github.danieltuerk.controlcenter.shared.track.model.AbstractDto;
import lombok.Getter;
import lombok.Setter;

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
