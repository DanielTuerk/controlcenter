package net.wbz.moba.controlcenter.web.client.components.table;

import org.gwtbootstrap3.client.ui.constants.IconType;

/**
 * Column for the edit button in table.
 *
 * @author Daniel Tuerk
 */
abstract public class EditButtonColumn<T> extends ButtonColumn<T> {

    protected EditButtonColumn() {
        super(IconType.EDIT);
    }
}
