package net.wbz.moba.controlcenter.service.config;


import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.api.config.ConfigItem;
import net.wbz.moba.controlcenter.persist.entity.ConfigValueEntity;
import net.wbz.moba.controlcenter.persist.repository.ConfigRepository;
import net.wbz.moba.controlcenter.shared.config.ConfigNotAvailableException;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.train.Train;
import org.mapstruct.ap.internal.util.Strings;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
@Startup
@Slf4j
public class ConfigService {

    private static final String CONSTRUCTION_DEFAULT = "construction.default";
    private static final String HP_0_AFTER_TRAIN_PASS_DELAY_IN_SECONDS = "HP0_AFTER_TRAIN_PASS_DELAY_IN_SECONDS";
    /**
     * Delay to start the {@link Train} for a started {@link Route}.
     */
    private static final String START_TRAIN_DELAY_SECONDS = "START_TRAIN_DELAY_SECONDS";
    /**
     * Delay to wait to finish the {@link Route} for a stopped {@link Train} at {@link Route} end.
     */
    private static final String FINISH_ROUTE_DELAY_SECONDS = "FINISH_ROUTE_DELAY_SECONDS";
    private static final String DEFAULT_START_DRIVING_LEVEL = "DEFAULT_START_DRIVING_LEVEL";
    private static final String WAIT_FOR_FREE_TACK_TIMEOUT_IN_MINUTES = "WAIT_FOR_FREE_TACK_TIMEOUT_IN_MINUTES";

    private final ConfigRepository configRepository;
    private final ConfigMapper configMapper;

    public ConfigService(ConfigRepository configRepository, ConfigMapper configMapper) {
        this.configRepository = configRepository;
        this.configMapper = configMapper;
    }

    public Set<ConfigItem> findAll() {
        return configRepository.listAll().stream()
            .map(configMapper::toDto)
            .collect(Collectors.toSet());
    }

    @ActivateRequestContext
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

    public Optional<Long> getDefaultConstructionId() {
        try {
            final var s = loadValue(CONSTRUCTION_DEFAULT);
            return Optional.ofNullable(Strings.isEmpty(s) ? null : Long.parseLong(s));
        } catch (ConfigNotAvailableException e) {
            log.debug("Config not available: {}", CONSTRUCTION_DEFAULT);
            return Optional.empty();
        }
    }

    public long getHp0AfterTrainPassDelayInSeconds() {
        return Long.parseLong(loadValue(HP_0_AFTER_TRAIN_PASS_DELAY_IN_SECONDS));
    }

    public int getDefaultStartDrivingLevel() {
        return Integer.parseInt(loadValue(DEFAULT_START_DRIVING_LEVEL));
    }

    public long getStartTrainDelaySeconds() {
        return Long.parseLong(loadValue(START_TRAIN_DELAY_SECONDS));
    }

    public long getFinishRouteDelaySeconds() {
        return Long.parseLong(loadValue(FINISH_ROUTE_DELAY_SECONDS));
    }

    public long getWaitForFreeTackTimeoutInMinutes() {
        return Long.parseLong(loadValue(WAIT_FOR_FREE_TACK_TIMEOUT_IN_MINUTES));
    }

}
