package net.wbz.moba.controlcenter.web.client.scenario;

import com.google.common.collect.Lists;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import java.util.Collection;
import java.util.List;
import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenariosChangedEvent;

/**
 * TODO mal so als Idee alles über provider und statechange auch wieder provider fragen, bzw. auf provider update warten
 * @author Daniel Tuerk
 */
public class ScenarioDataProvider {

    private List<Scenario> data;
    private RemoteEventListener scenarioEventListener;

    void init() {

        scenarioEventListener = new RemoteEventListener() {
            @Override
            public void apply(Event anEvent) {
                if (anEvent instanceof ScenariosChangedEvent) {
                    load();
                }
            }
        };

        EventReceiver.getInstance().addListener(ScenariosChangedEvent.class, scenarioEventListener);
        load();
    }

    void load() {
        RequestUtils.getInstance().getScenarioEditorService().getScenarios(
            new OnlySuccessAsyncCallback<Collection<Scenario>>() {
                @Override
                public void onSuccess(Collection<Scenario> result) {
                    data = Lists.newArrayList(result);
                }
            });
    }

    void destroy() {
        EventReceiver.getInstance().removeListener(ScenariosChangedEvent.class, scenarioEventListener);
        data = null;
    }
}
