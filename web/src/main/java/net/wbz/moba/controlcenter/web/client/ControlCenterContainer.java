package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.AppMenu.AppMenuCallback;
import org.gwtbootstrap3.client.ui.Container;

/**
 * @author Daniel Tuerk
 */
public class ControlCenterContainer extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Container contentContainer;
    @UiField
    AppMenu appMenu;

    ControlCenterContainer(AppMenuCallback appMenuCallback) {
        initWidget(uiBinder.createAndBindUi(this));
        appMenu.setCallback(appMenuCallback);
    }

    void setContent(Widget widget) {
        contentContainer.add(widget);
    }

    interface Binder extends UiBinder<Widget, ControlCenterContainer> {

    }
}
