package net.wbz.moba.controlcenter.web.shared.bus;

import com.google.common.base.Objects;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BusDataEvent that = (BusDataEvent) o;
        return super.equals(o) && getBus() == that.getBus() &&
                getAddress() == that.getAddress() &&
                getData() == that.getData();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getBus(), getAddress(), getData());
    }

    @Override
    public String toString() {
        return "BusDataEvent{" +
                "bus=" + bus +
                ", address=" + address +
                ", data=" + data +
                '}';
    }
}
