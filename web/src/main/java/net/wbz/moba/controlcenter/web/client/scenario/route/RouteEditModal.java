package net.wbz.moba.controlcenter.web.client.scenario.route;

import com.google.gwt.user.client.ui.IsWidget;

import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.components.AbstractEditModal;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;

/**
 * @author Daniel Tuerk
 */
public class RouteEditModal extends AbstractEditModal<Route> {

    private RouteEditModalBody editModalBody;

    public RouteEditModal(Route route) {
        super("Edit Route", "Save", "Cancel", route);
        setWidth("80%");
    }

    @Override
    protected IsWidget createContent(Route model) {
        editModalBody = new RouteEditModalBody(model);
        return editModalBody;
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected void onConfirm(Route route) {
        route.setName(editModalBody.getSelectedName());
        route.setOneway(editModalBody.getSelectedOnewayState());

        if (route.getId() == null) {
            RequestUtils.getInstance().getScenarioEditorService().createRoute(route,
                    new OnlySuccessAsyncCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            hide();
                        }
                    });
        } else {
            RequestUtils.getInstance().getScenarioEditorService().updateRoute(route,
                    new OnlySuccessAsyncCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            hide();
                        }
                    });
        }
    }
}
