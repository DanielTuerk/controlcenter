package net.wbz.moba.controlcenter.web.shared.scenario;

import java.util.List;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
public class Station extends AbstractDto {

    @JMap
    private String name;

    @JMap
    private List<StationRail> rails;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StationRail> getRails() {
        return rails;
    }

    public void setRails(List<StationRail> rails) {
        this.rails = rails;
    }
}
