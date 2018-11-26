package net.wbz.moba.controlcenter.web.client.train;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.components.BitSelect;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import org.gwtbootstrap3.client.ui.IntegerBox;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * {@link org.gwtbootstrap3.client.ui.FieldSet} to configure the {@link TrainFunction} of a
 * {@link net.wbz.moba.controlcenter.web.shared.train.Train}.
 *
 * @author Daniel Tuerk
 */
abstract class TrainFunctionFieldSet extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);

    private final TrainFunction trainFunction;
    @UiField
    TextBox txtAlias;
    @UiField
    IntegerBox txtAddress;
    @UiField
    BitSelect selectBit;

    /**
     * Create fieldset for the given {@link TrainFunction}.
     *
     * @param trainFunction {@link TrainFunction}
     */
    TrainFunctionFieldSet(TrainFunction trainFunction) {
        initWidget(uiBinder.createAndBindUi(this));
        this.trainFunction = trainFunction;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        txtAlias.setValue(trainFunction.getAlias());
        txtAddress.setValue(trainFunction.getConfiguration().getAddress());
        selectBit.setSelectedItem(trainFunction.getConfiguration().getBit());
    }

    @UiHandler("btnDelete")
    void onClickDelete(ClickEvent event) {
        onDelete();
    }

    /**
     * Callback for the delete action of the {@link TrainFunction} in the view.
     */
    abstract void onDelete();

    /**
     * Apply the changes from UI to model.
     */
    void applyChanges() {
        trainFunction.setAlias(txtAlias.getText());
        trainFunction.getConfiguration().setAddress(txtAddress.getValue());

        trainFunction.getConfiguration().setBit(selectBit.getSelected().get());
    }

    interface Binder extends UiBinder<Widget, TrainFunctionFieldSet> {

    }
}
