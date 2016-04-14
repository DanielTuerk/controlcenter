package net.wbz.moba.controlcenter.web.shared.track.model;

import java.util.List;
import java.util.Set;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionProxy;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = TrackPart.class, locator = InjectingEntityLocator.class)
public interface TrackPartProxy extends EntityProxyWithIdAndVersion {

    void setId(long id);

    List<TrackPartFunctionProxy> getFunctions();

    void setFunctions(List<TrackPartFunctionProxy> functions);

    void setEventStateConfig(EventConfigurationProxy eventStateConfig);

    ConstructionProxy getConstruction();

    void setConstruction(ConstructionProxy constructionProxy);

    List<TrackPartFunctionProxy> getFunctionConfigs();

    Set<ConfigurationProxy> getConfigurationsOfFunctions();

    /**
     * Return the {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration} of the default toggle
     * function. {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants#DEFAULT_TOGGLE_FUNCTION}
     *
     * @return {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration}
     */
    ConfigurationProxy getDefaultToggleFunctionConfig();

    ConfigurationProxy getDefaultBlockFunctionConfig();

    String getConfigurationInfo();

    GridPositionProxy getGridPosition();

    void setGridPosition(GridPositionProxy gridPosition);

    boolean hasActiveEventConfiguration();

    EventConfigurationProxy getEventConfiguration();

    void setEventConfiguration(EventConfigurationProxy eventConfiguration);

    /**
     * Rotation angle in degree of the track part.
     *
     * @return angle in degree
     */
    double getRotationAngle();
}
