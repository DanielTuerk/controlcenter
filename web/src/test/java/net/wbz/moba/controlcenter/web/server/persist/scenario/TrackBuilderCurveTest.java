package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.List;

import net.wbz.moba.controlcenter.web.shared.scenario.TrackNotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve.DIRECTION;

/**
 * Test {@link TrackBuilder} for {@link net.wbz.moba.controlcenter.web.shared.track.model.Curve}.
 *
 * @author Daniel Tuerk
 */
public class TrackBuilderCurveTest extends AbstractTrackBuilderTest {

    private int startAddress;
    private int startBit;
    private int endAddress;
    private int endBit;

    @BeforeMethod
    public void beforeMethod() {
        startAddress = 20;
        startBit = 1;
        endAddress = 30;
        endBit = 3;
    }

    /**
     * <pre>
     * S
     * # E
     * </pre>
     */
    @Test
    public void testCurve_TopRight() throws TrackNotFoundException {
        testSimpleCurve(Lists.newArrayList(
                createVerticalBlockStraight(1, 1, startAddress, startBit, true),
                createCurve(1, 2, Curve.DIRECTION.TOP_RIGHT),
                createHorizontalBlockStraight(2, 2, endAddress, endBit, true)));
    }

    /**
     * <pre>
     *   S
     * E #
     * </pre>
     */
    @Test
    public void testCurve_TopLeft() throws TrackNotFoundException {
        testSimpleCurve(Lists.newArrayList(
                createVerticalBlockStraight(2, 1, startAddress, startBit, true),
                createCurve(2, 2, DIRECTION.TOP_LEFT),
                createHorizontalBlockStraight(1, 2, endAddress, endBit, true)));
    }

    /**
     * <pre>
     * # E
     * S
     * </pre>
     */
    @Test
    public void testCurve_BottomRight() throws TrackNotFoundException {
        testSimpleCurve(Lists.newArrayList(
                createVerticalBlockStraight(1, 2, startAddress, startBit, true),
                createCurve(1, 1, DIRECTION.BOTTOM_RIGHT),
                createHorizontalBlockStraight(2, 1, endAddress, endBit, true)));
    }

    /**
     * <pre>
     * E #
     *   S
     * </pre>
     */
    @Test
    public void testCurve_BottomLeft() throws TrackNotFoundException {
        testSimpleCurve(Lists.newArrayList(
                createHorizontalBlockStraight(1, 1, endAddress, endBit, true),
                createCurve(2, 1, DIRECTION.BOTTOM_LEFT),
                createVerticalBlockStraight(2, 2, startAddress, startBit, true)));
    }

    private void testSimpleCurve(List<? extends AbstractTrackPart> trackParts) throws TrackNotFoundException {
        testSimpleTrack(trackParts, startAddress, startBit, endAddress, endBit, 3);
    }

}
