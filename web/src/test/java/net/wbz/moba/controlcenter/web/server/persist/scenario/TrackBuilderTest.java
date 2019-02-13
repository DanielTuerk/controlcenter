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
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch.PRESENTATION;
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

        testSimpleTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, startAddress, startBit, true),
                createHorizontalStraight(2, 1),
            createHorizontalBlockStraight(3, 1, endAddress, endBit, true)), startAddress, startBit, endAddress, endBit,
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

        testSimpleTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, endAddress, endBit, true),
                createHorizontalStraight(2, 1),
            createHorizontalBlockStraight(3, 1, startAddress, startBit, true)), startAddress, startBit, endAddress,
                endBit, 3);
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

        testSimpleTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
                createHorizontalStraight(2, 1),
            createHorizontalBlockStraight(3, 1, startAddress, startBit, true),
                createCurve(4, 1, Curve.DIRECTION.BOTTOM_LEFT),
                createVerticalStraight(4, 2),
                createCurve(4, 3, Curve.DIRECTION.TOP_LEFT),
            createHorizontalBlockStraight(3, 3, endAddress, endBit, true),
                createHorizontalStraight(2, 3),
                createCurve(1, 3, Curve.DIRECTION.TOP_RIGHT),
            createVerticalStraight(1, 2)), startAddress, startBit, endAddress, endBit, 5);
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

        testSimpleTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
            createHorizontalBlockStraight(2, 1, startAddress, startBit, true),
                createHorizontalStraight(3, 1),
                createCurve(4, 1, Curve.DIRECTION.BOTTOM_LEFT),
                createVerticalStraight(4, 2),
                createCurve(4, 3, Curve.DIRECTION.TOP_LEFT),
                createHorizontalStraight(3, 3),
            createHorizontalBlockStraight(2, 3, endAddress, endBit, true),
                createCurve(1, 3, Curve.DIRECTION.TOP_RIGHT),
                createVerticalStraight(1, 2)), startAddress, startBit, endAddress, endBit, 5);
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

        mockTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
                createHorizontalStraight(2, 1),
            createHorizontalBlockStraight(3, 1, startAddress, startBit, true),
                createCurve(4, 1, Curve.DIRECTION.BOTTOM_LEFT),
            createVerticalBlockStraight(4, 2, 40, 4, true),
                createCurve(4, 3, Curve.DIRECTION.TOP_LEFT),
            createHorizontalBlockStraight(3, 3, endAddress, endBit, true),
            createHorizontalBlockStraight(2, 3, blockAddress, blockBit, true),
                createCurve(1, 3, Curve.DIRECTION.TOP_RIGHT),
                createVerticalStraight(waypointX, waypointY)));

        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));
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

        mockTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
                createHorizontalStraight(2, 1),
            createHorizontalBlockStraight(3, 1, endAddress, endBit, true),
                createCurve(4, 1, Curve.DIRECTION.BOTTOM_LEFT),
            createVerticalBlockStraight(4, 2, 40, 4, true),
                createCurve(4, 3, Curve.DIRECTION.TOP_LEFT),
            createHorizontalBlockStraight(3, 3, startAddress, startBit, true),
            createHorizontalBlockStraight(2, 3, blockAddress, blockBit, true),
                createCurve(1, 3, Curve.DIRECTION.TOP_RIGHT),
                createVerticalStraight(waypointX, waypointY)));

        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));
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
        mockTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
            createHorizontalBlockStraight(2, 1, startAddress, startBit, true),
                createHorizontalStraight(3, 1),
            createHorizontalBlockStraight(5, 1, endAddress, endBit, true)));
        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));
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
        mockTrack(Lists.newArrayList(
                createCurve(1, 1, Curve.DIRECTION.BOTTOM_RIGHT),
            createHorizontalBlockStraight(2, 1, startAddress, startBit, true),
            createHorizontalBlockStraight(4, 1, endAddress, endBit, true)));
        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));
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
        mockTrack(Lists.newArrayList(
                createHorizontalStraight(waypointX, waypointY),
                createHorizontalStraight(2, 1),
            createHorizontalBlockStraight(3, 1, startAddress, startBit, true),
                createHorizontalStraight(4, 1),
            createHorizontalBlockStraight(5, 1, endAddress, endBit, true)));
        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));
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
    public void testExceptionWaypointSwitch() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        int switchAddress = 50;
        int switchBit = 5;

        int waypointX = 4;
        int waypointY = 1;
        mockTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, startAddress, startBit, true),
                createSwitch(2, 1, Switch.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createCurve(2, 2, DIRECTION.TOP_RIGHT),
            createHorizontalBlockStraight(3, 1, endAddress, endBit, true),
                createHorizontalStraight(waypointX, waypointY)));

        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));
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
    public void testForwardDoubleSwitch() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        testDoubleSwitch(startAddress, startBit, endAddress, endBit);
    }

    /**
     * <pre>
     * E # X #
     *     # X #
     *       # S
     * </pre>
     */
    @Test
    public void testBackwardDoubleSwitch() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;

        testDoubleSwitch(endAddress, endBit, startAddress, startBit);
    }

    private void testDoubleSwitch(int startAddress, int startBit, int endAddress, int endBit)
            throws TrackNotFoundException {
        int switchAddress = 50;
        int switchBit = 5;
        int switchAddress2 = 51;
        int switchBit2 = 4;

        mockTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, startAddress, startBit, true),
                createHorizontalStraight(2, 1),
                createSwitch(3, 1, Switch.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createCurve(3, 2, DIRECTION.TOP_RIGHT),
                createHorizontalStraight(4, 1),
                createSwitch(4, 2, Switch.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
                createCurve(4, 3, DIRECTION.TOP_RIGHT),

                createHorizontalStraight(5, 2),

            createHorizontalBlockStraight(5, 3, endAddress, endBit, true)));

        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));

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

        mockTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, startAddress, startBit, true),
                createHorizontalStraight(2, 1),
                createSwitch(3, 1, Switch.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createVerticalStraight(3, 2),
                createCurve(3, 3, DIRECTION.TOP_RIGHT),
                createHorizontalStraight(4, 1),
                createHorizontalStraight(4, 3),

                createSwitch(5, 1, Switch.DIRECTION.LEFT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
                createVerticalStraight(5, 2),
                createCurve(5, 3, DIRECTION.TOP_LEFT),

            createHorizontalBlockStraight(6, 1, endAddress, endBit, true)));

        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));

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
    public void testTwoSwitchInARow() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;
        int switchAddress = 100;
        int switchBit = 5;

        int switchAddress2 = 56;
        int switchBit2 = 6;

        mockTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, endAddress, endBit, true),
                createHorizontalStraight(2, 1),
                createCurve(3, 1, DIRECTION.BOTTOM_LEFT),

                createHorizontalStraight(1, 2),
                createHorizontalStraight(2, 2),
                createSwitch(3, 2, Switch.DIRECTION.RIGHT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createSwitch(4, 2, Switch.DIRECTION.LEFT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
            createHorizontalBlockStraight(5, 2, startAddress, startBit, true),

                createHorizontalStraight(1, 3),
                createHorizontalStraight(2, 3),
                createHorizontalStraight(3, 3),
                createCurve(4, 3, DIRECTION.TOP_LEFT)));

        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));

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

        mockTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 3, startAddress, startBit, true),
                createHorizontalStraight(2, 3),
                createSwitch(3, 3, Switch.DIRECTION.LEFT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createVerticalStraight(3, 2),
                createCurve(3, 1, DIRECTION.BOTTOM_RIGHT),
                createHorizontalStraight(4, 3),
                createHorizontalStraight(4, 1),
                createCurve(5, 1, DIRECTION.BOTTOM_LEFT),
                createVerticalStraight(5, 2),
                createSwitch(5, 3, Switch.DIRECTION.RIGHT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),

            createHorizontalBlockStraight(6, 3, endAddress, endBit, true)));

        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));

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
    public void testTwoSwitchVertical() throws TrackNotFoundException {
        int startAddress = 20;
        int startBit = 1;
        int endAddress = 30;
        int endBit = 3;
        int switchAddress = 56;
        int switchBit = 6;

        int switchAddress2 = 100;
        int switchBit2 = 5;

        mockTrack(Lists.newArrayList(
            createVerticalBlockStraight(1, 1, startAddress, startBit, true),
                createSwitch(1, 2, Switch.DIRECTION.LEFT, PRESENTATION.TOP_TO_BOTTOM, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createSwitch(2, 2, Switch.DIRECTION.LEFT, PRESENTATION.BOTTOM_TO_TOP, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
                createCurve(2, 3, DIRECTION.TOP_RIGHT),
            createHorizontalBlockStraight(3, 3, endAddress, endBit, true)));

        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));

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
        mockTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, startAddress, startBit, true),
                createHorizontalStraight(2, 1),
                createSwitch(3, 1, Switch.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createCurve(3, 2, DIRECTION.TOP_RIGHT),
                createHorizontalStraight(4, 1),
                createHorizontalStraight(waypointX, waypointY),
                createSwitch(5, 1, Switch.DIRECTION.LEFT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
                createCurve(5, 2, DIRECTION.TOP_LEFT),
            createHorizontalBlockStraight(6, 1, endAddress, endBit, true)));

        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));
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
        mockTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, startAddress, startBit, true),
                createHorizontalStraight(2, 1),
                createSwitch(3, 1, Switch.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                        switchAddress, switchBit, true)),
                createCurve(3, 2, DIRECTION.TOP_RIGHT),
                createHorizontalStraight(waypointX, waypointY),
                createHorizontalStraight(4, 2),
                createSwitch(5, 1, Switch.DIRECTION.LEFT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                        switchAddress2, switchBit2, true)),
                createCurve(5, 2, DIRECTION.TOP_LEFT),
            createHorizontalBlockStraight(6, 1, endAddress, endBit, true)));

        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));
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
        mockTrack(Lists.newArrayList(
                createCurve(1, 1, DIRECTION.BOTTOM_RIGHT),
            createVerticalBlockStraight(1, 2, startAddress, startBit, true),
                createCurve(1, 3, DIRECTION.TOP_RIGHT),
                createHorizontalStraight(2, 1),
                createHorizontalStraight(2, 3),

                createCurve(3, 1, DIRECTION.BOTTOM_LEFT),
                createVerticalStraight(3, 2),
                createCurve(3, 3, DIRECTION.TOP_LEFT)));

        Route route = mockRoute();
        route.setStart(createBlockStraight(startAddress, startBit, true));
        route.setEnd(createTrackBlock(100, 1, true));
        getTrackBuilder().build(route);
    }

}
