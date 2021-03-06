package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.google.common.collect.Lists;
import junit.framework.Assert;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Track;
import net.wbz.moba.controlcenter.web.shared.scenario.TrackNotFoundException;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve.DIRECTION;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.Turnout;
import net.wbz.moba.controlcenter.web.shared.track.model.Turnout.PRESENTATION;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import org.testng.annotations.Test;

/**
 * @author Daniel Tuerk
 */
public class TrackBuilderTest extends AbstractTrackBuilderTest {

    @Test(expectedExceptions = TrackNotFoundException.class, expectedExceptionsMessageRegExp = "no route given")
    public void testNoRoute() throws TrackNotFoundException {
        getTrackBuilder().build(null);
    }

    @Test(expectedExceptions = TrackNotFoundException.class, expectedExceptionsMessageRegExp = "no route start defined")
    public void testNoRouteStart() throws TrackNotFoundException {
        Route route = mockRoute();
        route.setEnd(new TrackBlock());
        getTrackBuilder().build(route);
    }

    @Test(expectedExceptions = TrackNotFoundException.class, expectedExceptionsMessageRegExp = "no route end defined")
    public void testNoRouteEnd() throws TrackNotFoundException {
        Route route = mockRoute();
        route.setStart(new BlockStraight());
        getTrackBuilder().build(route);
    }

    @Test(expectedExceptions = TrackNotFoundException.class, expectedExceptionsMessageRegExp = ".*invalid start.*invalid block function.*")
    public void testInvalidRouteStart() throws TrackNotFoundException {
        Route route = mockRoute();
        BlockStraight blockStraight = new BlockStraight();
        TrackBlock rightTrackBlock = new TrackBlock();
        rightTrackBlock.setBlockFunction(new BusDataConfiguration());
        blockStraight.setRightTrackBlock(rightTrackBlock);
        route.setStart(blockStraight);
        route.setEnd(createTrackBlock(11, 1, true));
        getTrackBuilder().build(route);
    }

    @Test(expectedExceptions = TrackNotFoundException.class, expectedExceptionsMessageRegExp = ".*invalid end.*invalid block function.*")
    public void testInvalidRouteEnd() throws TrackNotFoundException {
        Route route = mockRoute();
        TrackBlock end = new TrackBlock();
        end.setBlockFunction(new BusDataConfiguration());
        route.setEnd(end);
        route.setStart(createBlockStraight(11, 1, true));
        getTrackBuilder().build(route);
    }

