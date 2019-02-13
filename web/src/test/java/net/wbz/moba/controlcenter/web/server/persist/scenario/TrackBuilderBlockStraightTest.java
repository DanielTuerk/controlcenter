package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Track;
import net.wbz.moba.controlcenter.web.shared.scenario.TrackNotFoundException;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve.DIRECTION;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import org.testng.annotations.Test;

/**
 * Test track for {@link net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight} with length.
 *
 * @author Daniel Tuerk
 */
public class TrackBuilderBlockStraightTest extends AbstractTrackBuilderTest {

    /**
     * <pre>
     * SS # E
     * </pre>
     */
    @Test
    public void testHorizontalForward() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        testBlockLengthForward(startAddress, startBit, endAddress, endBit, 2);
        testBlockLengthForward(startAddress, startBit, endAddress, endBit, 3);
        testBlockLengthForward(startAddress, startBit, endAddress, endBit, 4);
    }

    /**
     * <pre>
     * S
     * S
     * #
     * E
     * </pre>
     */
    @Test
    public void testVerticalForward() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        testBlockLengthForwardVertical(startAddress, startBit, endAddress, endBit, 2);
        testBlockLengthForwardVertical(startAddress, startBit, endAddress, endBit, 3);
        testBlockLengthForwardVertical(startAddress, startBit, endAddress, endBit, 4);
    }

    /**
     * <pre>
     * E # SS
     * </pre>
     */
    @Test
    public void testHorizontalBackward() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        testBlockLengthBackward(startAddress, startBit, endAddress, endBit, 2);
        testBlockLengthBackward(startAddress, startBit, endAddress, endBit, 3);
        testBlockLengthBackward(startAddress, startBit, endAddress, endBit, 4);
    }

    /**
     * <pre>
     * EE # SS
     * </pre>
     */
    @Test
    public void testHorizontalBackward2() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        testSimpleTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, endAddress, endBit, true, 2),
            createHorizontalStraight(3, 1),
            createHorizontalBlockStraight(4, 1, startAddress, startBit, true, 2)),
            startAddress, startBit,
            endAddress, endBit,
            3);
    }

    /**
     * <pre>
     * E
     * #
     * S
     * S
     * </pre>
     */
    @Test
    public void testVerticalBackward() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        testBlockLengthBackwardVertical(startAddress, startBit, endAddress, endBit, 2);
        testBlockLengthBackwardVertical(startAddress, startBit, endAddress, endBit, 3);
        testBlockLengthBackwardVertical(startAddress, startBit, endAddress, endBit, 4);
    }

    /**
     * <pre>
     *      S
     *      S
     *      S
     *      S
     * EE x x
     * </pre>
     */
    @Test
    public void testLongBlocksBackward() throws TrackNotFoundException {
        int startAddress = 10;
        int startBit = 1;
        int endAddress = 11;
        int endBit = 2;

        TrackBlock leftStart = createTrackBlock(startAddress, startBit, true);
        leftStart.setId(1L);
        TrackBlock rightStart = createTrackBlock(12, 2, true);
        rightStart.setId(2L);

        BlockStraight startBlockStraight2 = new BlockStraight();
        startBlockStraight2.setId(3L);
        startBlockStraight2.setDirection(Straight.DIRECTION.VERTICAL);
        startBlockStraight2.setGridPosition(new GridPosition(9, 1));
        startBlockStraight2.setLeftTrackBlock(leftStart);
        startBlockStraight2.setRightTrackBlock(rightStart);
        startBlockStraight2.setBlockLength(4);

        TrackBlock endTrackBlock = createTrackBlock(endAddress, endBit, true);
        endTrackBlock.setId(4L);
        BlockStraight endBlockStraight = new BlockStraight();
        endBlockStraight.setDirection(Straight.DIRECTION.HORIZONTAL);
        endBlockStraight.setGridPosition(new GridPosition(6, 5));
        endBlockStraight.setRightTrackBlock(endTrackBlock);
        endBlockStraight.setBlockLength(2);

        Straight horizontalStraight = createHorizontalStraight(8, 5);
        horizontalStraight.setId(5L);

        Curve curve = createCurve(9, 5, DIRECTION.TOP_LEFT);
        curve.setId(6L);

        List<AbstractTrackPart> trackParts = Lists.newArrayList(
            startBlockStraight2,
            endBlockStraight,
            horizontalStraight,
            curve
            );
        mockTrack(trackParts);

        Route route = mockRoute();
        route.setStart(startBlockStraight2);
        route.setEnd(endTrackBlock);

        Track track = getTrackBuilder().build(route);
        Assert.assertEquals(trackParts.size(), track.getLength());
        Assert.assertEquals(0, track.getTrackBlocks().size());
        Assert.assertEquals(0, track.getTrackFunctions().size());
    }

    private void testBlockLengthBackwardVertical(int startAddress, int startBit, int endAddress, int endBit,
        int blockLength) throws TrackNotFoundException {
        testSimpleTrack(Lists.newArrayList(
            createVerticalBlockStraight(1, 1, endAddress, endBit, true),
            createVerticalStraight(1, 2),
            createVerticalBlockStraight(1, 3, startAddress, startBit, true, blockLength)), startAddress,
            startBit,
            endAddress,
            endBit, 3);
    }

    private void testBlockLengthForward(int startAddress, int startBit, int endAddress, int endBit, int blockLength)
        throws TrackNotFoundException {
        testSimpleTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, startAddress, startBit, true, blockLength),
            createHorizontalStraight(1 + blockLength, 1),
            createHorizontalBlockStraight(2 + blockLength, 1, endAddress, endBit, true)), startAddress, startBit,
            endAddress, endBit,
            3);
    }

    private void testBlockLengthForwardVertical(int startAddress, int startBit, int endAddress, int endBit,
        int blockLength)
        throws TrackNotFoundException {
        testSimpleTrack(Lists.newArrayList(
            createVerticalBlockStraight(1, 1, startAddress, startBit, true, blockLength),
            createVerticalStraight(1, 1 + blockLength),
            createVerticalBlockStraight(1, 2 + blockLength, endAddress, endBit, true)), startAddress, startBit,
            endAddress, endBit,
            3);
    }

    private void testBlockLengthBackward(int startAddress, int startBit, int endAddress, int endBit, int blockLength)
        throws TrackNotFoundException {
        testSimpleTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, endAddress, endBit, true),
            createHorizontalStraight(2, 1),
            createHorizontalBlockStraight(3, 1, startAddress, startBit, true, blockLength)), startAddress, startBit,
            endAddress,
            endBit, 3);
    }


}
