package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.AppMenu.AppMenuCallback;
import org.gwtbootstrap3.client.ui.Container;

/**
 * Main container to switch the content over {@link AppMenu}.
 *
 * @author Daniel Tuerk
 */
class ControlCenterContainer extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    HTMLPanel contentContainer;
    @UiField
    AppMenu appMenu;

    ControlCenterContainer(AppMenuCallback appMenuCallback) {
        initWidget(uiBinder.createAndBindUi(this));
        appMenu.setCallback(appMenuCallback);
    }

    void setContent(Widget widget) {
        // first remove all children from the entries which are also parents, otherwise the removal will be blocked
        for (Widget container : contentContainer) {
            if (contentContainer.getElement().isOrHasChild(container.getElement())) {
                contentContainer.remove(container);
            }
        }
        // remove the first level elements
        contentContainer.getElement().removeAllChildren();
        contentContainer.add(widget);
    }

    interface Binder extends UiBinder<Widget, ControlCenterContainer> {

    }
}
