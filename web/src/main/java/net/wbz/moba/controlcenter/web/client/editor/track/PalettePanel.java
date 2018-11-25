package net.wbz.moba.controlcenter.web.client.editor.track;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.html.ClearFix;
import org.gwtbootstrap3.client.ui.html.Text;

/**
 * Palette with the draggable track widgets in the track editor.
 *
 * @author Daniel Tuerk
 */
class PalettePanel extends Composite {

    private static final int ELEMENT_MARGIN = 5;
    private static PalettePanel.Binder UI_BINDER = GWT.create(PalettePanel.Binder.class);
    private final PickupDragController dragController;
    private final Map<String, FlowPanel> paletteGroupMapping = new HashMap<>();

    @UiField
    PanelBody panelBody;

    PalettePanel(PickupDragController dragController) {
        initWidget(UI_BINDER.createAndBindUi(this));
        this.dragController = dragController;
    }

    private void addPaletteItem(AbstractSvgTrackWidget widget, int index) {
        if (!paletteGroupMapping.containsKey(widget.getPaletteTitle())) {
            FlowPanel paletteGroup = new FlowPanel() {

                /**
                 * Removed widgets that are instances of {@link PaletteWidget} are immediately replaced with a
                 * cloned copy of the original.
                 *
                 * @param w the widget to remove
                 * @return true if a widget was removed
                 */
                @Override
                public boolean remove(Widget w) {
                    int index = getWidgetIndex(w);
                    boolean result = super.remove(w);
                    if (w instanceof PaletteWidget) {
                        PaletteWidget clone = ((PaletteWidget) w).cloneWidget();
                        clone.getWidget().setEnabled(true);
                        addPaletteItem(clone.getPaletteWidgetItem(), index);
                    }
                    return result;
                }
            };
            paletteGroupMapping.put(widget.getPaletteTitle(), paletteGroup);
            org.gwtbootstrap3.client.ui.gwt.FlowPanel titleFlowPanel = new org.gwtbootstrap3.client.ui.gwt.FlowPanel();
            titleFlowPanel.add(new Text(widget.getPaletteTitle()));
            panelBody.add(titleFlowPanel);
            panelBody.add(paletteGroup);
            panelBody.add(new ClearFix());
        }

        PaletteWidget paletteWidget = new PaletteWidget(widget);
        dragController.makeDraggable(paletteWidget);
        paletteWidget.getElement().getStyle().setFloat(Style.Float.LEFT);
        paletteWidget.getElement().getStyle().setMarginLeft(ELEMENT_MARGIN, Style.Unit.PX);
        paletteWidget.getElement().getStyle().setMarginBottom(ELEMENT_MARGIN, Style.Unit.PX);
        if (index > -1) {
            paletteGroupMapping.get(widget.getPaletteTitle()).insert(paletteWidget, index);
        } else {
            paletteGroupMapping.get(widget.getPaletteTitle()).add(paletteWidget);
        }
    }

    void addPaletteItem(AbstractSvgTrackWidget widget) {
        widget.setEnabled(true);
        // TODO: bullshit - only done to register the double click handler
        new EditorPaletteWidget(widget);
        addPaletteItem(widget, -1);
    }

    interface Binder extends UiBinder<Widget, PalettePanel> {

    }
}
