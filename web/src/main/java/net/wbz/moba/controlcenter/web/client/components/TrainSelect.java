package net.wbz.moba.controlcenter.web.client.components;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import javax.validation.constraints.NotNull;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Select component for {@link Train}s.
 * 
 * @author Daniel Tuerk
 */
public class TrainSelect extends AbstractLoadableSelect<Train> {

    @Override
    protected String getKey(@NotNull Train object) {
        return String.valueOf(object.getId());
    }

    @Override
    protected void loadChoices(AsyncCallback<Collection<Train>> callback) {
        RequestUtils.getInstance().getTrainEditorService().getTrains(callback);
    }

    @Override
    protected String getDisplayValue(Train choice) {
        return choice.getName();
    }
}
