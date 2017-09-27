package net.wbz.moba.controlcenter.web.client.components;

import java.util.Collection;

import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Select component for {@link Train}s.
 * 
 * @author Daniel Tuerk
 */
public class TrainSelect extends AbstractLoadableSelect<Train> {

    @Override
    protected String getKey(Train object) {
        return String.valueOf(object.getId());
    }

    @Override
    protected void loadChoices(OnlySuccessAsyncCallback<Collection<Train>> callback) {
        RequestUtils.getInstance().getTrainEditorService().getTrains(callback);
    }

    @Override
    protected String getDisplayValue(Train choice) {
        return choice.getName();
    }
}
