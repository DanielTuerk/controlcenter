package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.extras.growl.client.ui.Growl;
import org.gwtbootstrap3.extras.growl.client.ui.GrowlHelper;
import org.gwtbootstrap3.extras.growl.client.ui.GrowlOptions;

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
        ServiceUtils.getBusService().sendBusData(busNr, address, data, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                GrowlOptions growlOptions = GrowlHelper.getNewOptions();
                growlOptions.setDangerType();
                Growl.growl("can't send data: " + caught.getMessage(), growlOptions);
            }

            @Override
            public void onSuccess(Void result) {
                GrowlOptions growlOptions = GrowlHelper.getNewOptions();
                growlOptions.setInfoType();
                Growl.growl("data send - bus: " + busNr + " address: " + address + " data: " + data, growlOptions);
            }
        });
    }
}
