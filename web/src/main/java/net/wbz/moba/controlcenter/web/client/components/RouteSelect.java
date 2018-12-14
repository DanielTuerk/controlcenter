package net.wbz.moba.controlcenter.web.client.components;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import javax.validation.constraints.NotNull;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;

/**
 * Select component for {@link Route}s.
 * 
 * @author Daniel Tuerk
 */
public class RouteSelect extends AbstractLoadableSelect<Route> {

    @Override
    protected String getKey(@NotNull Route object) {
        return String.valueOf(object.getId());
    }

    @Override
    protected void loadChoices(AsyncCallback<Collection<Route>> callback) {
        RequestUtils.getInstance().getScenarioEditorService().getRoutes(callback);
    }

    @Override
    protected String getDisplayValue(Route choice) {
        return choice.getName();
    }

}
