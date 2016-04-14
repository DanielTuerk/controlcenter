package net.wbz.moba.controlcenter.web.client.editor.track;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.model.track.AbsoluteTrackPosition;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPartProxy;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.NavPills;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.growl.client.ui.Growl;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.GridConstrainedDropController;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;

/**
 * @author Daniel Tuerk
 */
public class TrackEditorContainer extends FlowPanel {

    public static int ZOOM_STEP = 10;
    private static int draggableOffsetPadding = 2;

    public static int draggableOffsetHeight = 25 + draggableOffsetPadding;

    public static int draggableOffsetWidth = 25 + draggableOffsetPadding;
    private Logger logger = Logger.getLogger(TrackEditorContainer.class.getName());
    private AbstractTrackPanel boundaryPanel;

    private PickupDragController dragController;

    public TrackEditorContainer() {
        this.setSize("100%", "100%");
        //
        boundaryPanel = new TrackEditorPanel();

        GridConstrainedDropController dropController = new GridConstrainedDropController(boundaryPanel,
                draggableOffsetWidth, draggableOffsetHeight);

        dragController = new PickupDragController(boundaryPanel, true);
        dragController.setBehaviorMultipleSelection(true);
        dragController.registerDropController(dropController);

        dragController.getSelectedWidgets();

        PalettePanel palette = new PalettePanel(dragController);

        for (AbstractSvgTrackWidget widget : ModelManager.getInstance().getWidgets()) {
            palette.addPaletteItem(widget);
        }

        NavPills menu = new NavPills();
        AnchorListItem saveAnchorListItem = new AnchorListItem("Save");
        saveAnchorListItem.setIcon(IconType.SAVE);
        saveAnchorListItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                List<TrackPartProxy> trackParts = new ArrayList<>();
                for (int i = 0; i < getBoundaryPanel().getWidgetCount(); i++) {
                    Widget paletteWidget = getBoundaryPanel().getWidget(i);
                    if (paletteWidget instanceof PaletteWidget) {
                        Widget w = ((PaletteWidget) paletteWidget).getPaletteWidgetItem();
                        if (w instanceof AbstractSvgTrackWidget) {
                            try {
                                trackParts.add(((AbstractSvgTrackWidget) w).getTrackPart(getBoundaryPanel(),
                                        getBoundaryPanel().getZoomLevel()));
                            } catch (Exception e) {
                                String msg = "ignore widget (can't save): " + ((AbstractSvgTrackWidget) w)
                                        .getPaletteTitle() + " - " + e.getMessage();
                                Growl.growl("", msg, IconType.WARNING);
                                logger.log(Level.SEVERE, msg, e);
                                e.printStackTrace();
                            }
                        }
                    }
                }

                for (TrackPartProxy trackPart : trackParts) {
                }
                ServiceUtils.getInstance().getTrackEditorService();

                ServiceUtils.getInstance().getTrackEditorService().saveTrack(trackParts).fire(new Receiver<Void>() {
                    public void onFailure(Throwable throwable) {
                        Growl.growl("Editor", "error by save track: " + throwable.getMessage(), IconType.WARNING);
                    }

                    @Override
                    public void onSuccess(Void response) {
                        Growl.growl("track saved!");
                    }
                });
            }
        });
        menu.add(saveAnchorListItem);

        AnchorListItem deleteAnchorListItem = new AnchorListItem("Delete");
        deleteAnchorListItem.setIcon(IconType.TIMES);
        deleteAnchorListItem.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (Widget selectedWidget : dragController.getSelectedWidgets()) {
                    boundaryPanel.remove(selectedWidget);
                }
            }
        });
        menu.add(deleteAnchorListItem);

        /**
         * TODO clean code
         */
        AnchorListItem editAnchorListItem = new AnchorListItem("Edit");
        editAnchorListItem.setIcon(IconType.PENCIL);
        editAnchorListItem.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (Widget selectedWidget : dragController.getSelectedWidgets()) {

                    new EditWidgetDoubleClickHandler((EditTrackWidgetHandler) ((EditorPaletteWidget) selectedWidget)
                            .getWidget()).onDoubleClick(null);
                    // boundaryPanel.remove(selectedWidget);
                    break;
                }
            }
        });
        menu.add(editAnchorListItem);

        add(menu);

        FlowPanel editorPanel = new FlowPanel();
        palette.getElement().getStyle().setFloat(Style.Float.LEFT);
        editorPanel.add(palette);

        ScrollPanel scrollPanel = new ScrollPanel(boundaryPanel);
        editorPanel.add(scrollPanel);

        add(editorPanel);
    }

    public AbstractTrackPanel getBoundaryPanel() {
        return boundaryPanel;
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        Log.info("load track " + new Date().toString());

        for (int i = boundaryPanel.getWidgetCount() - 1; i >= 0; i--) {
            boundaryPanel.remove(i);
        }

        ServiceUtils.getInstance().getTrackEditorService().loadTrack().fire(new Receiver<List<TrackPartProxy>>() {
            @Override
            public void onSuccess(List<TrackPartProxy> trackParts) {
                Log.info("load track success " + new Date().toString());
                for (TrackPartProxy trackPart : trackParts) {
                    AbstractSvgTrackWidget trackWidget = ModelManager.getInstance().getWidgetOf(trackPart);
                    trackWidget.setEnabled(true);
                    AbsoluteTrackPosition trackPosition = trackWidget.getTrackPosition(trackPart.getGridPosition(),
                            boundaryPanel.getZoomLevel());
                    PaletteWidget paletteWidget = new EditorPaletteWidget(trackWidget);
                    dragController.makeDraggable(paletteWidget);
                    boundaryPanel.add(paletteWidget, trackPosition.getLeft(), trackPosition.getTop());
                }
                Log.info("load track done " + new Date().toString());
            }
        });
    }
}
