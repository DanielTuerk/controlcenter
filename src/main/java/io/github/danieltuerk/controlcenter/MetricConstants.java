package io.github.danieltuerk.controlcenter;

/**
 * Names of the custom Micrometer metrics exposed at {@code /q/metrics}.
 *
 * @author Daniel Tuerk
 */
public final class MetricConstants {

    private MetricConstants() {
    }

    /**
     * Counter: number of websocket connections opened by clients.
     */
    public static final String WEBSOCKET_CONNECTIONS_OPENED_TOTAL = "controlcenter_websocket_connections_opened_total";

    /**
     * Counter: number of websocket connections closed by clients.
     */
    public static final String WEBSOCKET_CONNECTIONS_CLOSED_TOTAL = "controlcenter_websocket_connections_closed_total";

    /**
     * Counter: number of events broadcast over the websocket, tagged by event type ("type").
     */
    public static final String EVENTS_BROADCAST_TOTAL = "controlcenter_events_broadcast_total";

    /**
     * Gauge: whether the active device is currently connected (0/1).
     */
    public static final String DEVICE_CONNECTED = "controlcenter_device_connected";

    /**
     * Counter: number of successful connect() calls on the active SX1 device.
     */
    public static final String DEVICE_CONNECT_TOTAL = "controlcenter_device_connect_total";

    /**
     * Counter: number of successful disconnect() calls on the active SX1 device.
     */
    public static final String DEVICE_DISCONNECT_TOTAL = "controlcenter_device_disconnect_total";

    /**
     * Counter: number of scenarios started.
     */
    public static final String SCENARIO_STARTED_TOTAL = "controlcenter_scenario_started_total";

    /**
     * Counter: number of scenarios finished, tagged by outcome ("result": e.g. FINISHED, FAILED, STOPPED).
     */
    public static final String SCENARIO_FINISHED_TOTAL = "controlcenter_scenario_finished_total";

}
