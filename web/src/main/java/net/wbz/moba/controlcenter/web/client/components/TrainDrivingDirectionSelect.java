package net.wbz.moba.controlcenter.web.client.components;

import java.util.Collection;

import com.google.common.collect.Lists;

import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
public class TrainDrivingDirectionSelect extends AbstractEnumSelect<Train.DRIVING_DIRECTION> {

    @Override
    Collection<Train.DRIVING_DIRECTION> getChoices() {
        return Lists.newArrayList(Train.DRIVING_DIRECTION.values());
    }
}
