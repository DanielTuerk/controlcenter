package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.device.BusConnection;
import net.wbz.moba.controlcenter.web.client.device.BusConnectionListener;
import net.wbz.moba.controlcenter.web.client.editor.track.ClickActionViewerWidgetHandler;
import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link net.wbz.moba.controlcenter.web.client.model.track.AbstractImageTrackWidget} with click control
 * to toggle the state of the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
abstract public class AbstractControlImageTrackWidget<T extends TrackPart> extends AbstractImageTrackWidget<T>
        implements EditTrackWidgetHandler, ClickActionViewerWidgetHandler {

    //protected static final Logger LOG = LoggerFactory.getLogger(AbstractControlImageTrackWidget.class);

    private final ListBox selectBit = new ListBox();
    private final TextBox txtAdress = new TextBox();

    private int trackPartConfigAdress = -1;
    private int trackPartConfigBit = -1;

    public AbstractControlImageTrackWidget() {
        // repaint the widget by change of the device connection state
        BusConnection.getInstance().addListener(new BusConnectionListener() {
            @Override
            public void connected() {
                repaint();
            }

            @Override
            public void disconnected() {
                //To change body of implemented methods use File | Settings | File Templates.
                // TODO disable widget?
            }
        });
    }

    @Override
    public void repaint() {
        Configuration widgetConfig = getStoredWidgetConfiguration();
        if (widgetConfig.isValid()) {
            ServiceUtils.getBusService().isBusConnected(new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable caught) {
                   //LOG.error("can't request bus state",caught);
                }

                @Override
                public void onSuccess(Boolean connected) {
                    if (connected) {
                        ServiceUtils.getTrackViewerService().getTrackPartState(getStoredWidgetConfiguration(), new AsyncCallback<Boolean>() {

                            @Override
                            public void onFailure(Throwable caught) {
                             //LOG.error("can't load track part state",caught);
                            }

                            @Override
                            public void onSuccess(Boolean result) {
                                trackPartState = result;
                                changeState();
                            }
                        });
                    }
                }
            });
        } else {
            //LOG.error("widget has no track part config: " + getClass().getName());
        }
    }

    @Override
    public void onClick() {
        Configuration widgetConfiguration = getStoredWidgetConfiguration();
        ServiceUtils.getTrackViewerService().toogleTrackPart(widgetConfiguration, !trackPartState, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Void result) {
                toggleState();

            }
        });
    }


    private boolean trackPartState = true;

    private void toggleState() {
        trackPartState = !trackPartState;
        changeState();
    }

    private void changeState() {
        if (!trackPartState) {
            setUrl(getImageUrl());
        } else {
            setUrl(getActiveStateImageUrl());
        }
    }

    public abstract String getActiveStateImageUrl();

    public Configuration getStoredWidgetConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setAddress(trackPartConfigAdress);
        configuration.setOutput(trackPartConfigBit);
        return configuration;
    }

    @Override
    public VerticalPanel getDialogContent() {
        FlexTable layout = new FlexTable();
        layout.setCellSpacing(6);
        FlexTable.FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();

        // Add a title to the form
        layout.setHTML(0, 0, "Module Adress");
        cellFormatter.setColSpan(0, 0, 2);
        cellFormatter.setHorizontalAlignment(
                0, 0, HasHorizontalAlignment.ALIGN_CENTER);

        if (trackPartConfigAdress >= 0) {
            txtAdress.setText(String.valueOf(trackPartConfigAdress));
        }

        for (int index = 0; index < 8; index++) {
            selectBit.addItem(String.valueOf(index + 1));
            if (index + 1 == trackPartConfigBit) {
                selectBit.setSelectedIndex(index);
            }
        }

        // Add some standard form options
        layout.setHTML(1, 0, "Adress:");
        layout.setWidget(1, 1, txtAdress);
        layout.setHTML(2, 0, "Bit:");
        layout.setWidget(2, 1, selectBit);

        // Wrap the content in a DecoratorPanel
        DecoratorPanel decPanel = new DecoratorPanel();
        decPanel.setWidget(layout);

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(decPanel);

        return verticalPanel;
    }

    @Override
    public void onConfirmCallback() {
        trackPartConfigAdress = Integer.parseInt(txtAdress.getText());
        trackPartConfigBit = Integer.parseInt(selectBit.getItemText(selectBit.getSelectedIndex()));
    }

    @Override
    public void initFromTrackPart(T trackPart) {
        if (trackPart != null && trackPart.getConfiguration() != null) {
            trackPartConfigAdress = trackPart.getConfiguration().getAddress();
            trackPartConfigBit = trackPart.getConfiguration().getOutput();

            setAltText(trackPart.getConfiguration().toString());
        }
    }

    public void setTrackPartConfigAdress(int trackPartConfigAdress) {
        this.trackPartConfigAdress = trackPartConfigAdress;
    }

    public void setTrackPartConfigBit(int trackPartConfigBit) {
        this.trackPartConfigBit = trackPartConfigBit;
    }


}
