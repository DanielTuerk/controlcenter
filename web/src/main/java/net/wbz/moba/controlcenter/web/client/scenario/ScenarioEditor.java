package net.wbz.moba.controlcenter.web.client.scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk
 */
public class ScenarioEditor extends Composite {
    private static ScenarioEditorBinder uiBinder = GWT.create(ScenarioEditorBinder.class);

    public ScenarioEditor() {
        initWidget(uiBinder.createAndBindUi(this));

    }

    interface ScenarioEditorBinder extends UiBinder<Widget, ScenarioEditor> {
    }
}
