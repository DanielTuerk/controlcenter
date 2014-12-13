package net.wbz.moba.controlcenter.web.client.model.track.signal;

import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGPointList;
import org.vectomatic.dom.svg.OMSVGPolygonElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * Create the SVG elements for the different types of an {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal}.
 *
 * @author Daniel Tuerk
 */
public class SignalSvgBuilder {

    private final static SignalSvgBuilder INSTANCE = new SignalSvgBuilder();

    private final static float PREVIEW_PADDING_LEFT = 15f;
    private final static float PREVIEW_WIDTH = 50f;
    private final static float PREVIEW_HEIGHT = 120f;

    public static final String SHORTCUT_RED_1 = "r1";
    public static final String SHORTCUT_RED_2 = "r2";
    public static final String SHORTCUT_GREEN_1 = "g1";
    public static final String SHORTCUT_GREEN_2 = "g2";
    public static final String SHORTCUT_WHITE = "w";
    public static final String SHORTCUT_YELLOW_1 = "y1";
    public static final String SHORTCUT_YELLOW_2 = "y2";

    private SignalSvgBuilder() {
    }

    /**
     * Return the instance of the singleton.
     *
     * @return {@link net.wbz.moba.controlcenter.web.client.model.track.signal.SignalSvgBuilder} instance
     */
    public static SignalSvgBuilder getInstance() {
        return INSTANCE;
    }

    /**
     * Adding the svg content for the {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE} to the
     * given {@link org.vectomatic.dom.svg.OMSVGSVGElement}.
     *
     * @param signalType {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE}
     * @param doc        {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg        {@link org.vectomatic.dom.svg.OMSVGSVGElement}
     */
    public void addSvgContent(Signal.TYPE signalType, OMSVGDocument doc, OMSVGSVGElement svg) {
        switch (signalType) {
            case BLOCK:
                addBlockSignalSvgContent(doc, svg, SVGConstants.CSS_RED_VALUE, SVGConstants.CSS_WHITE_VALUE);
                break;
        }
    }

    public void addActiveStateSvgContent(Signal.TYPE signalType, OMSVGDocument doc, OMSVGSVGElement svg) {
        switch (signalType) {
            case BLOCK:
                addBlockSignalSvgContent(doc, svg, SVGConstants.CSS_WHITE_VALUE, SVGConstants.CSS_GREEN_VALUE);
                break;
        }
    }

