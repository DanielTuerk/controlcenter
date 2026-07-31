package io.github.danieltuerk.controlcenter.shared.station;


import io.github.danieltuerk.controlcenter.shared.track.model.AbstractDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Setter
public class Station extends AbstractDto {

    @Getter
    private String name;

    private List<StationPlatform> platforms;

    public List<StationPlatform> getPlatforms() {
        if (platforms == null) {
            platforms = new ArrayList<>();
        }
        return platforms;
    }

}
