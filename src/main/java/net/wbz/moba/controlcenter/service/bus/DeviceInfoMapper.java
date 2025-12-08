package net.wbz.moba.controlcenter.service.bus;

import net.wbz.moba.controlcenter.persist.entity.DeviceInfoEntity;
import net.wbz.moba.controlcenter.shared.bus.DeviceInfo;
import org.mapstruct.Mapper;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "cdi")
public interface DeviceInfoMapper {

    DeviceInfo toDto(DeviceInfoEntity entity);

}
