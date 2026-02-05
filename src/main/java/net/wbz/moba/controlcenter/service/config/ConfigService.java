package net.wbz.moba.controlcenter.service.config;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.api.config.ConfigItem;
import net.wbz.moba.controlcenter.persist.entity.ConfigValueEntity;
import net.wbz.moba.controlcenter.persist.repository.ConfigRepository;
import net.wbz.moba.controlcenter.shared.config.ConfigNotAvailableException;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ConfigService {

    @Inject
    ConfigRepository configRepository;
    @Inject
    ConfigMapper configMapper;

    public Set<ConfigItem> findAll() {
        return configRepository.listAll().stream()
            .map(configMapper::toDto)
            .collect(Collectors.toSet());
    }

    public String loadValue(String configKey) throws ConfigNotAvailableException {
        return configRepository.findByIdOptional(configKey)
            .orElseThrow(() -> new ConfigNotAvailableException(configKey))
            .value;
    }

    @Transactional
    public void saveValue(String configKey, String value) {
        var configValueEntity = configRepository.findByIdOptional(configKey)
            .orElseGet(() -> {
                var entity = new ConfigValueEntity();
                entity.key = configKey;
                entity.value = value;
                return entity;
            });
        configValueEntity.value = value;
        configRepository.persist(configValueEntity);
    }

}
