package net.wbz.moba.controlcenter.web.client.components;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractEditModal<T> extends Modal {

    private final ModalBody modalBody;
    private final ModalFooter modalFooter;
    private final T model;

    public AbstractEditModal(String title, String btnConfirmText, String btnCancelText, T model) {
        super();
        setFade(true);
        setTitle(title);

        this.model = model;

        modalBody = new ModalBody();
        modalBody.add(createContent(model));
        add(modalBody);

        modalFooter = new ModalFooter();
        modalFooter.add(createFooter(btnConfirmText, btnCancelText));
        add(modalFooter);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    protected T getModel() {
        return model;
    }

    protected abstract IsWidget createContent(T model);

    protected abstract void onCancel();

    protected abstract void onConfirm(T model);

    private FlowPanel createFooter(String btnConfirmText, String btnCancelText) {
        FlowPanel panel = new FlowPanel();
        Button btnSave = new Button(btnConfirmText, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onConfirm(model);
            }
        });
        btnSave.setType(ButtonType.PRIMARY);
        btnSave.setPull(Pull.RIGHT);
        panel.add(btnSave);

        Button btnClose = new Button(btnCancelText, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onCancel();
                hide();
            }
        });
        btnClose.setType(ButtonType.DANGER);
        btnClose.setPull(Pull.LEFT);
        panel.add(btnClose);
        return panel;
    }
}
