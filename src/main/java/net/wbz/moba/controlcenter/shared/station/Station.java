package net.wbz.moba.controlcenter.shared.station;


import java.util.ArrayList;
import java.util.List;
import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
public class Station extends AbstractDto {

    private String name;

    private List<StationPlatform> platforms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StationPlatform> getPlatforms() {
        if (platforms == null) {
            platforms = new ArrayList<>();
        }
        return platforms;
    }

    public void setPlatforms(List<StationPlatform> platforms) {
        this.platforms = platforms;
    }
}
