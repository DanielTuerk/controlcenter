package net.wbz.moba.controlcenter.web.client.util;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class DialogBoxUtil {

    private final static LoadingDialogBox DIALOG_LOADING_VIEWER = new LoadingDialogBox();

    @Deprecated
    public static LoadingDialogBox getLoading() {
        return DIALOG_LOADING_VIEWER;
    }



}
