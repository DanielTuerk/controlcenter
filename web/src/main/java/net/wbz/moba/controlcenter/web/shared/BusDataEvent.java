package net.wbz.moba.controlcenter.web.shared;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class BusDataEvent implements Event {
    private int bus;
    private int address;
    private int data;

    public BusDataEvent() {
    }

    public BusDataEvent(int bus, int address, int data) {
        this.bus = bus;
        this.address = address;
        this.data = data;
    }

    public int getBus() {
        return bus;
    }

    public int getAddress() {
        return address;
    }

    public int getData() {
        return data;
    }
}
