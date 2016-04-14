package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.gwt.user.client.rpc.IsSerializable;
import net.wbz.moba.controlcenter.web.shared.HasVersionAndId;


import javax.persistence.*;
import javax.sound.midi.Track;
import java.io.Serializable;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Entity
public class GridPosition implements HasVersionAndId {

    @Id
    @GeneratedValue
    private long id;

    private int x;
    private int y;

//    @OneToMany(fetch = FetchType.LAZY)
//    private List<TrackPart> trackPart;

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

    @Override
    public Integer getVersion() {
        return 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
