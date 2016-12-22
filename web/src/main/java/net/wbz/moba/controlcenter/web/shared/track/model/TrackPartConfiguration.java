package net.wbz.moba.controlcenter.web.shared.track.model;

/**
 * @author Daniel Tuerk
 */
public class TrackPartConfiguration extends AbstractDto {

    private int bus;

    private int address;

    private int bit;

    private boolean bitState;

    public TrackPartConfiguration(int bus, int address, int bit, boolean bitState) {
        this.bus=bus;
        this.address=address;
        this.bit=bit;
        this.bitState=bitState;
    }

    public TrackPartConfiguration() {
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

    public int getBit() {
        return bit;
    }

    public void setBit(int bit) {
        this.bit = bit;
    }

    public boolean isBitState() {
        return bitState;
    }

    public void setBitState(boolean bitState) {
        this.bitState = bitState;
    }

    public boolean isValid() {
        return address > 0 && bit > 0 && bus >-1;
    }
}