    /**
     * <pre>
     * S # E
     * </pre>
     */
    @Test
    public void testSimpleForward() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        BlockStraight startBlock = createHorizontalBlockStraight(1, 1, createTrackBlock(startAddress, startBit, true));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        testSimpleTrack(Lists.newArrayList(
            startBlock,
            createHorizontalStraight(2, 1),
            createHorizontalBlockStraight(3, 1, endBlock)),
            startBlock, endBlock,
            3);
    }

    /**
     * <pre>
     * E # S
     * </pre>
     */
    @Test
    public void testSimpleBackward() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(3, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        testSimpleTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, endBlock),
            createHorizontalStraight(2, 1),
            startBlockStraight),
            startBlockStraight, endBlock, 3);
    }

    /**
     * <pre>
     * # # S #
     * #     #
     * # # E #
     * </pre>
     */
    @Test
    public void testForward() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(3, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        testSimpleTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
                createHorizontalStraight(2, 1),
            startBlockStraight,
                createCurve(4, 1, Curve.DIRECTION.BOTTOM_LEFT),
                createVerticalStraight(4, 2),
                createCurve(4, 3, Curve.DIRECTION.TOP_LEFT),
            createHorizontalBlockStraight(3, 3, endBlock),
                createHorizontalStraight(2, 3),
                createCurve(1, 3, Curve.DIRECTION.TOP_RIGHT),
            createVerticalStraight(1, 2)),
            startBlockStraight, endBlock, 5);
    }

    /**
     * <pre>
     * # S # #
     * #     #
     * # E # #
     * </pre>
     */
    @Test
    public void testBackward() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(2, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        testSimpleTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
            startBlockStraight,
                createHorizontalStraight(3, 1),
                createCurve(4, 1, Curve.DIRECTION.BOTTOM_LEFT),
                createVerticalStraight(4, 2),
                createCurve(4, 3, Curve.DIRECTION.TOP_LEFT),
                createHorizontalStraight(3, 3),
            createHorizontalBlockStraight(2, 3, endBlock),
                createCurve(1, 3, Curve.DIRECTION.TOP_RIGHT),
            createVerticalStraight(1, 2)),
            startBlockStraight, endBlock, 5);
    }

    /**
     * <pre>
     * # # S #
     * W     #
     * # # E #
     * </pre>
     */
    @Test
    public void testForwardWaypoint() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        int blockAddress = 50;
        int blockBit = 7;

        int waypointX = 1;
        int waypointY = 2;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(3, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
                createHorizontalStraight(2, 1),
            startBlockStraight,
                createCurve(4, 1, Curve.DIRECTION.BOTTOM_LEFT),
            createVerticalBlockStraight(4, 2, createTrackBlock(40, 4, true)),
                createCurve(4, 3, Curve.DIRECTION.TOP_LEFT),
            createHorizontalBlockStraight(3, 3, endBlock),
            createHorizontalBlockStraight(2, 3, createTrackBlock(blockAddress, blockBit, true)),
                createCurve(1, 3, Curve.DIRECTION.TOP_RIGHT),
                createVerticalStraight(waypointX, waypointY)));

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);
        route.setWaypoints(Lists.newArrayList(new GridPosition(waypointX, waypointY)));

        Track track = getTrackBuilder().build(route);
        Assert.assertEquals(7, track.getLength());
        Assert.assertEquals(1, track.getTrackBlocks().size());
        Assert.assertEquals(new BusDataConfiguration(1, blockAddress, blockBit, true),
                track.getTrackBlocks().iterator().next().getBlockFunction());
        Assert.assertEquals(0, track.getTrackFunctions().size());
    }

    /**
     * <pre>
     * # # E #
     * W     #
     * # # S #
     * </pre>
     */
    @Test
    public void testBackwardWaypoint() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        int blockAddress = 50;
        int blockBit = 7;

        int waypointX = 1;
        int waypointY = 2;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(3, 3,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
                createHorizontalStraight(2, 1),
            createHorizontalBlockStraight(3, 1, endBlock),
                createCurve(4, 1, Curve.DIRECTION.BOTTOM_LEFT),
            createVerticalBlockStraight(4, 2, createTrackBlock(40, 4, true)),
                createCurve(4, 3, Curve.DIRECTION.TOP_LEFT),
            startBlockStraight,
            createHorizontalBlockStraight(2, 3, createTrackBlock(blockAddress, blockBit, true)),
                createCurve(1, 3, Curve.DIRECTION.TOP_RIGHT),
                createVerticalStraight(waypointX, waypointY)));

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);
        route.setWaypoints(Lists.newArrayList(new GridPosition(waypointX, waypointY)));

        Track track = getTrackBuilder().build(route);
        Assert.assertEquals(7, track.getLength());
        Assert.assertEquals(1, track.getTrackBlocks().size());
        Assert.assertEquals(new BusDataConfiguration(1, blockAddress, blockBit, true),
                track.getTrackBlocks().iterator().next().getBlockFunction());
        Assert.assertEquals(0, track.getTrackFunctions().size());
    }

    /**
     * <pre>
     * S # - E
     * </pre>
     */
    @Test(expectedExceptions = TrackNotFoundException.class)
    public void testException() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(2, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
            startBlockStraight,
                createHorizontalStraight(3, 1),
            createHorizontalBlockStraight(5, 1, endBlock)));
        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);
        getTrackBuilder().build(route);
    }

    /**
     * <pre>
     * S - E
     * </pre>
     */
    @Test(expectedExceptions = TrackNotFoundException.class)
    public void testExceptionNoTrack() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(2, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
            startBlockStraight,
            createHorizontalBlockStraight(4, 1, endBlock)));
        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);
        getTrackBuilder().build(route);
    }

    /**
     * <pre>
     * W # S # E
     * </pre>
     */
    @Test(expectedExceptions = TrackNotFoundException.class)
    public void testExceptionWaypoint() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;
        int waypointX = 1;
        int waypointY = 1;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(3, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
                createHorizontalStraight(waypointX, waypointY),
                createHorizontalStraight(2, 1),
            startBlockStraight,
                createHorizontalStraight(4, 1),
            createHorizontalBlockStraight(5, 1, endBlock)));
        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);
        route.setWaypoints(Lists.newArrayList(new GridPosition(waypointX, waypointY)));
        getTrackBuilder().build(route);
    }

    /**
     * <pre>
     * S X E
     *   # W
     * </pre>
     */
    @Test(expectedExceptions = TrackNotFoundException.class)
    public void testExceptionWaypointTurnout() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        int switchAddress = 50;
        int switchBit = 5;

        int waypointX = 4;
        int waypointY = 1;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(1, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
                createTurnout(2, 1, Turnout.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createCurve(2, 2, DIRECTION.TOP_RIGHT),
            createHorizontalBlockStraight(3, 1, endBlock),
                createHorizontalStraight(waypointX, waypointY)));

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);
        route.setWaypoints(Lists.newArrayList(new GridPosition(waypointX, waypointY)));

        getTrackBuilder().build(route);
    }

    /**
     * <pre>
     * S # X #
     *     # X #
     *       # E
     * </pre>
     */
    @Test
    public void testForwardDoubleTurnout() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        testDoubleTurnout(startAddress, startBit, endAddress, endBit);
    }

    /**
     * <pre>
     * E # X #
     *     # X #
     *       # S
     * </pre>
     */
    @Test
    public void testBackwardDoubleTurnout() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        testDoubleTurnout(endAddress, endBit, startAddress, startBit);
    }

    private void testDoubleTurnout(int startAddress, int startBit, int endAddress, int endBit)
            throws TrackNotFoundException {
        int switchAddress = 50;
        int switchBit = 5;
        int switchAddress2 = 51;
        int switchBit2 = 4;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(1, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
                createHorizontalStraight(2, 1),
                createTurnout(3, 1, Turnout.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createCurve(3, 2, DIRECTION.TOP_RIGHT),
                createHorizontalStraight(4, 1),
                createTurnout(4, 2, Turnout.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
                createCurve(4, 3, DIRECTION.TOP_RIGHT),

                createHorizontalStraight(5, 2),

            createHorizontalBlockStraight(5, 3, endBlock)));

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);

        Track track = getTrackBuilder().build(route);
        Assert.assertEquals(7, track.getLength());
        Assert.assertEquals(0, track.getTrackBlocks().size());
        Assert.assertEquals(2, track.getTrackFunctions().size());
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress, switchBit, true),
                track.getTrackFunctions().get(0));
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress2, switchBit2, true),
                track.getTrackFunctions().get(1));
    }

    /**
     * <pre>
     *
     * S # X # X E
     *     #   #
     *     # # #
     * </pre>
     */
    @Test
    public void testTwoWayShort() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;
        int switchAddress = 100;
        int switchBit = 5;

        int switchAddress2 = 56;
        int switchBit2 = 6;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(1, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
                createHorizontalStraight(2, 1),
                createTurnout(3, 1, Turnout.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createVerticalStraight(3, 2),
                createCurve(3, 3, DIRECTION.TOP_RIGHT),
                createHorizontalStraight(4, 1),
                createHorizontalStraight(4, 3),

                createTurnout(5, 1, Turnout.DIRECTION.LEFT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
                createVerticalStraight(5, 2),
                createCurve(5, 3, DIRECTION.TOP_LEFT),

            createHorizontalBlockStraight(6, 1, endBlock)));

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);

        Track track = getTrackBuilder().build(route);
        Assert.assertEquals(6, track.getLength());
        Assert.assertEquals(0, track.getTrackBlocks().size());
        Assert.assertEquals(2, track.getTrackFunctions().size());
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress, switchBit, false),
                track.getTrackFunctions().get(0));
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress2, switchBit2, false),
                track.getTrackFunctions().get(1));
    }

    /**
     * <pre>
     * E # #
     * # # X X S
     * # # # #
     * </pre>
     */
    @Test
    public void testTwoTurnoutInARow() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;
        int switchAddress = 100;
        int switchBit = 5;

        int switchAddress2 = 56;
        int switchBit2 = 6;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(5, 2,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, endBlock),
                createHorizontalStraight(2, 1),
                createCurve(3, 1, DIRECTION.BOTTOM_LEFT),

                createHorizontalStraight(1, 2),
                createHorizontalStraight(2, 2),
                createTurnout(3, 2, Turnout.DIRECTION.RIGHT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createTurnout(4, 2, Turnout.DIRECTION.LEFT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
            startBlockStraight,

                createHorizontalStraight(1, 3),
                createHorizontalStraight(2, 3),
                createHorizontalStraight(3, 3),
                createCurve(4, 3, DIRECTION.TOP_LEFT)));

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);

        Track track = getTrackBuilder().build(route);
        Assert.assertEquals(6, track.getLength());
        Assert.assertEquals(0, track.getTrackBlocks().size());
        Assert.assertEquals(2, track.getTrackFunctions().size());
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress2, switchBit2, false),
                track.getTrackFunctions().get(0));
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress, switchBit, true),
                track.getTrackFunctions().get(1));
    }

    /**
     * <pre>
     *     # # #
     *     #   #
     * S # X # X E
     * </pre>
     */
    @Test
    public void testTwoWayShort2() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;
        int switchAddress = 56;
        int switchBit = 6;

        int switchAddress2 = 100;
        int switchBit2 = 5;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(1, 3,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
                createHorizontalStraight(2, 3),
                createTurnout(3, 3, Turnout.DIRECTION.LEFT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createVerticalStraight(3, 2),
                createCurve(3, 1, DIRECTION.BOTTOM_RIGHT),
                createHorizontalStraight(4, 3),
                createHorizontalStraight(4, 1),
                createCurve(5, 1, DIRECTION.BOTTOM_LEFT),
                createVerticalStraight(5, 2),
                createTurnout(5, 3, Turnout.DIRECTION.RIGHT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),

            createHorizontalBlockStraight(6, 3, endBlock)));

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);

        Track track = getTrackBuilder().build(route);
        Assert.assertEquals(6, track.getLength());
        Assert.assertEquals(0, track.getTrackBlocks().size());
        Assert.assertEquals(2, track.getTrackFunctions().size());
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress, switchBit, false),
                track.getTrackFunctions().get(0));
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress2, switchBit2, false),
                track.getTrackFunctions().get(1));
    }

    /**
     * <pre>
     * S
     * X X
     *   # E
     * </pre>
     */
    @Test
    public void testTwoTurnoutVertical() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;
        int switchAddress = 56;
        int switchBit = 6;

        int switchAddress2 = 100;
        int switchBit2 = 5;

        BlockStraight startBlockStraight = createVerticalBlockStraight(1, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
                createTurnout(1, 2, Turnout.DIRECTION.LEFT, PRESENTATION.TOP_TO_BOTTOM, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createTurnout(2, 2, Turnout.DIRECTION.LEFT, PRESENTATION.BOTTOM_TO_TOP, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
                createCurve(2, 3, DIRECTION.TOP_RIGHT),
            createHorizontalBlockStraight(3, 3, endBlock)));

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);

        Track track = getTrackBuilder().build(route);
        Assert.assertEquals(5, track.getLength());
        Assert.assertEquals(0, track.getTrackBlocks().size());
        Assert.assertEquals(2, track.getTrackFunctions().size());
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress, switchBit, true),
                track.getTrackFunctions().get(0));
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress2, switchBit2, true),
                track.getTrackFunctions().get(1));
    }

    /**
     * <pre>
     *
     * S # X # X E
     *     # W #
     * </pre>
     */
    @Test
    public void testWaypointTwoWayLong() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;
        int switchAddress = 56;
        int switchBit = 6;

        int switchAddress2 = 100;
        int switchBit2 = 5;

        int waypointX = 4;
        int waypointY = 2;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(1, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
                createHorizontalStraight(2, 1),
                createTurnout(3, 1, Turnout.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createCurve(3, 2, DIRECTION.TOP_RIGHT),
                createHorizontalStraight(4, 1),
                createHorizontalStraight(waypointX, waypointY),
                createTurnout(5, 1, Turnout.DIRECTION.LEFT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
                createCurve(5, 2, DIRECTION.TOP_LEFT),
            createHorizontalBlockStraight(6, 1, endBlock)));

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);
        route.setWaypoints(Lists.newArrayList(new GridPosition(waypointX, waypointY)));

        Track track = getTrackBuilder().build(route);
        Assert.assertEquals(8, track.getLength());
        Assert.assertEquals(0, track.getTrackBlocks().size());
        Assert.assertEquals(2, track.getTrackFunctions().size());
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress, switchBit, true),
                track.getTrackFunctions().get(0));
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress2, switchBit2, true),
                track.getTrackFunctions().get(1));
    }

    /**
     * <pre>
     *
     * S # X W X E
     *     # # #
     * </pre>
     */
    @Test
    public void testWaypointTwoWayShort() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;
        int switchAddress = 56;
        int switchBit = 6;

        int switchAddress2 = 100;
        int switchBit2 = 5;

        int waypointX = 4;
        int waypointY = 1;

        BlockStraight startBlockStraight = createHorizontalBlockStraight(1, 1,
            createTrackBlock(startAddress, startBit));
        TrackBlock endBlock = createTrackBlock(endAddress, endBit);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
                createHorizontalStraight(2, 1),
                createTurnout(3, 1, Turnout.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createCurve(3, 2, DIRECTION.TOP_RIGHT),
                createHorizontalStraight(waypointX, waypointY),
                createHorizontalStraight(4, 2),
                createTurnout(5, 1, Turnout.DIRECTION.LEFT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
                createCurve(5, 2, DIRECTION.TOP_LEFT),
            createHorizontalBlockStraight(6, 1, endBlock)));

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);
        route.setWaypoints(Lists.newArrayList(new GridPosition(waypointX, waypointY)));

        Track track = getTrackBuilder().build(route);
        Assert.assertEquals(6, track.getLength());
        Assert.assertEquals(0, track.getTrackBlocks().size());
        Assert.assertEquals(2, track.getTrackFunctions().size());
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress, switchBit, true),
                track.getTrackFunctions().get(0));
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress2, switchBit2, true),
                track.getTrackFunctions().get(1));

    }

    /**
     * <pre>
     * # # #  E
     * S   #
     * # # #
     * </pre>
     */
    @Test(expectedExceptions = TrackNotFoundException.class)
    public void testEndless() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;

        BlockStraight startBlockStraight = createVerticalBlockStraight(1, 2,
            createTrackBlock(startAddress, startBit));

        mockTrack(Lists.newArrayList(
                createCurve(1, 1, DIRECTION.BOTTOM_RIGHT),
            startBlockStraight,
                createCurve(1, 3, DIRECTION.TOP_RIGHT),
                createHorizontalStraight(2, 1),
                createHorizontalStraight(2, 3),

                createCurve(3, 1, DIRECTION.BOTTOM_LEFT),
                createVerticalStraight(3, 2),
                createCurve(3, 3, DIRECTION.TOP_LEFT)));

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(createTrackBlock(100, 1, true));
        getTrackBuilder().build(route);
    }

}
