package net.wbz.moba.controlcenter.web.client.editor.track;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Divider;
import org.gwtbootstrap3.client.ui.NavPills;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.GridConstrainedDropController;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.client.util.modal.DeleteModal;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;

/**
 * @author Daniel Tuerk
 */
public class TrackEditorContainer extends FlowPanel {

    public static int ZOOM_STEP = 10;
    private static int draggableOffsetPadding = 2;

    public static int draggableOffsetHeight = 25 + draggableOffsetPadding;

    public static int draggableOffsetWidth = 25 + draggableOffsetPadding;
    private Logger logger = Logger.getLogger(TrackEditorContainer.class.getName());
    private SimpleTrackPanel trackEditorPanel;

    private PickupDragController dragController;

    private BlockEditModal blockEditModal;

    public TrackEditorContainer() {
        this.setSize("100%", "100%");

        blockEditModal = new BlockEditModal();
        blockEditModal.addHiddenHandler(new ModalHiddenHandler() {
            @Override
            public void onHidden(ModalHiddenEvent modalHiddenEvent) {
                trackEditorPanel.loadTrack();
            }
        });

        //
        trackEditorPanel = new SimpleTrackPanel() {
            @Override
            protected Widget initTrackWidget(AbstractSvgTrackWidget trackWidget) {
                PaletteWidget paletteWidget = new EditorPaletteWidget(trackWidget);
                dragController.makeDraggable(paletteWidget);
                return paletteWidget;
            }
        };

        GridConstrainedDropController dropController = new GridConstrainedDropController(trackEditorPanel,
                draggableOffsetWidth, draggableOffsetHeight);

        dragController = new PickupDragController(trackEditorPanel, true);
        dragController.setBehaviorMultipleSelection(true);
        dragController.registerDropController(dropController);

        dragController.getSelectedWidgets();

        PalettePanel palette = new PalettePanel(dragController);

        for (AbstractSvgTrackWidget widget : ModelManager.getInstance().getWidgets()) {
            palette.addPaletteItem(widget);
        }

        add(createMenu());

        FlowPanel editorPanel = new FlowPanel();
        palette.getElement().getStyle().setFloat(Style.Float.LEFT);
        editorPanel.add(palette);

        ScrollPanel scrollPanel = new ScrollPanel(trackEditorPanel);
        editorPanel.add(scrollPanel);

        add(editorPanel);
    }

    private NavPills createMenu() {
        NavPills menu = new NavPills();
        menu.add(createMenuSaveAnchor());
        menu.add(createMenuDeleteAnchor());
        menu.add(createMenuEditAnchor());
        menu.add(new Divider());
        menu.add(createMenuBlocksAnchor());
        return menu;
    }

    private AnchorListItem createMenuBlocksAnchor() {
        AnchorListItem openBlockEditAnchorListItem = new AnchorListItem("Blocks");
        openBlockEditAnchorListItem.setIcon(IconType.BOOK);
        openBlockEditAnchorListItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                blockEditModal.show();
            }
        });
        return openBlockEditAnchorListItem;
    }

    private AnchorListItem createMenuEditAnchor() {
        /**
         * TODO clean code
         */
        AnchorListItem editAnchorListItem = new AnchorListItem("Edit");
        editAnchorListItem.setIcon(IconType.PENCIL);
        editAnchorListItem.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (Widget selectedWidget : dragController.getSelectedWidgets()) {

                    new EditWidgetDoubleClickHandler(((EditorPaletteWidget) selectedWidget)
                            .getWidget()).onDoubleClick(null);
                    // trackEditorPanel.remove(selectedWidget);
                    break;
                }
            }
        });
        return editAnchorListItem;
    }

    private AnchorListItem createMenuSaveAnchor() {
        AnchorListItem saveAnchorListItem = new AnchorListItem("Save");
        saveAnchorListItem.setIcon(IconType.SAVE);
        saveAnchorListItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                List<AbstractTrackPart> trackParts = new ArrayList<>();
                for (int i = 0; i < getTrackEditorPanel().getWidgetCount(); i++) {
                    Widget paletteWidget = getTrackEditorPanel().getWidget(i);
                    if (paletteWidget instanceof PaletteWidget) {
                        Widget w = ((PaletteWidget) paletteWidget).getPaletteWidgetItem();
                        if (w instanceof AbstractSvgTrackWidget) {
                            try {
                                trackParts.add(((AbstractSvgTrackWidget) w).getTrackPart(getTrackEditorPanel(),
                                        getTrackEditorPanel().getZoomLevel()));
                            } catch (Exception e) {
                                String msg = "ignore widget (can't save): " + ((AbstractSvgTrackWidget) w)
                                        .getPaletteTitle() + " - " + e.getMessage();
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
        });
        return saveAnchorListItem;
    }

    private AnchorListItem createMenuDeleteAnchor() {
        AnchorListItem deleteAnchorListItem = new AnchorListItem("Delete");
        deleteAnchorListItem.setIcon(IconType.TIMES);
        deleteAnchorListItem.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
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
        });
        return deleteAnchorListItem;
    }

    public AbstractTrackPanel getTrackEditorPanel() {
        return trackEditorPanel;
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        Log.info("load track " + new Date().toString());

    }
}
