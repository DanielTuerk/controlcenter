package net.wbz.moba.controlcenter.persist.entity.track;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.wbz.moba.controlcenter.shared.track.model.Signal;

/**
 * Widget to show and control a signal.
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_SIGNAL")
public class SignalEntity extends StraightEntity {

    public Signal.TYPE type;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity signalConfigRed1;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity signalConfigRed2;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity signalConfigGreen1;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity signalConfigGreen2;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity signalConfigYellow1;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity signalConfigYellow2;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity signalConfigWhite;

    /**
     * Block to detect an entering train which has to be stopped for occupied monitoring blocks.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    public TrackBlockEntity enteringBlock;

    /**
     * Block to start breaking. If it's {@code null} than the breaking is used in the stop block.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    public TrackBlockEntity breakingBlock;

    /**
     * Block to immediately stop the train.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    public TrackBlockEntity stopBlock;

    /**
     * Blocks which should be monitored to be free.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    public TrackBlockEntity monitoringBlock;


}
