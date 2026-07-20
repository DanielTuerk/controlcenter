package io.github.danieltuerk.controlcenter.shared.train;


import io.github.danieltuerk.controlcenter.shared.track.model.AbstractDto;
import io.github.danieltuerk.controlcenter.shared.track.model.BusDataConfiguration;
import lombok.Getter;
import lombok.Setter;

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
