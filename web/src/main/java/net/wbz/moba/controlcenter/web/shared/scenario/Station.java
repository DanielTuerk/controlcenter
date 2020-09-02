package net.wbz.moba.controlcenter.web.shared.scenario;

import com.googlecode.jmapper.annotations.JMap;
import java.util.ArrayList;
import java.util.List;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
public class Station extends AbstractDto {

    @JMap
    private String name;

    @JMap
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
