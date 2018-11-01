package net.wbz.moba.controlcenter.web.client.components.table;

import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;

/**
 * Column with button.
 *
 * @author Daniel Tuerk
 */
public abstract class ButtonColumn<T> extends com.google.gwt.user.cellview.client.Column<T, String> {

    public ButtonColumn(IconType iconType) {
        this(iconType, ButtonType.DEFAULT);
    }

    public ButtonColumn(IconType iconType, ButtonType buttonType) {
        super(new ButtonCell(iconType, buttonType, ButtonSize.SMALL));
        setFieldUpdater((index, object, value) -> onAction(object));
    }

    abstract public void onAction(T object);

    @Override
    public String getValue(T t) {
        return "";
    }
}
