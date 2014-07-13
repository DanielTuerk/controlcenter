package net.wbz.moba.controlcenter.web.client.util;

import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * Util to create SVG components for the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}s.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class SvgTrackUtil {

    /**
     * Default color for the track part widgets.
     */
    private final static String TRACK_COLOR = SVGConstants.CSS_BLACK_VALUE;

    /**
     * Create a rectangle.
     * <p/>
     * x,y -------- < width > --
     * |                       |
     * |                   < height >
     * |                       |
     * -------------------------
     *
     * @param doc    {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param x      start x value
     * @param y      start y value
     * @param width  width as pixel
     * @param height height as pixel
     * @return {@link OMSVGRectElement}
     */
    public static OMSVGRectElement createRectangle(OMSVGDocument doc, float x, float y, float width, float height) {
        OMSVGRectElement rect = doc.createSVGRectElement(x, y, width, height, 0f, 0f);
        rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, TRACK_COLOR);
        rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, TRACK_COLOR);
        return rect;
    }

    /**
     * Create a line.
     * <p/>
     * fromX --------------- toX
     * |                       |
     * |                       |
     * |                       |
     * fromY --------------- toY
     *
     * @param doc     {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param fromX   start x value
     * @param fromY   start y value
     * @param toX     end x value
     * @param toY     end x value
     * @param widthPx width as pixel
     * @return {@link OMSVGLineElement}
     */
    public static OMSVGLineElement createLine(OMSVGDocument doc, float fromX, float fromY, float toX, float toY, int widthPx) {
        OMSVGLineElement lineElement = doc.createSVGLineElement(fromX, fromY, toX, toY);
        lineElement.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_VALUE, TRACK_COLOR);
        lineElement.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, widthPx + "px");
        return lineElement;
    }

    /**
     * Create a circle.
     *
     * @param doc    {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param x      position x
     * @param y      position y
     * @param radius radius
     * @param color  color to fill circle {@link SVGConstants}
     * @return {@link OMSVGCircleElement}
     */
    public static OMSVGCircleElement createCircle(OMSVGDocument doc, float x, float y, float radius, String color) {
        OMSVGCircleElement circleElement = doc.createSVGCircleElement(x, y, radius);
        circleElement.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_VALUE, TRACK_COLOR);
        circleElement.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE, color);
        circleElement.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "2px");
        return circleElement;
    }

}
