package io.github.danieltuerk.controlcenter.persist.entity.track;

/**
 * @author Daniel Tuerk
 */
public interface HasToggleFunctionEntity {

    EventConfigurationEntity getEventConfiguration();

    void setEventConfiguration(EventConfigurationEntity eventConfigurationEntity);

    BusDataConfigurationEntity getToggleFunctionConfiguration();

    void setToggleFunctionConfiguration(BusDataConfigurationEntity busDataConfigurationEntity);
}
