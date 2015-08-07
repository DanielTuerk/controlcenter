package net.wbz.moba.controlcenter.web.shared.constrution;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public interface ConstructionService {

    public Construction createConstruction(Construction construction);

    public Construction[] loadConstructions();

    public void setCurrentConstruction(Construction construction);

    public Construction getCurrentConstruction();

}
