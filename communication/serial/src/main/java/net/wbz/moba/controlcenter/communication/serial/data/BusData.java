package net.wbz.moba.controlcenter.communication.serial.data;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusData {
    private int bus;
    private int address;
    private int data;

    public BusData(int bus, int address, int data) {
        this.bus = bus;
        this.address = address;
        this.data = data;
    }

    public int getBus() {
        return bus;
    }

    public void setBus(int bus) {
        this.bus = bus;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
