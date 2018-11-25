package net.wbz.moba.controlcenter.web.client.editor.track;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.GridConstrainedDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.client.util.modal.DeleteModal;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

/**
 * @author Daniel Tuerk
 */
public class TrackEditorContainer extends Composite {

    private static final int SIZE = 25;

    public static int ZOOM_STEP = 10;
    private static Binder uiBinder = GWT.create(Binder.class);
    private static int DRAGGABLE_OFFSET_PADDING = 1;
    public static int DRAGGABLE_OFFSET_HEIGHT = SIZE + DRAGGABLE_OFFSET_PADDING;
    public static int DRAGGABLE_OFFSET_WIDTH = SIZE + DRAGGABLE_OFFSET_PADDING;
    @UiField
    HTMLPanel trackContainer;
    @UiField
    HTMLPanel paletteContainer;
    private Logger logger = Logger.getLogger(TrackEditorContainer.class.getName());
    private SimpleTrackPanel trackEditorPanel;
    private PickupDragController dragController;
    private BlockEditModal blockEditModal;

    public TrackEditorContainer() {
        initWidget(uiBinder.createAndBindUi(this));

        blockEditModal = new BlockEditModal();
        blockEditModal.addHiddenHandler(modalHiddenEvent -> trackEditorPanel.loadTrack());

        trackEditorPanel = new SimpleTrackPanel(true) {
            @Override
            protected Widget initTrackWidget(AbstractSvgTrackWidget trackWidget) {
                PaletteWidget paletteWidget = new EditorPaletteWidget(trackWidget);
                dragController.makeDraggable(paletteWidget);
                return paletteWidget;
            }
        };
        trackEditorPanel.setWidth("4000px");
        trackEditorPanel.setHeight("4000px");

        GridConstrainedDropController dropController = new GridConstrainedDropController(trackEditorPanel,
            DRAGGABLE_OFFSET_WIDTH, DRAGGABLE_OFFSET_HEIGHT);

        dragController = new PickupDragController(trackEditorPanel, true);
        dragController.setBehaviorMultipleSelection(true);
        dragController.registerDropController(dropController);

        dragController.getSelectedWidgets();

        PalettePanel palette = new PalettePanel(dragController);

        for (AbstractSvgTrackWidget widget : ModelManager.getInstance().getWidgets()) {
            palette.addPaletteItem(widget);
        }

        paletteContainer.add(palette);

        trackContainer.add(trackEditorPanel);
    }

    @UiHandler("menuSave")
    public void saveAction(ClickEvent event) {
        List<AbstractTrackPart> trackParts = new ArrayList<>();
        for (int i = 0; i < trackEditorPanel.getWidgetCount(); i++) {
            Widget paletteWidget = trackEditorPanel.getWidget(i);
            if (paletteWidget instanceof PaletteWidget) {
                Widget w = ((PaletteWidget) paletteWidget).getPaletteWidgetItem();
                if (w instanceof AbstractSvgTrackWidget) {
                    try {
                        trackParts.add(((AbstractSvgTrackWidget) w)
                            .getTrackPart(trackEditorPanel, trackEditorPanel.getZoomLevel()));
                    } catch (Exception e) {
                        String msg =
                            "ignore widget (can't save): " + ((AbstractSvgTrackWidget) w).getPaletteTitle()
                                + " - " + e.getMessage();
                        Notify.notify("", msg, IconType.WARNING);
                        logger.log(Level.SEVERE, msg, e);
                        e.printStackTrace();
                    }
                }
            }
        }

        RequestUtils.getInstance().getTrackEditorService().saveTrack(trackParts, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Notify.notify("Editor", "error by save track: " + caught.getMessage(), IconType.WARNING);

            }

            @Override
            public void onSuccess(Void result) {
                Notify.notify("track saved!");
            }
        });
    }


    @UiHandler("menuBlocks")
    public void blocksAction(ClickEvent event) {
        blockEditModal.show();
    }

    @UiHandler("menuEdit")
    public void editAction(ClickEvent event) {
        for (Widget selectedWidget : dragController.getSelectedWidgets()) {
            //TODO
            new EditWidgetDoubleClickHandler(((EditorPaletteWidget) selectedWidget).getWidget())
                .onDoubleClick(null);
            // trackEditorPanel.remove(selectedWidget);
            break;
        }
    }


    @UiHandler("menuDelete")
    public void deleteAction(ClickEvent event) {
        DeleteModal deleteModal = new DeleteModal("Delete the selected track parts?") {

            @Override
            public void onConfirm() {
                for (Widget selectedWidget : dragController.getSelectedWidgets()) {
                    trackEditorPanel.remove(selectedWidget);
                }
            }
        };
        deleteModal.show();
    }

    interface Binder extends UiBinder<Widget, TrackEditorContainer> {

    }
}
