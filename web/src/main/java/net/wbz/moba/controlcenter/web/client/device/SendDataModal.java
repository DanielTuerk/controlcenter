package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

/**
 * Modal to enter and send the data for an address of a bus.
 *
 * @author Daniel Tuerk
 */
public class SendDataModal extends Composite {

    private static SendDataModal.Binder UI_BINDER = GWT.create(SendDataModal.Binder.class);
    @UiField
    Button btnSend;
    @UiField
    Modal modal;
    @UiField
    TextBox txtBus;
    @UiField
    TextBox txtAddress;
    @UiField
    TextBox txtData;

    public SendDataModal() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    public void show() {
        modal.show();
    }

    @UiHandler({"txtBus", "txtAddress", "txtData"})
    void onPasswordTextBoxKeyPress(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
            sendData();
        }
    }

    @UiHandler("btnSend")
    void send(ClickEvent event) {
        sendData();
    }

    @UiHandler("btnClose")
    void close(ClickEvent event) {
        modal.hide();
    }

    private void sendData() {
        final int address = Integer.parseInt(txtAddress.getText());
        final int busNr = Integer.parseInt(txtBus.getText());
        final int data = Integer.parseInt(txtData.getText());
        RequestUtils.getInstance().getBusService().sendBusData(busNr, address, data, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Notify.notify("send data", "can't send data: " + caught.getMessage(), IconType.WARNING);
            }

            @Override
            public void onSuccess(Void result) {
                Notify.notify("send data", "data send - bus: " + busNr + " address: " + address + " data: " + data,
                    IconType.INFO);
            }
        });
    }

    interface Binder extends UiBinder<Widget, SendDataModal> {

    }
}
