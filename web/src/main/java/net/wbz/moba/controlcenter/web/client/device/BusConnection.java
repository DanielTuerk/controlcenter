package net.wbz.moba.controlcenter.web.client.device;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusConnection {

    private final static BusConnection INSTANCE = new BusConnection();
    private final List<BusConnectionListener> listeners = Lists.newArrayList();

    private enum STATE {CONNECTED, DISCONNECTED}

    private STATE actualState = STATE.DISCONNECTED;

    private BusConnection() {
        // singleton
    }

    public static BusConnection getInstance() {
        return INSTANCE;
    }

    public void addListener(BusConnectionListener listener) {
        listeners.add(listener);
    }

    public void setConnected() {
        actualState = STATE.CONNECTED;
        callListeners();
    }

    public void setDisconnected() {
        actualState = STATE.DISCONNECTED;
        callListeners();
    }


    private void callListeners() {
        for (BusConnectionListener listener : listeners) {
            switch (actualState) {
                case CONNECTED:
                    listener.connected();
                    break;
                case DISCONNECTED:
                    listener.disconnected();
                    break;
            }
        }
    }


}
