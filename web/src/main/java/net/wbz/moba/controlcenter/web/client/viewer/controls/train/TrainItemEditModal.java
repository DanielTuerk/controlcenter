package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.train.TrainProxy;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.growl.client.ui.Growl;
import org.gwtbootstrap3.extras.growl.client.ui.GrowlType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * Modal to edit the {@link net.wbz.moba.controlcenter.web.shared.train.Train} data.
 *
 * @author Daniel Tuerk
 */
public class TrainItemEditModal extends Modal {

    private final TrainProxy train;
    private TextBox txtName;
    private TextBox txtAddress;

    public TrainItemEditModal(TrainProxy train) {
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

                ServiceUtils.getInstance().getTrainEditorService().deleteTrain(train.getId()).fire(
                        new Receiver<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        Growl.growl("", "Train " + train.getName() + " deleted", IconType.INFO);
                    }

                    @Override
                    public void onFailure(ServerFailure error) {
                        Growl.growl("", "Delete Train " + train.getName() + " Error: " + error.getMessage(),
                                IconType.WARNING, GrowlType.DANGER);
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
                ServiceUtils.getInstance().getTrainEditorService().updateTrain(train).fire(new Receiver<Void>() {
                    @Override
                    public void onSuccess(Void response) {
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
