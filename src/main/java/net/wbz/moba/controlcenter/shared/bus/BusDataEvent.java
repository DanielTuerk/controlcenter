package net.wbz.moba.controlcenter.shared.bus;

import java.util.Objects;
import net.wbz.moba.controlcenter.shared.StateEvent;

/**
 * @author Daniel Tuerk
 */
public class BusDataEvent implements StateEvent {

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
    public String getCacheKey() {
        return getClass().getName() + ":" + bus + "-" + address;
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
        return bus == that.bus && address == that.address && data == that.data;
    }

    @Override
    public int hashCode() {

        return Objects.hash(bus, address, data);
    }

    @Override
    public String toString() {
        return "BusDataEvent{" + "bus=" + bus + ", address=" + address + ", data=" + data + '}';
    }
}
