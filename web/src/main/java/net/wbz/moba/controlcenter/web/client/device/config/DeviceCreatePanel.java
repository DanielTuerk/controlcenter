package net.wbz.moba.controlcenter.web.client.device.config;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.bus.BusServiceAsync;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

/**
 * @author Daniel Tuerk
 */
class DeviceCreatePanel extends Composite {

    private static DeviceCreatePanel.Binder UI_BINDER = GWT.create(DeviceCreatePanel.Binder.class);
    @UiField
    TextBox txtKey;

    DeviceCreatePanel() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    @UiHandler("btnCreate")
    void create(ClickEvent event) {
        // TODO validation feedback on dialog
        if (!Strings.isNullOrEmpty(txtKey.getText())) {
            BusServiceAsync busRequest = RequestUtils.getInstance().getBusService();
            final DeviceInfo deviceInfo = new DeviceInfo();
            if ("test".equals(txtKey.getText())) {
                deviceInfo.setType(DeviceInfo.DEVICE_TYPE.TEST);
            } else {
                deviceInfo.setType(DeviceInfo.DEVICE_TYPE.SERIAL);
            }
            deviceInfo.setKey(txtKey.getValue());

            busRequest.createDevice(deviceInfo, new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    Notify.notify("", "Device " + deviceInfo.getKey() + " error", IconType.INBOX);
                }

                @Override
                public void onSuccess(Void result) {
                    Notify.notify("", "Device " + deviceInfo.getKey() + " created", IconType.INFO);
                }
            });
        }
    }

    interface Binder extends UiBinder<Widget, DeviceCreatePanel> {

    }

}
