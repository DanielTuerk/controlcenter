package io.github.danieltuerk.controlcenter.shared.constrution;


import io.github.danieltuerk.controlcenter.shared.track.model.AbstractDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Daniel Tuerk
 */
@Setter
@Getter
public class Construction extends AbstractDto {
    private String name;

    private boolean inAutomaticMode = false;

    public Construction() {
    }

}