    private void addBlockSignalSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color, String colorActive) {
        svg.appendChild(SvgTrackUtil.createRectangle(doc, 0f, 10f, 25f, 5f));
        svg.appendChild(SvgTrackUtil.createCircle(doc, 12.5f, 6f, 5f, color));
        svg.appendChild(SvgTrackUtil.createCircle(doc, 12.5f, 19f, 5f, colorActive));
    }

    /**
     * Adding the preview svg by {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE} to the given
     * {@link org.vectomatic.dom.svg.OMSVGSVGElement}.
     *
     * @param signalType {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE}
     * @param doc        {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg        {@link org.vectomatic.dom.svg.OMSVGSVGElement}
     */
    public void addPreview(Signal.TYPE signalType, OMSVGDocument doc, OMSVGSVGElement svg) {
        float circleLeftX = PREVIEW_PADDING_LEFT + 12.5f;
        float textRightX = PREVIEW_PADDING_LEFT + 52f;
        float circleRightX = 50f;
        float textLeftX = 0f;
        float circleWidth = 8f;
        switch (signalType) {
            case BLOCK:
                svg.appendChild(createPreviewPlatePolygon(doc, svg));

                // red light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 100f, circleWidth, SVGConstants.CSS_RED_VALUE));
                svg.appendChild(doc.createSVGTextElement(textLeftX, 105f, (short) 5, SHORTCUT_RED_1));

                // green light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleRightX, 100f, circleWidth, SVGConstants.CSS_GREEN_VALUE));
                svg.appendChild(doc.createSVGTextElement(textRightX, 105f, (short) 5, SHORTCUT_GREEN_1));

                break;
            case ENTER:
                svg.appendChild(createPreviewPlatePolygon(doc, svg));

                // red light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 100f, circleWidth, SVGConstants.CSS_RED_VALUE));
                svg.appendChild(doc.createSVGTextElement(textLeftX, 105f, (short) 5, SHORTCUT_RED_1));

                // yellow light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleRightX, 100f, circleWidth, SVGConstants.CSS_YELLOW_VALUE));
                svg.appendChild(doc.createSVGTextElement(textRightX, 105f, (short) 5, SHORTCUT_YELLOW_1));

                // green light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleRightX, 18f, circleWidth, SVGConstants.CSS_GREEN_VALUE));
                svg.appendChild(doc.createSVGTextElement(textRightX, 18f, (short) 5, SHORTCUT_GREEN_1));

                break;
            case EXIT:
                svg.appendChild(createPreviewPlatePolygon(doc, svg));

                // red 1 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 40f, circleWidth, SVGConstants.CSS_RED_VALUE));
                svg.appendChild(doc.createSVGTextElement(textLeftX, 45f, (short) 5, SHORTCUT_RED_1));
                // red 2 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleRightX, 40f, circleWidth, SVGConstants.CSS_RED_VALUE));
                svg.appendChild(doc.createSVGTextElement(textRightX, 45f, (short) 5, SHORTCUT_RED_2));

                // white light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleRightX, 60f, circleWidth, SVGConstants.CSS_WHITE_VALUE));
                svg.appendChild(doc.createSVGTextElement(textRightX, 65f, (short) 5, SHORTCUT_WHITE));

                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 78f, circleWidth, SVGConstants.CSS_WHITE_VALUE));
                svg.appendChild(doc.createSVGTextElement(textLeftX, 84f, (short) 5, SHORTCUT_WHITE));

                // yellow light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 100f, circleWidth, SVGConstants.CSS_YELLOW_VALUE));
                svg.appendChild(doc.createSVGTextElement(0, 105f, (short) 5, SHORTCUT_YELLOW_1));

                // green light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 18f, circleWidth, SVGConstants.CSS_GREEN_VALUE));
                svg.appendChild(doc.createSVGTextElement(textLeftX, 18f, (short) 5, SHORTCUT_GREEN_1));

                break;
            case BEFORE:
                float beforePlateWidth = 50f;
                float beforePlateHeight = 20f;
                OMSVGPolygonElement polygon = doc.createSVGPolygonElement();
                OMSVGPointList points = polygon.getPoints();
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT, beforePlateWidth));
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + beforePlateWidth, 0));
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + beforePlateWidth + beforePlateHeight, 0));
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + beforePlateWidth + beforePlateHeight + beforePlateHeight, beforePlateHeight));
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + beforePlateWidth + 2 * beforePlateHeight, 2 * beforePlateHeight));
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + 2 * beforePlateHeight, 2 * beforePlateHeight + beforePlateWidth));
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + beforePlateHeight, 2 * beforePlateHeight + beforePlateWidth));
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT, beforePlateHeight + beforePlateWidth));


                polygon.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE, SVGConstants.CSS_GRAY_VALUE);
                polygon.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_VALUE, SVGConstants.CSS_BLACK_VALUE);
                svg.appendChild(polygon);

                // yellow 1 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, PREVIEW_PADDING_LEFT + 45, 25f, circleWidth, SVGConstants.CSS_YELLOW_VALUE));
                svg.appendChild(doc.createSVGTextElement(PREVIEW_PADDING_LEFT + 5f, 28f, (short) 5, SHORTCUT_YELLOW_1));

                // green 1 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, PREVIEW_PADDING_LEFT + 45, 65f, circleWidth, SVGConstants.CSS_GREEN_VALUE));
                svg.appendChild(doc.createSVGTextElement(PREVIEW_PADDING_LEFT + 70f, 68f, (short) 5, SHORTCUT_GREEN_1));

                // yellow 2 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, PREVIEW_PADDING_LEFT + 15, 65f, circleWidth, SVGConstants.CSS_YELLOW_VALUE));
                svg.appendChild(doc.createSVGTextElement(0, 68f, (short) 5, SHORTCUT_YELLOW_2));

                // green 2 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, PREVIEW_PADDING_LEFT + 75, 25f, circleWidth, SVGConstants.CSS_GREEN_VALUE));
                svg.appendChild(doc.createSVGTextElement(PREVIEW_PADDING_LEFT + 95, 28f, (short) 5, SHORTCUT_GREEN_2));
                break;
        }
    }

    private OMSVGPolygonElement createPreviewPlatePolygon(OMSVGDocument doc, OMSVGSVGElement svg) {
        OMSVGPolygonElement polygon = doc.createSVGPolygonElement();
        OMSVGPointList points = polygon.getPoints();
        points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT, 10));
        points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + 10, 0));
        points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + 40, 0));
        points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + PREVIEW_WIDTH, 10));
        points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + PREVIEW_WIDTH, PREVIEW_HEIGHT));
        points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT, PREVIEW_HEIGHT));
        polygon.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE, SVGConstants.CSS_GRAY_VALUE);
        polygon.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_VALUE, SVGConstants.CSS_BLACK_VALUE);
        return polygon;
    }
}
