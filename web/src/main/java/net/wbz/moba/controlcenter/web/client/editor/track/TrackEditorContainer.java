package net.wbz.moba.controlcenter.web.client.editor.track;

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
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.model.track.AbsoluteTrackPosition;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.NavPills;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.growl.client.ui.Growl;
import org.gwtbootstrap3.extras.growl.client.ui.GrowlHelper;
import org.gwtbootstrap3.extras.growl.client.ui.GrowlOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrackEditorContainer extends FlowPanel {

    private Logger logger = Logger.getLogger(TrackEditorContainer.class.getName());

    private static int draggableOffsetPadding = 2;

    public static int draggableOffsetHeight = 25 + draggableOffsetPadding;

    public static int draggableOffsetWidth = 25 + draggableOffsetPadding;

    public static int ZOOM_STEP = 10;

    private AbstractTrackPanel boundaryPanel;

    private PickupDragController dragController;

    public AbstractTrackPanel getBoundaryPanel() {
        return boundaryPanel;
    }

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
                List<TrackPart> trackParts = new ArrayList<TrackPart>();
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
                                GrowlOptions growlOptions = GrowlHelper.getNewOptions();
                                growlOptions.setDangerType();
                                Growl.growl(msg, growlOptions);
                                logger.log(Level.SEVERE, msg, e);
                                e.printStackTrace();
                            }
                        }
                    }
                }

                ServiceUtils.getTrackEditorService().saveTrack(trackParts.toArray(new TrackPart[trackParts.size()]),
                        new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                GrowlOptions growlOptions = GrowlHelper.getNewOptions();
                                growlOptions.setDangerType();
                                Growl.growl("error by save track: " + throwable.getMessage(), growlOptions);
                            }

                            @Override
                            public void onSuccess(Void aVoid) {
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
        add(menu);

        FlowPanel editorPanel = new FlowPanel();
        palette.getElement().getStyle().setFloat(Style.Float.LEFT);
        editorPanel.add(palette);

        ScrollPanel scrollPanel = new ScrollPanel(boundaryPanel);
        editorPanel.add(scrollPanel);

        add(editorPanel);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        Log.info("load track " + new Date().toString());

        for (int i = boundaryPanel.getWidgetCount() - 1; i >= 0; i--) {
            boundaryPanel.remove(i);
        }

        ServiceUtils.getTrackEditorService().loadTrack(new AsyncCallback<TrackPart[]>() {
            @Override
            public void onFailure(Throwable throwable) {
                Log.severe(throwable.getLocalizedMessage());
            }

            @Override
            public void onSuccess(TrackPart[] trackParts) {
                Log.info("load track success " + new Date().toString());
                for (TrackPart trackPart : trackParts) {
                    AbstractSvgTrackWidget trackWidget = ModelManager.getInstance().getWidgetOf(trackPart);
                    trackWidget.setEnabled(true);
                    AbsoluteTrackPosition trackPosition = trackWidget.getTrackPosition(trackPart.getGridPosition(), boundaryPanel.getZoomLevel());
                    PaletteWidget paletteWidget = new EditorPaletteWidget(trackWidget);
                    dragController.makeDraggable(paletteWidget);
                    boundaryPanel.add(paletteWidget, trackPosition.getLeft(), trackPosition.getTop());
                }
                Log.info("load track done " + new Date().toString());
            }
        });
    }
}
