package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.Pull;

/**
 * @author Daniel Tuerk
 */
public class EditWidgetDoubleClickHandler implements DoubleClickHandler {

    private final EditTrackWidgetHandler handler;

    public EditWidgetDoubleClickHandler(EditTrackWidgetHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onDoubleClick(DoubleClickEvent event) {
        Modal modal = createModal();
        modal.show();
    }

    private Modal createModal() {
        // Create a dialog box and set the caption text
        final Modal modal = new Modal();
        modal.setFade(true);
        modal.setTitle("Edit");

        ModalBody modalBody = new ModalBody();
        Widget contentPanel = handler.getDialogContent();
        modalBody.add(contentPanel);
        modal.add(modalBody);

        ModalFooter modalFooter = new ModalFooter();
        Button btnOk = new Button("Ok", new ClickHandler() {
            public void onClick(ClickEvent event) {
                handler.onConfirmCallback();
                modal.hide();
            }
        });
        btnOk.setType(ButtonType.PRIMARY);
        btnOk.setPull(Pull.RIGHT);
        modalFooter.add(btnOk);

        Button btnClose = new Button("Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                modal.hide();
            }
        });
        btnClose.setType(ButtonType.DANGER);
        btnClose.setPull(Pull.LEFT);
        modalFooter.add(btnClose);
        modal.add(modalFooter);

        return modal;
    }
}
