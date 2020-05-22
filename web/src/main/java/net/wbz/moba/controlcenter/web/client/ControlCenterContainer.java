package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.AppMenu.AppMenuCallback;

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

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    void setContent(Widget widget) {
        contentContainer.clear();
        contentContainer.getElement().setInnerHTML("");
        contentContainer.add(widget);
    }

    interface Binder extends UiBinder<Widget, ControlCenterContainer> {

    }
}
