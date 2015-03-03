package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Spy;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

import java.util.List;

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

        ServiceUtils.getBusService().getRecords(new AsyncCallback<List<String>>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<String> result) {
                for (final String name : result) {
                    Button btnRecord = new Button(name);
                    btnRecord.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            ServiceUtils.getBusService().startPlayer(name, new EmptyCallback<Void>());
                            hide();
                        }
                    });
                    content.add(btnRecord);
                }
            }
        });
    }
}
