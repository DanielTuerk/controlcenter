package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.google.common.collect.Lists;
import net.wbz.moba.controlcenter.web.shared.scenario.TrackNotFoundException;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve.DIRECTION;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch.PRESENTATION;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test {@link TrackBuilder} for {@link Switch}.
 *
 * @author Daniel Tuerk
 */
public class TrackBuilderSwitchTest extends AbstractTrackBuilderTest {

    private int switchAddress;
    private int switchBit;
    private TrackBlock endBlock;
    private TrackBlock startBlock;

    @BeforeMethod
    public void beforeMethod() {
        startBlock = createTrackBlock(20, 1);
        endBlock = createTrackBlock(30, 3);

        switchAddress = 100;
        switchBit = 5;
    }

    /**
     * <pre>
     *     # #
     * S # X E
     * </pre>
     */
    @Test
    public void testSwitch_LeftToRight_Left_Straight() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createHorizontalBlockStraight(1, 1, startBlock);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
            createHorizontalStraight(2, 1),
            createSwitch(3, 1, Switch.DIRECTION.LEFT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                switchAddress, switchBit, false)),
            createCurve(3, 0, DIRECTION.BOTTOM_RIGHT),
            createHorizontalBlockStraight(4, 1, endBlock),
            createHorizontalStraight(4, 0)));

        testSwitch(false, 4, startBlockStraight);
    }

    /**
     * <pre>
     *     # E
     * S # X #
     * </pre>
     */
    @Test
    public void testSwitch_LeftToRight_Left_Branch() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createHorizontalBlockStraight(1, 1, startBlock);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
            createHorizontalStraight(2, 1),
            createSwitch(3, 1, Switch.DIRECTION.LEFT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                switchAddress, switchBit, false)),
            createCurve(3, 0, Curve.DIRECTION.BOTTOM_RIGHT),
            createHorizontalStraight(4, 1),
            createHorizontalBlockStraight(4, 0, endBlock)));

        testSwitch(true, 5, startBlockStraight);
    }

    /**
     * <pre>
     * E # X S
     *     # #
     * </pre>
     */
    @Test
    public void testSwitch_LeftToRight_Right_Straight() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createHorizontalBlockStraight(4, 1, startBlock);

        mockTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, endBlock),
            createHorizontalStraight(2, 1),
            createSwitch(3, 1, Switch.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createCurve(3, 2, Curve.DIRECTION.TOP_RIGHT),
            startBlockStraight,
            createHorizontalStraight(4, 2)));

        testSwitch(false, 4, startBlockStraight);
    }

    /**
     * <pre>
     * S # X # #
     *     # E
     * </pre>
     */
    @Test
    public void testSwitch_LeftToRight_Right_Branch() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createHorizontalBlockStraight(1, 1, startBlock);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
            createHorizontalStraight(2, 1),
            createSwitch(3, 1, Switch.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                switchAddress, switchBit, false)),
            createCurve(3, 2, Curve.DIRECTION.TOP_RIGHT),
            createHorizontalStraight(4, 1),
            createHorizontalStraight(5, 1),
            createHorizontalBlockStraight(4, 2, endBlock)));

        testSwitch(true, 5, startBlockStraight);
    }

    /**
     * <pre>
     * # #
     * E X # S
     * </pre>
     */
    @Test
    public void testSwitch_RightToLeft_Right_Straight() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createHorizontalBlockStraight(4, 2, startBlock);

        mockTrack(Lists.newArrayList(
            createHorizontalStraight(1, 1),
            createCurve(2, 1, DIRECTION.BOTTOM_LEFT),
            createHorizontalBlockStraight(1, 2, endBlock),
            createSwitch(2, 2, Switch.DIRECTION.RIGHT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createHorizontalStraight(3, 2),
            startBlockStraight));

        testSwitch(false, 4, startBlockStraight);
    }

    /**
     * <pre>
     * E #
     * # X # S
     * </pre>
     */
    @Test
    public void testSwitch_RightToLeft_Right_Branch() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createHorizontalBlockStraight(4, 2, startBlock);

        mockTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, endBlock),
            createCurve(2, 1, DIRECTION.BOTTOM_LEFT),
            createHorizontalStraight(1, 2),
            createSwitch(2, 2, Switch.DIRECTION.RIGHT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createHorizontalStraight(3, 2),
            startBlockStraight));

        testSwitch(true, 5, startBlockStraight);
    }

    /**
     * <pre>
     * E X # S
     * # #
     * </pre>
     */
    @Test
    public void testSwitch_RightToLeft_Left_Straight() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createHorizontalBlockStraight(4, 1, startBlock);

        mockTrack(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, endBlock),
            createSwitch(2, 1, Switch.DIRECTION.LEFT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createHorizontalStraight(3, 1),
            startBlockStraight,
            createHorizontalStraight(1, 2),
            createCurve(2, 2, DIRECTION.TOP_LEFT)));

        testSwitch(false, 4, startBlockStraight);
    }

    /**
     * <pre>
     * # X # S
     * E #
     * </pre>
     */
    @Test
    public void testSwitch_RightToLeft_Left_Branch() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createHorizontalBlockStraight(4, 1, startBlock);

        mockTrack(Lists.newArrayList(
            createHorizontalStraight(1, 1),
            createSwitch(2, 1, Switch.DIRECTION.LEFT, PRESENTATION.RIGHT_TO_LEFT, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createHorizontalStraight(3, 1),
            startBlockStraight,
            createHorizontalBlockStraight(1, 2, endBlock),
            createCurve(2, 2, DIRECTION.TOP_LEFT)));

        testSwitch(true, 5, startBlockStraight);
    }

    /**
     * <pre>
     *   S
     * # X
     * # #
     *   E
     * </pre>
     */
    @Test
    public void testSwitch_TopToBottom_Right_Straight() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(2, 1, startBlock);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
            createCurve(1, 2, DIRECTION.BOTTOM_RIGHT),
            createSwitch(2, 2, Switch.DIRECTION.RIGHT, PRESENTATION.TOP_TO_BOTTOM, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createVerticalStraight(1, 3),
            createVerticalStraight(2, 3),
            createVerticalBlockStraight(2, 4, endBlock)));

        testSwitch(false, 4, startBlockStraight);
    }

    /**
     * <pre>
     *   S
     * # X
     * # #
     * E
     * </pre>
     */
    @Test
    public void testSwitch_TopToBottom_Right_Branch() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(2, 1, startBlock);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
            createCurve(1, 2, DIRECTION.BOTTOM_RIGHT),
            createSwitch(2, 2, Switch.DIRECTION.RIGHT, PRESENTATION.TOP_TO_BOTTOM, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createVerticalStraight(1, 3),
            createVerticalStraight(2, 3),
            createVerticalBlockStraight(1, 4, endBlock)));

        testSwitch(true, 5, startBlockStraight);
    }

    /**
     * <pre>
     * S
     * X #
     * # #
     * E
     * </pre>
     */
    @Test
    public void testSwitch_TopToBottom_Left_Straight() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(1, 1, startBlock);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
            createSwitch(1, 2, Switch.DIRECTION.LEFT, PRESENTATION.TOP_TO_BOTTOM, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createCurve(2, 2, DIRECTION.BOTTOM_LEFT),
            createVerticalStraight(1, 3),
            createVerticalStraight(2, 3),
            createVerticalBlockStraight(1, 4, endBlock)));

        testSwitch(false, 4, startBlockStraight);
    }

    /**
     * <pre>
     * S
     * X #
     * # #
     *   E
     * </pre>
     */
    @Test
    public void testSwitch_TopToBottom_Left_Branch() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(1, 1, startBlock);

        mockTrack(Lists.newArrayList(
            startBlockStraight,
            createSwitch(1, 2, Switch.DIRECTION.LEFT, PRESENTATION.TOP_TO_BOTTOM, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createCurve(2, 2, DIRECTION.BOTTOM_LEFT),
            createVerticalStraight(1, 3),
            createVerticalStraight(2, 3),
            createVerticalBlockStraight(2, 4, endBlock)));

        testSwitch(true, 5, startBlockStraight);
    }

    /**
     * <pre>
     * E #
     * X #
     * #
     * S
     * </pre>
     */
    @Test
    public void testSwitch_BottomToTop_Right_Straight() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(1, 4, startBlock);

        mockTrack(Lists.newArrayList(
            createVerticalBlockStraight(1, 1, endBlock),
            createVerticalStraight(2, 1),
            createSwitch(1, 2, Switch.DIRECTION.RIGHT, PRESENTATION.BOTTOM_TO_TOP, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createCurve(2, 2, DIRECTION.TOP_LEFT),
            createVerticalStraight(1, 3),
            startBlockStraight));

        testSwitch(false, 4, startBlockStraight);
    }

    /**
     * <pre>
     * # E
     * X #
     * #
     * S
     * </pre>
     */
    @Test
    public void testSwitch_BottomToTop_Right_Branch() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(1, 4, startBlock);

        mockTrack(Lists.newArrayList(
            createVerticalStraight(1, 1),
            createVerticalBlockStraight(2, 1, endBlock),
            createSwitch(1, 2, Switch.DIRECTION.RIGHT, PRESENTATION.BOTTOM_TO_TOP, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createCurve(2, 2, DIRECTION.TOP_LEFT),
            createVerticalStraight(1, 3),
            startBlockStraight));

        testSwitch(true, 5, startBlockStraight);
    }

    /**
     * <pre>
     * -> X E
     *    S
     * </pre>
     *
     * Switch to right, no drive from bottom.
     */
    @Test(expectedExceptions = TrackNotFoundException.class)
    public void testSwitch_LeftToRight_Exception() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(1, 4, startBlock);

        mockTrack(Lists.newArrayList(
            createVerticalStraight(1, 1),
            createVerticalBlockStraight(2, 1, endBlock),
            createSwitch(1, 2, Switch.DIRECTION.RIGHT, PRESENTATION.LEFT_TO_RIGHT, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createCurve(2, 2, DIRECTION.TOP_LEFT),
            createVerticalStraight(1, 3),
            startBlockStraight));

        testSwitch(true, -1, startBlockStraight);
    }

    /**
     * <pre>
     * # E
     * # X
     *   #
     *   S
     * </pre>
     */
    @Test
    public void testSwitch_BottomToTop_Left_Straight() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(2, 4, startBlock);

        mockTrack(Lists.newArrayList(
            createVerticalStraight(1, 1),
            createVerticalBlockStraight(2, 1, endBlock),
            createCurve(1, 2, DIRECTION.TOP_RIGHT),
            createSwitch(2, 2, Switch.DIRECTION.LEFT, PRESENTATION.BOTTOM_TO_TOP, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createVerticalStraight(2, 3),
            startBlockStraight));

        testSwitch(false, 4, startBlockStraight);
    }

    /**
     * <pre>
     * E #
     * # X
     *   #
     *   S
     * </pre>
     */
    @Test
    public void testSwitch_BottomToTop_Left_Branch() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(2, 4, startBlock);

        mockTrack(Lists.newArrayList(
            createVerticalBlockStraight(1, 1, endBlock),
            createVerticalStraight(2, 1),
            createCurve(1, 2, DIRECTION.TOP_RIGHT),
            createSwitch(2, 2, Switch.DIRECTION.LEFT, PRESENTATION.BOTTOM_TO_TOP, new BusDataConfiguration(1,
                switchAddress, switchBit, true)),
            createVerticalStraight(2, 3),
            startBlockStraight));

        testSwitch(true, 5, startBlockStraight);
    }

    private void testSwitch(boolean switchTargetBitState, int expectedTrackLength,
        BlockStraight startBlockStraight) throws TrackNotFoundException {
        testSwitch(switchAddress, switchBit, switchTargetBitState,
            expectedTrackLength, startBlockStraight, endBlock);
    }

}
