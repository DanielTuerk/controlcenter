package net.wbz.moba.controlcenter.web.client.viewer;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.internal.Nullability;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Created by Daniel on 08.03.14.
 */
public class TrainItemEditDialog extends DialogBox {

    private final Train train;


    public TrainItemEditDialog(Train train) {
        this.train = train;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        setGlassEnabled(true);
        setAnimationEnabled(true);
        setPixelSize(300, 120);
        setText("Edit train: "+train.getName());

        VerticalPanel container = new VerticalPanel();
        container.add(new Label("Address:"));

        final TextBox txtAddress = new TextBox();
        txtAddress.setValue(String.valueOf(train.getAddress()));
        container.add(txtAddress);

        Button btnSave=new Button("Save", IconType.SAVE);
        btnSave.setType(ButtonType.SUCCESS);
        btnSave.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                train.setAddress(Integer.parseInt(txtAddress.getValue()));
                ServiceUtils.getTrainEditorService().updateTrain(train, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                       //TODO
                    }

                    @Override
                    public void onSuccess(Void result) {
                        TrainItemEditDialog.this.hide();
                    }
                });
            }
        });
        container.add(btnSave);

        Button btnCancel = new Button("Cancel");
        btnCancel.setType(ButtonType.INVERSE);
        btnCancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TrainItemEditDialog.this.hide();
            }
        });
        container.add(btnCancel);

        setWidget(container);
    }
}
