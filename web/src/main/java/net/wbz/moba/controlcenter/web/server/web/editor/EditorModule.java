package net.wbz.moba.controlcenter.web.server.web.editor;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * @author Daniel Tuerk
 */
public class EditorModule extends AbstractModule {

    @Override
    protected void configure() {
        addValidator(GridPosValidator.class);
    }

    private void addValidator(Class<? extends TrackPartValidator> validatorClass) {
        Multibinder.newSetBinder(binder(), TrackPartValidator.class).addBinding().to(validatorClass);
    }
}
