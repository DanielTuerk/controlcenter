package net.wbz.moba.controlcenter.web.client.util;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class DialogBoxUtil {

    private final static LoadingDialogBox DIALOG_LOADING_VIEWER = new LoadingDialogBox();

    public static LoadingDialogBox getLoading() {
        return DIALOG_LOADING_VIEWER;
    }



}
