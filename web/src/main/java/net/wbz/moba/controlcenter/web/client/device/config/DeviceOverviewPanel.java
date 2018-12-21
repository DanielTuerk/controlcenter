package net.wbz.moba.controlcenter.web.client.device.config;

import com.google.common.collect.Maps;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Collection;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.event.device.RemoteConnectionListener;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.util.Log;
import net.wbz.moba.controlcenter.web.server.persist.device.DeviceInfoEntity;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

/**
 * @author Daniel Tuerk
 */
class DeviceOverviewPanel extends Composite {

    private static DeviceOverviewPanel.Binder UI_BINDER = GWT.create(DeviceOverviewPanel.Binder.class);
    /**
     * Hold {@link com.google.gwt.event.dom.client.ClickHandler} for the delete button of each
     * {@link DeviceInfoEntity}.
     * Used to call the handler from the column of the {@link org.gwtbootstrap3.client.ui.gwt.CellTable}.
     */
    private final Map<DeviceInfo, ClickHandler> btnDeleteActions = Maps.newConcurrentMap();

    @UiField
    CellTable<DeviceInfo> table;
    private ListDataProvider<DeviceInfo> dataProvider;
    private final RemoteConnectionListener remoteConnectionListener;

    DeviceOverviewPanel() {
        initWidget(UI_BINDER.createAndBindUi(this));
        initDeviceTable();

        remoteConnectionListener = new RemoteConnectionListener() {
            @Override
            public void connected(DeviceInfo deviceInfoEvent) {
                reloadDeviceList();
            }

            @Override
            public void disconnected(DeviceInfo deviceInfoEvent) {
                reloadDeviceList();
            }
        };

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        reloadDeviceList();
        EventReceiver.getInstance().addListener(remoteConnectionListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(remoteConnectionListener);
    }

    private void initDeviceTable() {

        // TODO refacror table and delete buttons
        TextColumn<DeviceInfo> columnDeviceKey = new TextColumn<DeviceInfo>() {
            @Override
            public String getValue(DeviceInfo contact) {
                return contact.getKey();
            }
        };
        columnDeviceKey.setSortable(true);

        table.addColumn(columnDeviceKey, "Key");
        table.addColumn(new TextColumn<DeviceInfo>() {
            @Override
            public String getValue(DeviceInfo contact) {
                return contact.getType().name();
            }
        }, "Type");

        Column<DeviceInfo, String> columnDeleteDevice = new Column<DeviceInfo, String>(new ButtonCell(
            ButtonType.DANGER,
            IconType.TRASH)) {
            @Override
            public String getValue(DeviceInfo object) {
                return "";
            }
        };
        columnDeleteDevice.setFieldUpdater(new FieldUpdater<DeviceInfo, String>() {
            @Override
            public void update(int index, DeviceInfo object, String value) {
                if (btnDeleteActions.containsKey(object)) {
                    btnDeleteActions.get(object).onClick(null);
                } else {
                    Log.warn("no action found for device: " + object);
                }
            }
        });
        table.addColumn(columnDeleteDevice, "");

        // Create a data provider.
        dataProvider = new ListDataProvider<>();
        // Connect the table to the data provider.
        dataProvider.addDataDisplay(table);
    }

    private void reloadDeviceList() {
        RequestUtils.getInstance().getBusService().getDevices(new AsyncCallback<Collection<DeviceInfo>>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Collection<DeviceInfo> response) {
                // reset devices to load fresh list
                dataProvider.getList().clear();
                btnDeleteActions.clear();

                // add delete action for the device entry in the list
                dataProvider.getList().addAll(response);
                for (final DeviceInfo deviceInfo : response) {
                    btnDeleteActions.put(deviceInfo, new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            RequestUtils.getInstance().getBusService().deleteDevice(deviceInfo,
                                new AsyncCallback<Void>() {
                                    @Override
                                    public void onFailure(Throwable caught) {
                                        Notify.notify("", "Can't delete device: " + deviceInfo.getKey(),
                                            IconType.WARNING);
                                    }

                                    @Override
                                    public void onSuccess(Void result) {
                                        Notify.notify("", "Device " + deviceInfo.getKey() + " deleted",
                                            IconType.INFO);
                                    }
                                });
                        }
                    });
                }
            }
        });

    }

    interface Binder extends UiBinder<Widget, DeviceOverviewPanel> {

    }

}
