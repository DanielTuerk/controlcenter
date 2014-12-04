package net.wbz.moba.controlcenter.web.client.viewer.bus;

import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;

import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.Well;
import org.gwtbootstrap3.client.ui.constants.WellSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusMonitorPanel extends FlowPanel {

	private final Map<Integer, Map<Integer, BusAddressItemPanel>> addressItemMapping = Maps
			.newHashMap();
	private RemoteEventListener listener;
	private RemoteEventListener connectionListener;
	private List<Panel> busPanels = new ArrayList<>();
	final Well well = new Well();
	final Label lbl = new Label();

	public BusMonitorPanel() {
		super();
		lbl.setText("Sorry, there is no connection.Please connect.");
		lbl.getElement().getStyle().setFloat(Float.NONE);
		lbl.getElement().getStyle().setColor("Black");
		well.setSize(WellSize.LARGE);
		well.add(lbl);
		well.setHeight("70px");
		well.setWidth("360px");
		well.getElement().getStyle().setMarginLeft(480, Unit.PX);
		well.getElement().getStyle().setMarginTop(280, Unit.PX);
		
		setHeight("100%");
		for (int i = 0; i < 2; i++) {
			Panel panel = new org.gwtbootstrap3.client.ui.Panel();
			panel.setWidth("48%");
			panel.getElement().getStyle().setFloat(Style.Float.LEFT);
			panel.getElement().getStyle().setMargin(1, Style.Unit.PCT);

			PanelHeader panelHeader = new PanelHeader();
			panelHeader.setText("Bus " + i);
			PanelBody panelBody = new PanelBody();
			panel.add(panelHeader);
			panel.add(panelBody);
			busPanels.add(panel);

			Map<Integer, BusAddressItemPanel> addressItemPanelMap = Maps
					.newHashMap();

			for (int j = 0; j < 127; j++) {
				BusAddressItemPanel busAddressItemPanel = new BusAddressItemPanel(
						i, j);
				busAddressItemPanel.getElement().getStyle()
						.setPaddingRight(5, Style.Unit.PX);
				busAddressItemPanel.getElement().getStyle()
						.setPaddingBottom(5, Style.Unit.PX);
				panelBody.add(busAddressItemPanel);

				addressItemPanelMap.put(j, busAddressItemPanel);
			}
			addressItemMapping.put(i, addressItemPanelMap);
		}

		listener = new RemoteEventListener() {
			public void apply(Event anEvent) {
				BusDataEvent busDataEvent = (BusDataEvent) anEvent;
				addressItemMapping.get(busDataEvent.getBus())
						.get(busDataEvent.getAddress())
						.updateData(busDataEvent.getData());
			}
		};
		connectionListener = new RemoteEventListener() {
			@Override
			public void apply(Event event) {
				DeviceInfoEvent deviceInfoEvent = (DeviceInfoEvent) event;

				if (deviceInfoEvent.getEventType() == DeviceInfoEvent.TYPE.CONNECTED) {

					ServiceUtils.getBusService().startTrackingBus(
							new EmptyCallback<Void>());
					remove(well);
					onLoad();

				} else if (deviceInfoEvent.getEventType() == DeviceInfoEvent.TYPE.DISCONNECTED) {
					ServiceUtils.getBusService().stopTrackingBus(
							new EmptyCallback<Void>());
					removePanel();
					add(well);
				}
			}
		};
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		EventReceiver.getInstance().addListener(DeviceInfoEvent.class,
				connectionListener);

		ServiceUtils.getBusService().isBusConnected(
				new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Boolean result) {
						if (result) {

							remove(well);
							addPanel();

						} else {
							removePanel();
							add(well);
						}
					}
				});

		ServiceUtils.getBusService()
				.startTrackingBus(new EmptyCallback<Void>());
		EventReceiver.getInstance().addListener(BusDataEvent.class, listener);

	}

	@Override
	protected void onUnload() {
		super.onUnload();
		ServiceUtils.getBusService().stopTrackingBus(new EmptyCallback<Void>());
		EventReceiver.getInstance()
				.removeListener(BusDataEvent.class, listener);
		EventReceiver.getInstance().removeListener(DeviceInfoEvent.class,
				connectionListener);
		removePanel();
	}

	public void removePanel() {
		for (Panel busPanel : busPanels) {
			remove(busPanel);
		}
	}

	public void addPanel() {
		for (Panel busPanel : busPanels) {
			add(busPanel);
		}
	}
}
