package net.wbz.moba.controlcenter;

import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;

/**
 * TODO: migrate
 *
 * Identifier for a address of in a bus.
 *
 * @author Daniel Tuerk
 */
public class BusAddressIdentifier {
    private final int bus;
    private final int address;

    /**
     * Create new identifier.
     *
     * @param bus bus number
     * @param address address number
     */
    public BusAddressIdentifier(int bus, int address) {
        this.bus = bus;
        this.address = address;
    }

    public BusAddressIdentifier(BusDataConfiguration blockFunction) {
        this(blockFunction.getBus(), blockFunction.getAddress());
    }

    public int getBus() {
        return bus;
    }

    public int getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BusAddressIdentifier that = (BusAddressIdentifier) o;

        if (address != that.address)
            return false;
        return bus == that.bus;

    }

    @Override
    public int hashCode() {
        int result = bus;
        result = 31 * result + address;
        return result;
    }
}