package net.wbz.moba.controlcenter.service.bus;

import net.wbz.moba.controlcenter.persist.entity.DeviceInfoEntity;
import net.wbz.moba.controlcenter.shared.device.DeviceInfo;
import org.mapstruct.Mapper;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "jakarta-cdi")
public interface DeviceInfoMapper {

    DeviceInfo toDto(DeviceInfoEntity entity);

}
