package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

/**
 * Modal to select the playback file to start the playback.
 *
 * @author Daniel Tuerk
 */
public class PlayerModal extends Composite {

    private static final PlayerModal.Binder UI_BINDER = GWT.create(PlayerModal.Binder.class);

    @UiField
    FlowPanel recordsContainer;
    @UiField
    Modal modal;
    @UiField
    Input playbackSpeed;

    public PlayerModal() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    @UiHandler("btnClose")
    void close(ClickEvent event) {
        close();
    }

    public void show() {
        recordsContainer.clear();

        RequestUtils.getInstance().getBusService().getRecords(new AsyncCallback<Collection<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                Notify.notify("", "Can't load records: " + caught.getMessage(), IconType.INBOX);
            }

            @Override
            public void onSuccess(Collection<String> response) {
                for (final String name : response) {
                    Button btnRecord = new Button(name);
                    btnRecord.addClickHandler(event -> {
                        RequestUtils.getInstance().getBusService().startPlayer(name, getPlaybackSpeed(),
                            RequestUtils.VOID_ASYNC_CALLBACK);
                        close();
                    });
                    recordsContainer.add(btnRecord);
                }
            }
        });

        modal.show();
    }

    private int getPlaybackSpeed() {
        return Integer.parseInt(playbackSpeed.getText());
    }

    private void close() {
        modal.hide();
    }

    interface Binder extends UiBinder<Widget, PlayerModal> {

    }
}
