package io.github.danieltuerk.controlcenter.persist.entity.track;


import io.github.danieltuerk.controlcenter.shared.track.model.Signal;
import jakarta.persistence.*;

/**
 * Widget to show and control a signal.
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_SIGNAL")
public class SignalEntity extends StraightEntity {

    @Enumerated(EnumType.ORDINAL)
    public Signal.SIGNAL_TYPE type;

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
     * Block to immediately stop the train.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    public TrackBlockEntity stopBlock;
}
