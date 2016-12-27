package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Spy;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

import java.util.Collection;

/**
 * Modal to select the playback file to start the playback.
 *
 * @author Daniel Tuerk
 */
public class PlayerModal extends Modal {

    private final FlowPanel content;

    public PlayerModal() {
        setFade(true);
        setTitle("Bus Data Player");

        ModalBody modalBody = new ModalBody();

        content = new FlowPanel();
        content.setDataSpy(Spy.SCROLL);

        modalBody.add(content);
        add(modalBody);

        ModalFooter modalFooter = new ModalFooter();
        Button btnClose = new Button("Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        btnClose.setType(ButtonType.DANGER);
        btnClose.setPull(Pull.LEFT);
        modalFooter.add(btnClose);
        add(modalFooter);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        content.clear();

        RequestUtils.getInstance().getBusRequest().getRecords(new AsyncCallback<Collection<String>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Collection<String> response) {
                for (final String name : response) {
                    Button btnRecord = new Button(name);
                    btnRecord.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            RequestUtils.getInstance().getBusRequest().startPlayer(name, RequestUtils.VOID_ASYNC_CALLBACK);
                            hide();
                        }
                    });
                    content.add(btnRecord);
                }
            }
        });
    }
}
