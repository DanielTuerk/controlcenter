package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

import javax.persistence.Entity;

/**
 * @author Daniel Tuerk
 */
@Entity
public class GridPositionEntity extends AbstractEntity {

//    @Id
//    @GeneratedValue
//    private long id;

    private int x;
    private int y;

//    @OneToMany(fetch = FetchType.LAZY)
//    private List<TrackPartEntity> trackPart;

    public GridPositionEntity() {

    }

    public GridPositionEntity(int x, int y) {
        this.x = x;
        this.y = y;
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
