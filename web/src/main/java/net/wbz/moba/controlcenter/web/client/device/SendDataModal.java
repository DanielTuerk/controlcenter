package net.wbz.moba.controlcenter.web.client.device;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.wbz.moba.controlcenter.web.client.request.RequestUtils;

/**
 * Modal to enter and send the data for an address of a bus.
 *
 * @author Daniel Tuerk
 */
public class SendDataModal extends Modal {

    public SendDataModal() {
        setFade(true);
        setTitle("Send Data");

        ModalBody modalBody = new ModalBody();

        Form form = new Form();
        form.setType(FormType.INLINE);

        FormGroup formGroupBus = new FormGroup();
        final TextBox txtBus = new TextBox();
        txtBus.setPlaceholder("bus number");
        formGroupBus.add(txtBus);
        form.add(formGroupBus);

        FormGroup formGroupAddress = new FormGroup();
        final TextBox txtAddress = new TextBox();
        txtAddress.setPlaceholder("address");
        formGroupBus.add(txtAddress);
        form.add(formGroupAddress);

        FormGroup formGroupData = new FormGroup();
        final TextBox txtData = new TextBox();

        txtData.setPlaceholder("data");
        formGroupBus.add(txtData);
        form.add(formGroupData);

        modalBody.add(form);
        add(modalBody);

        KeyPressHandler keyPressHandler = new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    sendData(txtAddress, txtBus, txtData);
                }

            }
        };
        txtAddress.addKeyPressHandler(keyPressHandler);
        txtBus.addKeyPressHandler(keyPressHandler);
        txtData.addKeyPressHandler(keyPressHandler);

        ModalFooter modalFooter = new ModalFooter();
        Button btnOk = new Button("Send", new ClickHandler() {
            public void onClick(ClickEvent event) {
                sendData(txtAddress, txtBus, txtData);

            }
        });
        btnOk.setType(ButtonType.PRIMARY);
        btnOk.setPull(Pull.RIGHT);
        modalFooter.add(btnOk);

        Button btnClose = new Button("Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        btnClose.setType(ButtonType.DANGER);
        btnClose.setPull(Pull.LEFT);
        modalFooter.add(btnClose);
        add(modalFooter);

    }

    private void sendData(TextBox txtAddress, TextBox txtBus, TextBox txtData) {
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
}
