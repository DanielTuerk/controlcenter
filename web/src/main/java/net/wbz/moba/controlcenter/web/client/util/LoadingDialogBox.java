package net.wbz.moba.controlcenter.web.client.util;

import com.google.common.base.Strings;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Daniel Tuerk
 */
@Deprecated
public class LoadingDialogBox extends DialogBox {


    private final Label lblPercentage = new Label("");
    private final Label lblStateMessage = new Label("");

    private final static int LOADING_BAR_MAX_WIDTH = 200;

    @Override
    protected void onLoad() {
        setGlassEnabled(true);
        setAnimationEnabled(true);
        setPixelSize(300, 120);
        setText("Loading ...");

        VerticalPanel container = new VerticalPanel();

        lblPercentage.setStyleName("loadingBar");

        container.add(lblPercentage);
        container.add(lblStateMessage);

        setWidget(container);
    }

    public void setProgressPercentage(int percentage) {
        if(percentage ==0){
            lblStateMessage.setText("");
        }
        setProgressPercentage(percentage, null);
    }


    public void setProgressPercentage(int percentage, String message) {
        lblPercentage.setWidth(LOADING_BAR_MAX_WIDTH * percentage / 100 + "px");
        if (!Strings.isNullOrEmpty(message)) {
            lblStateMessage.setText(message);
        }
    }
}
