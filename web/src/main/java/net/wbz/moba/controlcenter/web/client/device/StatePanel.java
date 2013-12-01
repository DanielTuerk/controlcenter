package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.DeviceInfo;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class StatePanel extends HorizontalPanel {

    private final DeviceListBox deviceListBox = new DeviceListBox();
    private final BusConnectionToggleButton busConnectionToggleButton = new BusConnectionToggleButton(deviceListBox);

    @Override
    protected void onLoad() {
        setStyleName("statePanel");
        setSpacing(10);



        add(busConnectionToggleButton);
        add(deviceListBox);

        final DialogBox configureDeviceDialog  = createDialogBox();

        Button configButton = new Button("Conifig");
        configButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                configureDeviceDialog.center();
                configureDeviceDialog.show();
            }
        });
        add(configButton);

        add(new Label("SX-Bus"));
        add(new Label("v0.01.alpha"));
    }


    /**
     * Create the dialog box for this example.
     *
     * @return the new dialog box
     */
    private DialogBox createDialogBox() {
        // Create a dialog box and set the caption text
        final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Configure Device");

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        dialogContents.add(new Label("Key: "));
        final TextBox txtKey = new TextBox();
        dialogContents.add(txtKey);

        // Add a close button at the bottom of the dialog
        Button btnConfirm = new Button(
                "Confirm", new ClickHandler() {
            public void onClick(ClickEvent event) {

                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setType(DeviceInfo.DEVICE_TYPE.COM1);
                deviceInfo.setKey(txtKey.getValue());

                ServiceUtils.getBusService().createDevice(deviceInfo, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        dialogBox.hide();
                    }

                    @Override
                    public void onSuccess(Void result) {
                        dialogBox.hide();

                        deviceListBox.reload();
                    }
                });


            }
        });
        dialogContents.add(btnConfirm);


        // Add a close button at the bottom of the dialog
        Button closeButton = new Button(
                "Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });
        dialogContents.add(closeButton);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            dialogContents.setCellHorizontalAlignment(
                    closeButton, HasHorizontalAlignment.ALIGN_LEFT);

        } else {
            dialogContents.setCellHorizontalAlignment(
                    closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
        }

        // Return the dialog box
        return dialogBox;
    }
}
