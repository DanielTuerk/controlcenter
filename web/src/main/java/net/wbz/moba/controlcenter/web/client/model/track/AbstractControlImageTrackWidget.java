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

/**
 * A {@link net.wbz.moba.controlcenter.web.client.model.track.AbstractImageTrackWidget} with click control
 * to toggle the state of the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractControlImageTrackWidget<T extends TrackPart> extends AbstractImageTrackWidget<T>
        implements ClickActionViewerWidgetHandler {



    private boolean trackPartState = true;

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
    public void repaint(boolean state) {
        if (trackPartState != state) {
            trackPartState = state;
            changeState();
        }
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
//                                changeState();
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
        ServiceUtils.getTrackViewerService().toggleTrackPart(widgetConfiguration, !trackPartState, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Void result) {
                //toggleState();

            }
        });
    }

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


}
