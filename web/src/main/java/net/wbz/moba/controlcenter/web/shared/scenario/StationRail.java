package net.wbz.moba.controlcenter.web.shared.scenario;

import com.googlecode.jmapper.annotations.JMap;

import com.googlecode.jmapper.annotations.JMapConversion;
import com.googlecode.jmapper.annotations.JMapConversion.Type;
import javax.persistence.ManyToOne;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
public class StationRail extends AbstractDto {

    @JMap
    private int railNumber;

    // @JMap
    // private Station station;


    public int getRailNumber() {
        return railNumber;
    }

    public void setRailNumber(int railNumber) {
        this.railNumber = railNumber;
    }


    // public Station getStation() {
    // return station;
    // }
    //
    // public void setStation(Station station) {
    // this.station = station;
    // }
}
