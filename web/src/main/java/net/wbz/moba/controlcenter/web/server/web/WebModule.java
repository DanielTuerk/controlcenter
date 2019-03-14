package net.wbz.moba.controlcenter.web.server.web;

import com.google.inject.AbstractModule;
import net.wbz.moba.controlcenter.web.server.web.editor.EditorModule;

/**
 * @author Daniel Tuerk
 */
public class WebModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new EditorModule());
    }
}
