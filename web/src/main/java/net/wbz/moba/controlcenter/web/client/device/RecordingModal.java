package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import net.wbz.moba.controlcenter.web.shared.bus.RecordingEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;

/**
 * @author Daniel Tuerk
 */
public class RecordingModal extends Modal {

    private final HTML bodyText;
    private String filePathToDownload = null;

    public RecordingModal() {
        ModalBody modalBody = new ModalBody();
        bodyText = new HTML();
        modalBody.add(bodyText);
        Button btnDownload = new Button("Download", IconType.FILE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (filePathToDownload != null) {
                    Window.open("download?file="+filePathToDownload, "_parent", "");
                }
            }
        });
        modalBody.add(btnDownload);
        add(modalBody);

        ModalFooter modalFooter = new ModalFooter();
        Button btnClose = new Button("Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        btnClose.setType(ButtonType.DANGER);
        btnClose.setPull(Pull.LEFT);
        modalFooter.add(btnClose);
        add(modalFooter);
    }

    public void show(RecordingEvent recordingEvent) {
        if (recordingEvent.getState() == RecordingEvent.STATE.STOP) {
            setTitle("Recording finished");
            bodyText.setHTML("File: " + recordingEvent.getFilePath());
            filePathToDownload = recordingEvent.getFilePath();
        }
        show();
    }
}
