package net.wbz.moba.controlcenter.web.server.web.constrution;

import net.wbz.moba.controlcenter.web.shared.constrution.Construction;

/**
 * @author Daniel Tuerk
 */
public interface ConstructionChangeListener {

    void currentConstructionChanged(Construction construction);
}
