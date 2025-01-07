package net.wbz.moba.controlcenter.web.server.web;

import com.google.inject.AbstractModule;
import net.wbz.moba.controlcenter.web.server.web.constrution.ConstructionService;
import net.wbz.moba.controlcenter.web.server.web.editor.EditorModule;
import net.wbz.moba.controlcenter.web.server.web.train.TrainEditorService;
import net.wbz.moba.controlcenter.web.server.web.train.TrainService;

/**
 * @author Daniel Tuerk
 */
public class WebModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(ConstructionService.class).asEagerSingleton();
        bind(TrainService.class).asEagerSingleton();
        bind(TrainEditorService.class).asEagerSingleton();

        install(new EditorModule());
    }
}
