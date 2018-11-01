package net.wbz.moba.controlcenter.web.client.components.table;

import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;

/**
 * Column for the delete button in table.
 *
 * @author Daniel Tuerk
 */
abstract public class DeleteButtonColumn<T> extends ButtonColumn<T> {

    protected DeleteButtonColumn() {
        super(IconType.TRASH, ButtonType.DANGER);
    }
}
