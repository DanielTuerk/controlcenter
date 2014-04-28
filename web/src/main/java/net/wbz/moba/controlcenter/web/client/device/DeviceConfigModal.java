package net.wbz.moba.controlcenter.web.client.device;

import com.github.gwtbootstrap.client.ui.*;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.DeviceInfo;

/**
 * Created by Daniel on 10.04.2014.
 */
public class DeviceConfigModal extends Modal {


    public DeviceConfigModal(final DeviceListBox deviceListBox) {

        setTitle("Configure Device");

//        // Create a table to layout the content
//        VerticalPanel dialogContents = new VerticalPanel();
//        dialogContents.setSpacing(4);
//        setWidget(dialogContents);
//
//        dialogContents.add(new Label("Key: "));
//        final TextBox txtKey = new TextBox();
//        dialogContents.add(txtKey);




//        add(createForm);
//        // Add a close button at the bottom of the dialog
//        Button btnConfirm = new Button(
//                "Confirm", new ClickHandler() {
//            public void onClick(ClickEvent event) {
//
//
//
//
//            }
//        }
//        );
//        dialogContents.add(btnConfirm);


//             // Add a close button at the bottom of the dialog
//        Button closeButton = new Button(
//                "Close", new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                DeviceConfigModal.this.hide();
//            }
//        }
//        );
//        dialogContents.add(closeButton);
//        if (LocaleInfo.getCurrentLocale().isRTL()) {
//            dialogContents.setCellHorizontalAlignment(
//                    closeButton, HasHorizontalAlignment.ALIGN_LEFT);
//
//        } else {
//            dialogContents.setCellHorizontalAlignment(
//                    closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
//        }

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.add(getCreateDevicePanel());
        contentPanel.add(getEditDevicePanel());

        add(contentPanel);


        Button btnClose = new Button(
                "Close", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DeviceConfigModal.this.hide();
            }
        }
        );
        ModalFooter modalFooter = new ModalFooter(btnClose);
        add(modalFooter);
    }


    @Override
    protected void onLoad() {


    }

    private FlowPanel getCreateDevicePanel() {
        FlowPanel createDevicePanel =new FlowPanel();
        createDevicePanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        PageHeader createDeviceHeader = new PageHeader();
        createDeviceHeader.setText("Create");
        createDevicePanel.add(createDeviceHeader);
        WellForm createForm = new WellForm();
        createForm.add(new ControlLabel("Key:"));
        final TextBox txtDeviceName = new TextBox();
        createForm.add(txtDeviceName);
        SubmitButton btnCreateDevice = new SubmitButton("Create", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setType(DeviceInfo.DEVICE_TYPE.COM1);
                deviceInfo.setKey(txtDeviceName.getValue());

                ServiceUtils.getBusService().createDevice(deviceInfo, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        DeviceConfigModal.this.hide();
                    }

                    @Override
                    public void onSuccess(Void result) {
                        DeviceConfigModal.this.hide();

//                        deviceListBox.reload();
                    }
                });
            }
        });

        createForm.add(btnCreateDevice);
        createDevicePanel.add(createForm);
        return createDevicePanel;
    }

    public FlowPanel getEditDevicePanel() {

        FlowPanel editDevicePanel = new FlowPanel();
        editDevicePanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        PageHeader editDeviceHeader = new PageHeader();
        editDeviceHeader.setText("Edit");
        editDevicePanel.add(editDeviceHeader);



        


        return editDevicePanel;
    }
}
