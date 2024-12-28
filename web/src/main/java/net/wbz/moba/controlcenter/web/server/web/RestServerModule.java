package net.wbz.moba.controlcenter.web.server.web;

import com.google.inject.AbstractModule;
import net.wbz.moba.controlcenter.web.server.web.constrution.ConstructionController;
import net.wbz.moba.controlcenter.web.server.web.train.TrainController;

/**
 * Module to bind all the REST controller.
 *
 * @author Daniel Tuerk
 */
public class RestServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConstructionController.class);
        bind(TrainController.class);
    }
}
