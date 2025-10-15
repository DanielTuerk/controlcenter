package net.wbz.moba.controlcenter.service;

import net.wbz.moba.controlcenter.shared.constrution.Construction;

/**
 * @author Daniel Tuerk
 */
public interface ConstructionChangeListener {

    void currentConstructionChanged(Construction construction);
}
