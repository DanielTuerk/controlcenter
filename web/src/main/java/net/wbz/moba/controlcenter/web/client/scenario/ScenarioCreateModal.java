package net.wbz.moba.controlcenter.web.client.scenario;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;

/**
 * @author Daniel Tuerk
 */
class ScenarioCreateModal extends AbstractScenarioDataModal {

    ScenarioCreateModal(Scenario scenario) {
        super(scenario, "Create scenario");
    }

    @Override
    protected void saveScenario(Scenario scenario) {
        RequestUtils.getInstance().getScenarioEditorService().createScenario(scenario,
                RequestUtils.VOID_ASYNC_CALLBACK);
    }
}
