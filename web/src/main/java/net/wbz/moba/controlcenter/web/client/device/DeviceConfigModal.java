package net.wbz.moba.controlcenter.web.client.device;

import java.util.List;
import java.util.Map;

import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.Log;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoProxy;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gwtbootstrap3.extras.growl.client.ui.Growl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

/**
 * Modal to configure the {@link net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo}s for the connections.
 *
 * @author Daniel Tuerk
 */
public class DeviceConfigModal extends Modal {

    /**
     * Hold {@link com.google.gwt.event.dom.client.ClickHandler} for the delete button of each
     * {@link net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo}.
     * Used to call the handler from the column of the {@link org.gwtbootstrap3.client.ui.gwt.CellTable}.
     */
    private final Map<DeviceInfoProxy, ClickHandler> btnDeleteActions = Maps.newConcurrentMap();
    private ListDataProvider<DeviceInfoProxy> dataProvider;

    public DeviceConfigModal() {
        super();

        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, new RemoteEventListener() {
            @Override
            public void apply(Event event) {
                reloadDeviceList();
            }
        });

        setTitle("Configure Device");

        ModalBody modalBody = new ModalBody();
        FlowPanel contentPanel = new FlowPanel();
        contentPanel.add(getCreateDevicePanel());
        contentPanel.add(getEditDevicePanel());
        modalBody.add(contentPanel);
        add(modalBody);

        ModalFooter modalFooter = new ModalFooter();
        Button btnClose = new Button(
                "Close", new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        DeviceConfigModal.this.hide();
                    }
                });
        modalFooter.add(btnClose);
        add(modalFooter);

    }

    @Override
    protected void onLoad() {
        super.onLoad();

        reloadDeviceList();
    }

    private Panel getCreateDevicePanel() {
        Panel createDevicePanel = new Panel();

        PanelHeader panelHeader = new PanelHeader();
        panelHeader.setText("Create Device");
        createDevicePanel.add(panelHeader);

        PanelBody panelBody = new PanelBody();

        Form createForm = new Form();
        // createForm.setType(FormType.VERTICAL);
        FormLabel formLabel = new FormLabel();
        formLabel.setText("Key:");
        createForm.add(formLabel);
        final TextBox txtDeviceName = new TextBox();
        createForm.add(txtDeviceName);
        Button btnCreateDevice = new Button("Create");
        btnCreateDevice.addClickHandler(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        // TODO validation feedback on dialog
                        if (!Strings.isNullOrEmpty(txtDeviceName.getText())) {
                            final DeviceInfoProxy deviceInfo = ServiceUtils.getInstance().getBusService().create(
                                    DeviceInfoProxy.class);
                            if ("test".equals(txtDeviceName.getText())) {
                                deviceInfo.setType(DeviceInfo.DEVICE_TYPE.TEST);
                            } else {
                                deviceInfo.setType(DeviceInfo.DEVICE_TYPE.SERIAL);
                            }
                            deviceInfo.setKey(txtDeviceName.getValue());

                            ServiceUtils.getInstance().getBusService().createDevice(deviceInfo).fire(
                                    new Receiver<Void>() {
                                @Override
                                public void onSuccess(Void response) {
                                    Growl.growl("", "Device " + deviceInfo.getKey() + " created", IconType.INFO);
                                }
                            });
                        }
                    }
                });
        createForm.add(btnCreateDevice);

        panelBody.add(createForm);
        createDevicePanel.add(panelBody);

        return createDevicePanel;
    }

    public Panel getEditDevicePanel() {
        Panel editDevicePanel = new Panel();

        PanelHeader panelHeader = new PanelHeader();
        panelHeader.setText("Edit Device");
        editDevicePanel.add(panelHeader);

        PanelBody panelBody = new PanelBody();

        CellTable<DeviceInfoProxy> cellTable = new CellTable<>();

        TextColumn<DeviceInfoProxy> columnDeviceKey = new TextColumn<DeviceInfoProxy>() {
            @Override
            public String getValue(DeviceInfoProxy contact) {
                return contact.getKey();
            }
        };
        columnDeviceKey.setSortable(true);

        cellTable.addColumn(columnDeviceKey, "Key");
        cellTable.addColumn(new TextColumn<DeviceInfoProxy>() {
            @Override
            public String getValue(DeviceInfoProxy contact) {
                return contact.getType().name();
            }
        }, "Type");

        Column<DeviceInfoProxy, String> columnDeleteDevice = new Column<DeviceInfoProxy, String>(new ButtonCell(
                ButtonType.DANGER,
                IconType.TRASH)) {
            @Override
            public String getValue(DeviceInfoProxy object) {
                return "";
            }
        };
        columnDeleteDevice.setFieldUpdater(new FieldUpdater<DeviceInfoProxy, String>() {
            @Override
            public void update(int index, DeviceInfoProxy object, String value) {
                if (btnDeleteActions.containsKey(object)) {
                    btnDeleteActions.get(object).onClick(null);
                } else {
                    Log.warn("no action found for device: " + object);
                }
            }
        });
        cellTable.addColumn(columnDeleteDevice, "");

        // Create a data provider.
        dataProvider = new ListDataProvider<>();
        // Connect the table to the data provider.
        dataProvider.addDataDisplay(cellTable);

        panelBody.add(cellTable);
        editDevicePanel.add(panelBody);

        return editDevicePanel;
    }

    private void reloadDeviceList() {
        ServiceUtils.getInstance().getBusService().getDevices().fire(new Receiver<List<DeviceInfoProxy>>() {
            @Override
            public void onSuccess(List<DeviceInfoProxy> response) {
                // reset devices to load fresh list
                dataProvider.getList().clear();
                btnDeleteActions.clear();

                // add delete action for the device entry in the list
                dataProvider.getList().addAll(response);
                for (final DeviceInfoProxy deviceInfo : response) {
                    btnDeleteActions.put(deviceInfo, new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            ServiceUtils.getInstance().getBusService().deleteDevice(deviceInfo).fire(
                                    new Receiver<Void>() {
                                @Override
                                public void onSuccess(Void response) {
                                    Growl.growl("", "Device " + deviceInfo.getKey() + " deleted", IconType.INFO);
                                }

                                @Override
                                public void onFailure(ServerFailure error) {
                                    Growl.growl("", "Can't delete device: " + deviceInfo.getKey(), IconType.INFO);
                                }
                            });
                        }
                    });
                }
            }
        });

    }

}
