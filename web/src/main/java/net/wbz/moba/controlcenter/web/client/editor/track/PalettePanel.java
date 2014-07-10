package net.wbz.moba.controlcenter.web.client.editor.track;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;

import java.util.HashMap;
import java.util.Map;

public class PalettePanel extends VerticalPanel   {

    private final PickupDragController dragController;

    private final Map<String, FlowPanel> palleteGroupMapping = new HashMap<String, FlowPanel>();

    public PalettePanel(PickupDragController dragController) {
        this.dragController = dragController;

        setWidth("200px");
        addStyleName("pallete");
        setSpacing(2);
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        Label header = new Label("Widget Palette");
        add(header);


        for(Map.Entry<String,FlowPanel> entry: palleteGroupMapping.entrySet()) {
            add(new Label(entry.getKey()));
            add(entry.getValue());
        }


    }

    private void addPaletteItem(AbstractSvgTrackWidget widget, int index) {
        if (!palleteGroupMapping.containsKey(widget.getPaletteTitle())) {
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
                        addPaletteItem(clone.getPaletteWidgetItem(), index);
                    }
                    return result;
                }
            };
            palleteGroupMapping.put(widget.getPaletteTitle(), paletteGroup);
            add(new Label(widget.getPaletteTitle()));
            add(paletteGroup);
        }

        PaletteWidget paletteWidget = new PaletteWidget(widget);
        dragController.makeDraggable(paletteWidget);
        paletteWidget.getElement().getStyle().setFloat(Style.Float.LEFT);
        paletteWidget.getElement().getStyle().setMarginLeft(5, Style.Unit.PX);
        if (index > -1) {
            palleteGroupMapping.get(widget.getPaletteTitle()).insert(paletteWidget, index);
        } else {
            palleteGroupMapping.get(widget.getPaletteTitle()).add(paletteWidget);

        }
    }


    public void addPaletteItem(AbstractSvgTrackWidget widget) {
        addPaletteItem(widget, -1);
    }

}
