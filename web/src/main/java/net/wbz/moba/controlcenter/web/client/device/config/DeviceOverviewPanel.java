package net.wbz.moba.controlcenter.web.client.device.config;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.components.table.DeleteButtonColumn;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.device.RemoteDeviceListener;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

/**
 * Panel with overview of the existing {@link DeviceInfo}s.
 *
 * @author Daniel Tuerk
 */
class DeviceOverviewPanel extends Composite {

    private static DeviceOverviewPanel.Binder UI_BINDER = GWT.create(DeviceOverviewPanel.Binder.class);
    private final RemoteDeviceListener remoteDeviceListener;
    private final ListDataProvider<DeviceInfo> dataProvider = new ListDataProvider<>();

    @UiField
    CellTable<DeviceInfo> table;

    DeviceOverviewPanel() {
        initWidget(UI_BINDER.createAndBindUi(this));
        initDeviceTable();

        remoteDeviceListener = new RemoteDeviceListener() {
            @Override
            public void devicesModified(DeviceInfo deviceInfo) {
                reloadDeviceList();
            }

            @Override
            public void deviceCreated(DeviceInfo deviceInfoEvent) {
                reloadDeviceList();
            }

            @Override
            public void deviceRemoved(DeviceInfo deviceInfoEvent) {
                reloadDeviceList();
            }
        };

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        reloadDeviceList();
        EventReceiver.getInstance().addListener(remoteDeviceListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(remoteDeviceListener);
    }

    private void initDeviceTable() {
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
            public String getValue(DeviceInfo deviceInfo) {
                return deviceInfo.getType().name();
            }
        }, "Type");

        table.addColumn(new DeleteButtonColumn<DeviceInfo>() {
            @Override
            public void onAction(DeviceInfo deviceInfo) {
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
                dataProvider.getList().addAll(response);
            }
        });

    }

    interface Binder extends UiBinder<Widget, DeviceOverviewPanel> {

    }

}
