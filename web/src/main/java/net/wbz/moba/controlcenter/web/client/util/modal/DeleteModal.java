package net.wbz.moba.controlcenter.web.client.util.modal;

import org.gwtbootstrap3.client.ui.constants.IconType;

/**
 * Abstract modal to delete.
 * 
 * @author Daniel Tuerk
 */
public abstract class DeleteModal extends AbstractConfirmModal {

    public DeleteModal(String message) {
        super("Delete", message, "Delete", IconType.TRASH, "Abort", null);
    }

    @Override
    public void onAbort() {
    }
}
