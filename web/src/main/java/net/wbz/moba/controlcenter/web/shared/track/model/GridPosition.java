package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class GridPosition implements IsSerializable,Serializable {

    private int x;
    private int y;

    public GridPosition(){

    }

    public GridPosition(int x, int y) {
        this.x=x;
        this.y=y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
