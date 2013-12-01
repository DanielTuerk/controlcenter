package net.wbz.moba.controlcenter.web.client.editor.track;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.GridConstrainedDropController;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.model.track.AbsoluteTrackPosition;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractImageTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class TrackEditorContainer extends SplitLayoutPanel {

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
        super(5);
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

        for (AbstractImageTrackWidget widget : ModelManager.getInstance().getWidgets()) {
                palette.addPaletteItem(widget);
        }


        HorizontalPanel titelPanel = new HorizontalPanel();
        titelPanel.add(new Label("Track Editor"));



        addNorth(titelPanel,30);

        addWest(palette, 200);

        MenuBar menuBar = new MenuBar();
        menuBar.addItem(new MenuItem("Save", false, new Command() {
            @Override
            public void execute() {
                List<TrackPart> trackParts = new ArrayList<TrackPart>();
                for (int i = 0; i < getBoundaryPanel().getWidgetCount(); i++) {
                    Widget paletteWidget = getBoundaryPanel().getWidget(i);
                    if (paletteWidget instanceof PaletteWidget) {
                        Widget w = ((PaletteWidget) paletteWidget).getPaletteWidgetItem();
                        if (w instanceof AbstractImageTrackWidget) {
                            trackParts.add(((AbstractImageTrackWidget) w).getTrackPart(getBoundaryPanel(), getBoundaryPanel().getZoomLevel()));
                        }
                    }
                }

                ServiceUtils.getTrackEditorService().saveTrack(trackParts.toArray(new TrackPart[trackParts.size()]), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.severe("save track error " + throwable.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.info("save track ok");//To change body of implemented methods use File | Settings | File Templates.
                    }
                });
            }
        }));
        menuBar.addItem("Delete", new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                for (Widget selectedWidget : dragController.getSelectedWidgets()) {
                    boundaryPanel.remove(selectedWidget);
                }
            }
        });


        addNorth(menuBar, 30);


        add(boundaryPanel);
    }

    @Override
    protected void onLoad() {
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
                    AbstractImageTrackWidget trackWidget = ModelManager.getInstance().getWidgetOf(trackPart);
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
