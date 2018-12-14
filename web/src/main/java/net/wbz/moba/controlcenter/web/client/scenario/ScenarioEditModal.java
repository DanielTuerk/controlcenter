package net.wbz.moba.controlcenter.web.client.scenario;

import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;

/**
 * @author Daniel Tuerk
 */
public class ScenarioEditModal extends AbstractScenarioDataModal {

    ScenarioEditModal(Scenario scenario) {
        super(scenario, "Edit scenario");
    }

    @Override
    protected void saveScenario(Scenario scenario) {
        RequestUtils.getInstance().getScenarioEditorService().updateScenario(scenario,
                RequestUtils.VOID_ASYNC_CALLBACK);
    }
}
