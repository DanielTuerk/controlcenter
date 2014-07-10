package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Panel;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;

/**
 * Widget wrapper class used by {@link net.wbz.moba.controlcenter.web.client.editor.track.PalettePanel}.
 */
public class EditorPaletteWidget extends PaletteWidget{

    public EditorPaletteWidget(AbstractSvgTrackWidget widget) {
        super(widget);
        if (getWidget() instanceof EditTrackWidgetHandler) {
            getShim().addDoubleClickHandler(new EditWidgetDoubleClickHandler((EditTrackWidgetHandler) getWidget()));
        }
    }

    private final class EditWidgetDoubleClickHandler implements DoubleClickHandler {

        private final EditTrackWidgetHandler handler;

        private EditWidgetDoubleClickHandler(EditTrackWidgetHandler handler) {
            this.handler = handler;
        }

        @Override
        public void onDoubleClick(DoubleClickEvent event) {
            DialogBox dialogBox = createDialogBox();
            dialogBox.center();
            dialogBox.show();
        }

        private DialogBox createDialogBox() {
            // Create a dialog box and set the caption text
            final DialogBox dialogBox = new DialogBox();
            dialogBox.ensureDebugId("cwDialogBox");
            dialogBox.setText("Edit");

            Panel contentPanel = handler.getDialogContent();
            dialogBox.setWidget(contentPanel);


            contentPanel.add(new Button(
                    "Ok", new ClickHandler() {
                public void onClick(ClickEvent event) {
                    handler.onConfirmCallback();
                    dialogBox.hide();
                }
            }));

            // Add a close button at the bottom of the dialog
            contentPanel.add(new Button(
                    "Close", new ClickHandler() {
                public void onClick(ClickEvent event) {
                    dialogBox.hide();
                }
            }));

            // Return the dialog box
            return dialogBox;
        }
    }

}
