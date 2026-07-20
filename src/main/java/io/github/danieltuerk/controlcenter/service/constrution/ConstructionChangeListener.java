package io.github.danieltuerk.controlcenter.service.constrution;

import io.github.danieltuerk.controlcenter.shared.constrution.Construction;

/**
 * @author Daniel Tuerk
 */
public interface ConstructionChangeListener {

    void currentConstructionChanged(Construction construction);
}
