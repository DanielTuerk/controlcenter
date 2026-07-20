package io.github.danieltuerk.controlcenter.service.bus;

import io.github.danieltuerk.controlcenter.persist.entity.DeviceInfoEntity;
import io.github.danieltuerk.controlcenter.shared.device.DeviceInfo;
import org.mapstruct.Mapper;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "jakarta-cdi")
public interface DeviceInfoMapper {

    DeviceInfo toDto(DeviceInfoEntity entity);

}
