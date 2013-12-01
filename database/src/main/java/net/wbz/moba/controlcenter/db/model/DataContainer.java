package net.wbz.moba.controlcenter.db.model;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class DataContainer {

    private final long dateTime;
    private final Object data;


    public DataContainer(long dateTime, Object data) {
        this.dateTime = dateTime;
        this.data = data;
    }

    public long getDateTime() {
        return dateTime;
    }

    public Object getData() {
        return data;
    }
}
