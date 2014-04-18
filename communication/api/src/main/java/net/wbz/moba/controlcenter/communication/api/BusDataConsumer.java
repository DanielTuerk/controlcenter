package net.wbz.moba.controlcenter.communication.api;

/**
 * Consumers are informed by state changes of the configured bus and address.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class BusDataConsumer {

    private  int bus;
    private int address;

    protected BusDataConsumer(int bus, int address) {
        this.bus = bus;
        this.address = address;
    }

    public void setBus(int bus) {
        this.bus = bus;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getBus() {
        return bus;
    }

    public int getAddress() {
        return address;
    }

    abstract public void valueChanged(int value);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BusDataConsumer that = (BusDataConsumer) o;

        if (address != that.address) {
            return false;
        }
        if (bus != that.bus) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = bus;
        result = 31 * result + address;
        return result;
    }
}
