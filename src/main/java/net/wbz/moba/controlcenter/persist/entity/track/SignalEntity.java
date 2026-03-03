package net.wbz.moba.controlcenter.persist.entity.track;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.ORDINAL)
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

}
