package net.wbz.moba.controlcenter.shared.train;


import lombok.Getter;
import lombok.Setter;
import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;

/**
 * @author Daniel Tuerk
 */
@Setter
@Getter
public class TrainFunction extends AbstractDto {

    private String alias;
    private BusDataConfiguration configuration;

    @Override
    public String toString() {
        return "TrainFunction{alias='%s', configuration=%s}".formatted(alias, configuration);
    }
}
