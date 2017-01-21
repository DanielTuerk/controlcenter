package net.wbz.moba.controlcenter.web.client.model.track.signal;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGPointList;
import org.vectomatic.dom.svg.OMSVGPolygonElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

/**
 * Create the SVG elements for the different types of an
 * {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal}.
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

    private final static String GREEN = SVGConstants.CSS_LIMEGREEN_VALUE;
    private final static String RED = SVGConstants.CSS_RED_VALUE;
    private final static String YELLOW = SVGConstants.CSS_YELLOW_VALUE;
    private final static String WHITE = SVGConstants.CSS_WHITE_VALUE;
    private final static String GRAY = SVGConstants.CSS_GRAY_VALUE;

    public enum HEIGHT_LEVEL {
        H1, H2, H3
    }

    public enum WIDTH_LEVEL {
        W1, W2
    }

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

    public void updateSvgContent(Signal.TYPE signalType, Signal.LIGHT light, boolean state, OMSVGDocument doc,
            OMSVGSVGElement svg) {

        if (!state)
            return;

        switch (signalType) {
            case BLOCK:
                switch (light) {
                    case RED1:
                        addLight(doc, svg, RED, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H2);
                        // addLightMiddleLeft(doc, svg, state ? RED : GRAY);
                        break;
                    case GREEN1:
                        addLight(doc, svg, GREEN, WIDTH_LEVEL.W2, HEIGHT_LEVEL.H2);
                        // addLightMiddleRight(doc, svg, state ? GREEN : GRAY);
                        break;
                }
                break;
            case EXIT:
                switch (light) {
                    case RED1:
                        addLight(doc, svg, RED, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H1);
                        break;
                    case RED2:
                        addLight(doc, svg, RED, WIDTH_LEVEL.W2, HEIGHT_LEVEL.H1);
                        break;
                    case GREEN1:
                        addLight(doc, svg, GREEN, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H1);
                        break;
                    case YELLOW1:
                        addLight(doc, svg, YELLOW, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H3);
                        break;
                    case WHITE:
                        addLight(doc, svg, WHITE, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H3);
                        addLight(doc, svg, WHITE, WIDTH_LEVEL.W2, HEIGHT_LEVEL.H2);
                        break;
                }
                break;
            // case ENTER:
            // switch (function) {
            // case HP0:
            // addTwoLightsMiddle(doc, svg, RED, GRAY);
            // break;
            // case HP1:
            // addTwoLightsMiddle(doc, svg, GRAY, GREEN);
            // break;
            // case HP2:
            // addTwoTopAndBottom(doc, svg, GREEN, YELLOW, false);
            // break;
            // case HP0_SH1:
            // break;
            // }
            // break;
            // case BEFORE:
            // switch (function) {
            // case HP0:
            // addTwoTopAndBottom(doc, svg, YELLOW, YELLOW, false);
            // break;
            // case HP1:
            // addTwoTopAndBottom(doc, svg, GREEN, GREEN, true);
            // break;
            // case HP2:
            // svg.appendChild(SvgTrackUtil.createCircle(doc, 19f, 19f, 5f, GREEN));
            // svg.appendChild(SvgTrackUtil.createCircle(doc, 6f, 6f, 5f, YELLOW));
            // break;
            // }
            // break;
        }
    }

    private void addLight(OMSVGDocument doc, OMSVGSVGElement svg, String color, WIDTH_LEVEL widthLevel,
            HEIGHT_LEVEL heightLevel) {
        float y = widthLevel == WIDTH_LEVEL.W1 ? 6f : 20f;
        float x = 0f;
        switch (heightLevel) {
            case H1:
                x = 20f;
                break;
            case H2:
                x = 12.5f;
                break;
            case H3:
                x = 5f;
                break;
            // case H4:
            // x = 25f;
            // break;
            // case H5:
            // x = 31f;
            // break;
        }
        svg.appendChild(SvgTrackUtil.createCircle(doc, x, y, 4f, color));
    }

    /**
     * Add the svg for the given {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION} of the
     * {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE} to the
     * {@link org.vectomatic.dom.svg.OMSVGSVGElement}.
     *
     * @param signalType {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE}
     * @param function {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION}
     * @param doc {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg {@link org.vectomatic.dom.svg.OMSVGSVGElement}
     */
    public void addSvgContent(Signal.TYPE signalType, Signal.FUNCTION function, OMSVGDocument doc,
            OMSVGSVGElement svg) {
        addTrackRectangle(doc, svg);

        switch (signalType) {
            case BLOCK:
                switch (function) {
                    case HP0:
                        // addTwoLightsMiddle(doc, svg, RED, GRAY);
                        addLight(doc, svg, RED, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H2);
                        // addLight(doc,svg,GRAY,WIDTH_LEVEL.W2,HEIGHT_LEVEL.H2);
                        break;
                    case HP1:
                        // addTwoLightsMiddle(doc, svg, GRAY, GREEN);
                        // addLight(doc,svg,GRAY,WIDTH_LEVEL.W1,HEIGHT_LEVEL.H2);
                        addLight(doc, svg, GREEN, WIDTH_LEVEL.W2, HEIGHT_LEVEL.H2);
                        break;
                }
                break;
            case EXIT:
                switch (function) {
                    case HP0:
                        addLight(doc, svg, RED, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H2);
                        addLight(doc, svg, RED, WIDTH_LEVEL.W2, HEIGHT_LEVEL.H2);
                        break;
                    case HP1:
                        addLight(doc, svg, GREEN, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H1);
                        break;
                    case HP2:
                        addLight(doc, svg, GREEN, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H1);
                        addLight(doc, svg, YELLOW, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H3);
                        break;
                    case HP0_SH1:
                        addLight(doc, svg, RED, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H1);
                        addLight(doc, svg, WHITE, WIDTH_LEVEL.W2, HEIGHT_LEVEL.H2);
                        addLight(doc, svg, WHITE, WIDTH_LEVEL.W1, HEIGHT_LEVEL.H3);
                        break;
                }
                break;
            case ENTER:
                switch (function) {
                    case HP0:
                        addTwoLightsMiddle(doc, svg, RED, GRAY);
                        break;
                    case HP1:
                        addTwoLightsMiddle(doc, svg, GRAY, GREEN);
                        break;
                    case HP2:
                        addTwoTopAndBottom(doc, svg, GREEN, YELLOW, false);
                        break;
                    case HP0_SH1:
                        break;
                }
                break;
            case BEFORE:
                switch (function) {
                    case HP0:
                        addTwoTopAndBottom(doc, svg, YELLOW, YELLOW, false);
                        break;
                    case HP1:
                        addTwoTopAndBottom(doc, svg, GREEN, GREEN, true);
                        break;
                    case HP2:
                        svg.appendChild(SvgTrackUtil.createCircle(doc, 19f, 19f, 5f, GREEN));
                        svg.appendChild(SvgTrackUtil.createCircle(doc, 6f, 6f, 5f, YELLOW));
                        break;
                }
                break;
        }
    }

    /**
     * Draw two lights horizontal in the middle.
     *
     * @param doc {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg {@link org.vectomatic.dom.svg.OMSVGSVGElement}
     * @param leftLightColor color of the left light
     * @param rightLightColor color of the right light
     */
    private void addTwoLightsMiddle(OMSVGDocument doc, OMSVGSVGElement svg, String leftLightColor,
            String rightLightColor) {
        addLightMiddleLeft(doc, svg, leftLightColor);
        addLightMiddleRight(doc, svg, rightLightColor);

    }

    private void addLightMiddleLeft(OMSVGDocument doc, OMSVGSVGElement svg, String leftLightColor) {
        svg.appendChild(SvgTrackUtil.createCircle(doc, 12.5f, 6f, 5f, leftLightColor));
    }

    private void addLightMiddleRight(OMSVGDocument doc, OMSVGSVGElement svg, String rightLightColor) {
        svg.appendChild(SvgTrackUtil.createCircle(doc, 12.5f, 19f, 5f, rightLightColor));
    }

    /**
     * Draw two lights. First on the top and the second at bottom.
     *
     * @param doc {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg {@link org.vectomatic.dom.svg.OMSVGSVGElement}
     * @param topLightColor color of the left light
     * @param bottomLightColor color of the right light
     * @param left <code>true</code> to draw at the left, otherwise right
     */
    private void addTwoTopAndBottom(OMSVGDocument doc, OMSVGSVGElement svg, String topLightColor,
            String bottomLightColor, boolean left) {
        svg.appendChild(SvgTrackUtil.createCircle(doc, 19f, left ? 6f : 19f, 5f, topLightColor));
        svg.appendChild(SvgTrackUtil.createCircle(doc, 6f, left ? 6f : 19f, 5f, bottomLightColor));
    }

    /**
     * Add the track of straight as rectangle.
     *
     * @param doc {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg {@link org.vectomatic.dom.svg.OMSVGSVGElement}
     */
    public void addTrackRectangle(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createRectangle(doc, 0f, 10f, 25f, 5f));
    }

    /**
     * Adding the preview svg by {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE} to the given
     * {@link org.vectomatic.dom.svg.OMSVGSVGElement}.
     *
     * @param signalType {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE}
     * @param doc {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg {@link org.vectomatic.dom.svg.OMSVGSVGElement}
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
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 100f, circleWidth,
                        SVGConstants.CSS_RED_VALUE));
                svg.appendChild(doc.createSVGTextElement(textLeftX, 105f, (short) 5, SHORTCUT_RED_1));

                // green light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleRightX, 100f, circleWidth, GREEN));
                svg.appendChild(doc.createSVGTextElement(textRightX, 105f, (short) 5, SHORTCUT_GREEN_1));

                break;
            case ENTER:
                svg.appendChild(createPreviewPlatePolygon(doc, svg));

                // red light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 100f, circleWidth,
                        SVGConstants.CSS_RED_VALUE));
                svg.appendChild(doc.createSVGTextElement(textLeftX, 105f, (short) 5, SHORTCUT_RED_1));

                // yellow light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleRightX, 100f, circleWidth, YELLOW));
                svg.appendChild(doc.createSVGTextElement(textRightX, 105f, (short) 5, SHORTCUT_YELLOW_1));

                // green light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleRightX, 18f, circleWidth, GREEN));
                svg.appendChild(doc.createSVGTextElement(textRightX, 18f, (short) 5, SHORTCUT_GREEN_1));

                break;
            case EXIT:
                svg.appendChild(createPreviewPlatePolygon(doc, svg));

                // red 1 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 40f, circleWidth,
                        SVGConstants.CSS_RED_VALUE));
                svg.appendChild(doc.createSVGTextElement(textLeftX, 45f, (short) 5, SHORTCUT_RED_1));
                // red 2 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleRightX, 40f, circleWidth,
                        SVGConstants.CSS_RED_VALUE));
                svg.appendChild(doc.createSVGTextElement(textRightX, 45f, (short) 5, SHORTCUT_RED_2));

                // white light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleRightX, 60f, circleWidth,
                        SVGConstants.CSS_WHITE_VALUE));
                svg.appendChild(doc.createSVGTextElement(textRightX, 65f, (short) 5, SHORTCUT_WHITE));

                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 78f, circleWidth,
                        SVGConstants.CSS_WHITE_VALUE));
                svg.appendChild(doc.createSVGTextElement(textLeftX, 84f, (short) 5, SHORTCUT_WHITE));

                // yellow light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 100f, circleWidth, YELLOW));
                svg.appendChild(doc.createSVGTextElement(0, 105f, (short) 5, SHORTCUT_YELLOW_1));

                // green light
                svg.appendChild(SvgTrackUtil.createCircle(doc, circleLeftX, 18f, circleWidth, GREEN));
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
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + beforePlateWidth + beforePlateHeight
                        + beforePlateHeight, beforePlateHeight));
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + beforePlateWidth + 2 * beforePlateHeight, 2
                        * beforePlateHeight));
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + 2 * beforePlateHeight, 2 * beforePlateHeight
                        + beforePlateWidth));
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT + beforePlateHeight, 2 * beforePlateHeight
                        + beforePlateWidth));
                points.appendItem(svg.createSVGPoint(PREVIEW_PADDING_LEFT, beforePlateHeight + beforePlateWidth));

                polygon.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE, GRAY);
                polygon.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_VALUE, SVGConstants.CSS_BLACK_VALUE);
                svg.appendChild(polygon);

                // yellow 1 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, PREVIEW_PADDING_LEFT + 45, 25f, circleWidth, YELLOW));
                svg.appendChild(doc.createSVGTextElement(PREVIEW_PADDING_LEFT + 5f, 28f, (short) 5, SHORTCUT_YELLOW_1));

                // green 1 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, PREVIEW_PADDING_LEFT + 45, 65f, circleWidth, GREEN));
                svg.appendChild(doc.createSVGTextElement(PREVIEW_PADDING_LEFT + 70f, 68f, (short) 5, SHORTCUT_GREEN_1));

                // yellow 2 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, PREVIEW_PADDING_LEFT + 15, 65f, circleWidth, YELLOW));
                svg.appendChild(doc.createSVGTextElement(0, 68f, (short) 5, SHORTCUT_YELLOW_2));

                // green 2 light
                svg.appendChild(SvgTrackUtil.createCircle(doc, PREVIEW_PADDING_LEFT + 75, 25f, circleWidth, GREEN));
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
        polygon.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE, GRAY);
        polygon.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_VALUE, SVGConstants.CSS_BLACK_VALUE);
        return polygon;
    }
}
