package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
abstract public class AbstractSwitchLeftWidget extends AbstractSwitchWidget {

    @Override
    public String getImageUrl() {
        return "img/widget/track/switch_left_straight.png";
    }


    @Override
    public String getActiveStateImageUrl() {
        return "img/widget/track/switch_left_branch.png";
    }


    @Override
    protected Switch.DIRECTION getDirection() {
        return Switch.DIRECTION.LEFT;
    }
}
