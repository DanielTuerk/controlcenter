package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.train.TrainDataChangedRemoteListener;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractViewerContainer;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Viewer for the {@link Train}s.
 * TODO: reload trains for configuration changes -> verify event thrown for address change
 * 
 * @author Daniel Tuerk
 */
public class TrainViewerControlsPanel extends AbstractViewerContainer {

    private final TrainDataChangedRemoteListener trainDataChangedRemoteListener;

    public TrainViewerControlsPanel() {
        trainDataChangedRemoteListener = event -> reloadItems();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(trainDataChangedRemoteListener);
    }


    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(trainDataChangedRemoteListener);
    }

    @Override
    protected void loadItems() {
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
