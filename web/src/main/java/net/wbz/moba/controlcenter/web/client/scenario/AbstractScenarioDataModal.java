package net.wbz.moba.controlcenter.web.client.scenario;

import com.google.gwt.user.client.ui.IsWidget;

import net.wbz.moba.controlcenter.web.client.components.AbstractEditModal;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;

/**
 * @author Daniel Tuerk
 */
abstract class AbstractScenarioDataModal extends AbstractEditModal<Scenario> {

    private ScenarioEditModalBody editModalBody;

    AbstractScenarioDataModal(Scenario scenario, String title) {
        super(title, "Apply", "Cancel", scenario);
    }

    @Override
    protected IsWidget createContent(Scenario model) {
        editModalBody = new ScenarioEditModalBody(model);
        return editModalBody;
    }

    @Override
    protected void onCancel() {
        hide();
    }

    @Override
    protected void onConfirm(Scenario model) {
        saveScenario(editModalBody.getUpdatedModel());
        hide();
    }

    protected abstract void saveScenario(Scenario scenario);
}
