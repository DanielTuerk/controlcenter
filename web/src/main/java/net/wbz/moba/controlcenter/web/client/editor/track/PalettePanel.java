package net.wbz.moba.controlcenter.web.client.editor.track;

import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.html.ClearFix;
import org.gwtbootstrap3.client.ui.html.Text;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;

/**
 * @author Daniel Tuerk
 */
public class PalettePanel extends org.gwtbootstrap3.client.ui.Panel {

    private final PickupDragController dragController;

    private final Map<String, FlowPanel> paletteGroupMapping = new HashMap<String, FlowPanel>();
    private final PanelBody panelBody;

    public PalettePanel(PickupDragController dragController) {
        this.dragController = dragController;

        setWidth("200px");
        addStyleName("palette");

        PanelHeader panelHeader = new PanelHeader();
        panelHeader.setText("Widget Palette");
        add(panelHeader);

        panelBody = new PanelBody();
        add(panelBody);

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
        paletteWidget.getElement().getStyle().setMarginLeft(5, Style.Unit.PX);
        paletteWidget.getElement().getStyle().setMarginBottom(5, Style.Unit.PX);
        if (index > -1) {
            paletteGroupMapping.get(widget.getPaletteTitle()).insert(paletteWidget, index);
        } else {
            paletteGroupMapping.get(widget.getPaletteTitle()).add(paletteWidget);
        }
    }

    public void addPaletteItem(AbstractSvgTrackWidget widget) {
        widget.setEnabled(true);
        // TODO: bullshit - only done to register the double click handler
        new EditorPaletteWidget(widget);
        addPaletteItem(widget, -1);
    }

}
