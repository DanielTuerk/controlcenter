package net.wbz.moba.controlcenter.api;

import org.junit.jupiter.api.BeforeEach;

public class BaseIt {

    protected static final WebSocketEventReceiver EVENT_RECEIVER = new WebSocketEventReceiver();

    @BeforeEach
    public void before() {
        EVENT_RECEIVER.reset();
    }
}
