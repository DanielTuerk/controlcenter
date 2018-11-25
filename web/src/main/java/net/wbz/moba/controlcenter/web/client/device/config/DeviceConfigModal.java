package net.wbz.moba.controlcenter.web.client.device.config;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.server.persist.device.DeviceInfoEntity;
import org.gwtbootstrap3.client.ui.Modal;

/**
 * Modal to configure the {@link DeviceInfoEntity}s for the connections.
 *
 * @author Daniel Tuerk
 */
public class DeviceConfigModal extends Composite {

    private static DeviceConfigModal.Binder UI_BINDER = GWT.create(DeviceConfigModal.Binder.class);

    @UiField
    Modal modal;

    public DeviceConfigModal() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    public void show() {
        modal.show();
    }

    @UiHandler("btnClose")
    void close(ClickEvent event) {
        modal.hide();
    }

    interface Binder extends UiBinder<Widget, DeviceConfigModal> {

    }
}
