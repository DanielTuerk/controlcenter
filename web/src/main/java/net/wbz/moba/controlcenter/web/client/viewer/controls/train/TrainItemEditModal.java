package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.growl.client.ui.Growl;
import org.gwtbootstrap3.extras.growl.client.ui.GrowlType;

/**
 * Modal to edit the {@link net.wbz.moba.controlcenter.web.shared.train.Train} data.
 *
 * @author Daniel Tuerk
 */
public class TrainItemEditModal extends Modal {

    private final Train train;
    private TextBox txtName;
    private TextBox txtAddress;

    public TrainItemEditModal(Train train) {
        this.train = train;
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        setTitle("Edit train: " + train.getName());

        ModalBody modalBody = new ModalBody();
        modalBody.add(createContent());
        add(modalBody);

        createFooter();

    }

    @Override
    protected void onUnload() {
        super.onUnload();
        clear();
    }

    private Widget createContent() {
        Form createForm = new Form(FormType.HORIZONTAL);
        FormGroup nameGroup = new FormGroup();
        FormLabel lblName = new FormLabel();
        lblName.addStyleName("col-lg-2");
        lblName.setText("Name");
        nameGroup.add(lblName);
        FlowPanel namePanel = new FlowPanel();
        namePanel.addStyleName("col-lg-10");
        txtName = new TextBox();
        txtName.setText(train.getName());
        namePanel.add(txtName);
        nameGroup.add(namePanel);
        createForm.add(nameGroup);

        FormGroup addressGroup = new FormGroup();
        FormLabel lblAddress = new FormLabel();
        lblAddress.addStyleName("col-lg-2");
        lblAddress.setText("Address");
        addressGroup.add(lblAddress);
        FlowPanel addressPanel = new FlowPanel();
        addressPanel.addStyleName("col-lg-10");
        txtAddress = new TextBox();
        txtAddress.setText(String.valueOf(train.getAddress()));
        addressPanel.add(txtAddress);
        addressGroup.add(addressPanel);
        createForm.add(addressGroup);

        Button btnDelete = new Button("Delete Train", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                TrainItemEditModal.this.hide();

                ServiceUtils.getTrainEditorService().deleteTrain(train.getId(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Growl.growl("", "Delete Train " + train.getName() + " Error: " + throwable.getMessage(), IconType.WARNING, GrowlType.DANGER);
                    }

                    @Override
                    public void onSuccess(Void aVoid) {
                        Growl.growl("", "Train " + train.getName() + " deleted", IconType.INFO);
                    }
                });
            }
        });
        btnDelete.setType(ButtonType.DANGER);
        createForm.add(btnDelete);

        return createForm;
    }

    private void createFooter() {
        ModalFooter modalFooter = new ModalFooter();

        Button btnSave = new Button("Save", IconType.SAVE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                train.setAddress(Integer.parseInt(txtAddress.getValue()));
                train.setName(txtName.getValue());
                ServiceUtils.getTrainEditorService().updateTrain(train, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        //TODO
                    }

                    @Override
                    public void onSuccess(Void result) {
                        TrainItemEditModal.this.hide();
                    }
                });
            }
        });
        btnSave.setType(ButtonType.SUCCESS);
        modalFooter.add(btnSave);

        Button btnCancel = new Button("Cancel");
        btnCancel.setType(ButtonType.WARNING);
        btnCancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TrainItemEditModal.this.hide();
            }
        });
        modalFooter.add(btnCancel);
        add(modalFooter);
    }
}
