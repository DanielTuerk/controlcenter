package net.wbz.moba.controlcenter.web.server.web;

import com.google.inject.AbstractModule;
import net.wbz.moba.controlcenter.web.server.web.constrution.ConstructionServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.editor.EditorModule;
import net.wbz.moba.controlcenter.web.server.web.train.TrainEditorServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionService;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorService;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;

/**
 * @author Daniel Tuerk
 */
public class WebModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(ConstructionService.class).to(ConstructionServiceImpl.class).asEagerSingleton();
        bind(TrainService.class).to(TrainServiceImpl.class).asEagerSingleton();
        bind(TrainEditorService.class).to(TrainEditorServiceImpl.class).asEagerSingleton();

        install(new EditorModule());
    }
}
