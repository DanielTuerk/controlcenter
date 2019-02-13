package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.google.common.collect.Maps;
import com.googlecode.jmapper.annotations.JMap;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.LIGHT;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants;

/**
 * Widget to show and control a signal.
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_SIGNAL")
public class SignalEntity extends StraightEntity {

    @JMap
    @Column
    private TYPE type;

    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity signalConfigRed1;
    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity signalConfigRed2;
    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity signalConfigGreen1;
    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity signalConfigGreen2;
    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity signalConfigYellow1;
    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity signalConfigYellow2;
    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity signalConfigWhite;

    /**
     * Block to detect an entering train which has to be stopped for occupied monitoring blocks.
     */
    @JMap
    @ManyToOne(fetch = FetchType.EAGER)
    private TrackBlockEntity enteringBlock;

    /**
     * Block to start breaking. If it's {@code null} than the breaking is used in the stop block.
     */
    @JMap
    @ManyToOne(fetch = FetchType.EAGER)
    private TrackBlockEntity breakingBlock;

    /**
     * Block to immediately stop the train.
     */
    @JMap
    @ManyToOne(fetch = FetchType.EAGER)
    private TrackBlockEntity stopBlock;

    /**
     * Blocks which should be monitored to be free.
     */
    @JMap
    @ManyToOne(fetch = FetchType.EAGER)
    private TrackBlockEntity monitoringBlock;

    public SignalEntity() {
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public BusDataConfigurationEntity getSignalConfigRed1() {
        return signalConfigRed1;
    }

    public void setSignalConfigRed1(BusDataConfigurationEntity signalConfigRed1) {
        this.signalConfigRed1 = signalConfigRed1;
    }

    public BusDataConfigurationEntity getSignalConfigRed2() {
        return signalConfigRed2;
    }

    public void setSignalConfigRed2(BusDataConfigurationEntity signalConfigRed2) {
        this.signalConfigRed2 = signalConfigRed2;
    }

    public BusDataConfigurationEntity getSignalConfigGreen1() {
        return signalConfigGreen1;
    }

    public void setSignalConfigGreen1(BusDataConfigurationEntity signalConfigGreen1) {
        this.signalConfigGreen1 = signalConfigGreen1;
    }

    public BusDataConfigurationEntity getSignalConfigGreen2() {
        return signalConfigGreen2;
    }

    public void setSignalConfigGreen2(BusDataConfigurationEntity signalConfigGreen2) {
        this.signalConfigGreen2 = signalConfigGreen2;
    }

    public BusDataConfigurationEntity getSignalConfigYellow1() {
        return signalConfigYellow1;
    }

    public void setSignalConfigYellow1(BusDataConfigurationEntity signalConfigYellow1) {
        this.signalConfigYellow1 = signalConfigYellow1;
    }

    public BusDataConfigurationEntity getSignalConfigYellow2() {
        return signalConfigYellow2;
    }

    public void setSignalConfigYellow2(BusDataConfigurationEntity signalConfigYellow2) {
        this.signalConfigYellow2 = signalConfigYellow2;
    }

    public BusDataConfigurationEntity getSignalConfigWhite() {
        return signalConfigWhite;
    }

    public void setSignalConfigWhite(BusDataConfigurationEntity signalConfigWhite) {
        this.signalConfigWhite = signalConfigWhite;
    }

    public TrackBlockEntity getEnteringBlock() {
        return enteringBlock;
    }

    public void setEnteringBlock(TrackBlockEntity enteringBlock) {
        this.enteringBlock = enteringBlock;
    }

    public TrackBlockEntity getBreakingBlock() {
        return breakingBlock;
    }

    public void setBreakingBlock(TrackBlockEntity breakingBlock) {
        this.breakingBlock = breakingBlock;
    }

    public TrackBlockEntity getStopBlock() {
        return stopBlock;
    }

    public void setStopBlock(TrackBlockEntity stopBlock) {
        this.stopBlock = stopBlock;
    }

    public TrackBlockEntity getMonitoringBlock() {
        return monitoringBlock;
    }

    public void setMonitoringBlock(TrackBlockEntity monitoringBlock) {
        this.monitoringBlock = monitoringBlock;
    }

    @Override
    public Class<? extends AbstractTrackPart> getDefaultDtoClass() {
        return Signal.class;
    }
}
