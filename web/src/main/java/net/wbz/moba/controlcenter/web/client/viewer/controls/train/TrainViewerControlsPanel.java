package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.train.TrainDataChangedRemoteListener;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.util.Log;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractViewerContainer;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Viewer for the {@link Train}s.
 *
 * @author Daniel Tuerk
 */
public class TrainViewerControlsPanel extends AbstractViewerContainer {

    private final TrainDataChangedRemoteListener trainDataChangedRemoteListener;

    public TrainViewerControlsPanel() {
        super();
        trainDataChangedRemoteListener = event -> reloadItems();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(trainDataChangedRemoteListener);
    }


    @Override
    protected void onUnload() {
        EventReceiver.getInstance().removeListener(trainDataChangedRemoteListener);

        resetItems();

        super.onUnload();
    }

    @Override
    protected void loadItems() {
        Log.info("load items");
        RequestUtils.getInstance().getTrainEditorService().getTrains(new AsyncCallback<Collection<Train>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Collection<Train> result) {
                for (Train train : result) {
                    addItemPanel(new TrainViewerItemPanel(new TrainItemControlsPanel(train)));
                }
            }
        });
    }
}
