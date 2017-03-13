package net.wbz.moba.controlcenter.web.client.util.modal;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk
 * @deprecated refactor with {@link net.wbz.moba.controlcenter.web.client.components.AbstractEditModal}
 */
@Deprecated
public abstract class AbstractConfirmModal extends Modal {

    public AbstractConfirmModal(String title, String message, String confirmButtonText, IconType confirmButtonIcon,
            String abortButtonText, IconType abortButtonIcon) {
        setFade(true);
        setTitle(title);

        ModalBody modalBody = new ModalBody();
        Widget contentPanel = new HTML(message);
        modalBody.add(contentPanel);
        add(modalBody);

        ModalFooter modalFooter = new ModalFooter();
        Button btnConfirm = new Button(confirmButtonText, confirmButtonIcon, new ClickHandler() {
            public void onClick(ClickEvent event) {
                onConfirm();
                hide();
            }
        });
        btnConfirm.setType(ButtonType.PRIMARY);
        btnConfirm.setPull(Pull.RIGHT);
        modalFooter.add(btnConfirm);

        Button btnAbort = new Button(abortButtonText, abortButtonIcon, new ClickHandler() {
            public void onClick(ClickEvent event) {
                onAbort();
                hide();
            }
        });
        btnAbort.setType(ButtonType.DANGER);
        btnAbort.setPull(Pull.LEFT);
        modalFooter.add(btnAbort);
        add(modalFooter);
    }

    public abstract void onConfirm();

    public abstract void onAbort();
}
