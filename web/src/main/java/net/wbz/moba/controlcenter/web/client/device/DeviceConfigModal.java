package net.wbz.moba.controlcenter.web.client.device;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.AlertUtil;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 10.04.2014.
 */
public class DeviceConfigModal extends Modal {

    private final List<Widget> rows = Lists.newArrayList();
    private final FlowPanel statusMessagePanel;
    private Column devicesColumn;

    public DeviceConfigModal() {
        super();

        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, new RemoteEventListener() {
            @Override
            public void apply(Event event) {
                reloadDeviceList();
            }
        });

        setTitle("Configure Device");

        statusMessagePanel = AlertUtil.createAlertContainer();
        add(statusMessagePanel);

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
        ModalFooter modalFooter = new ModalFooter();
        modalFooter.add(btnClose);
        add(modalFooter);

    }


    @Override
    protected void onLoad() {
    }

    private FlowPanel getCreateDevicePanel() {
        FlowPanel createDevicePanel = new FlowPanel();
        PageHeader createDeviceHeader = new PageHeader();
        createDeviceHeader.setText("Create");
        createDevicePanel.add(createDeviceHeader);
        Form createForm = new Form();
//        createForm.setType(FormType.VERTICAL);
        FormLabel formLabel = new FormLabel();
        formLabel.setText("Key:");
        createForm.add(formLabel);
        final TextBox txtDeviceName = new TextBox();
        createForm.add(txtDeviceName);
        Button btnCreateDevice = new Button("Create");
//        btnCreateDevice.setText("Create");
        btnCreateDevice.addClickHandler(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        //TODO validation feedback on dialog
                        if (!Strings.isNullOrEmpty(txtDeviceName.getText())) {
                            DeviceInfo deviceInfo = new DeviceInfo();
                            if ("test".equals(txtDeviceName.getText())) {
                                deviceInfo.setType(DeviceInfo.DEVICE_TYPE.TEST);
                            } else {
                                deviceInfo.setType(DeviceInfo.DEVICE_TYPE.SERIAL);
                            }
                            deviceInfo.setKey(txtDeviceName.getValue());

                            ServiceUtils.getBusService().createDevice(deviceInfo, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        DeviceConfigModal.this.hide();
                    }

                    @Override
                    public void onSuccess(Void result) {
                        DeviceConfigModal.this.hide();
                        reloadDeviceList();
                    }
                });
                }
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

        Row row = new Row();
        devicesColumn = new Column(ColumnSize.LG_3, new Label("Devices"));
        row.add(devicesColumn);
        editDevicePanel.add(row);

        reloadDeviceList();
        return editDevicePanel;
    }

    private void reloadDeviceList() {
        for (Widget row : rows) {
            devicesColumn.remove(row);
        }
        rows.clear();

        ServiceUtils.getBusService().getDevices(new AsyncCallback<ArrayList<DeviceInfo>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(ArrayList<DeviceInfo> result) {
                for (final DeviceInfo deviceInfo : result) {
                    Row row = new Row();
                    row.add(new Column(ColumnSize.LG_1, new Label(deviceInfo.getKey())));
                    row.add(new Column(ColumnSize.LG_1, new Label(deviceInfo.getType().name())));
                    Button btnDeleteDevice = new Button("delete", new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            ServiceUtils.getBusService().deleteDevice(deviceInfo, new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    AlertUtil.showAlert(statusMessagePanel, "Can't delete device: " + deviceInfo.getKey(), AlertType.DANGER);
                                }

                                @Override
                                public void onSuccess(Void result) {
                                    AlertUtil.showAlert(statusMessagePanel, "Device " + deviceInfo.getKey() + " deleted", AlertType.SUCCESS);
                                }
                            });
                        }
                    });
                    row.add(new Column(ColumnSize.LG_1, btnDeleteDevice));

                    rows.add(row);
                    devicesColumn.add(row);
                }
            }
        });
    }
}
