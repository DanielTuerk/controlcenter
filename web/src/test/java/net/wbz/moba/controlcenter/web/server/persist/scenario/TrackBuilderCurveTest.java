package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.google.common.collect.Lists;
import java.util.List;
import net.wbz.moba.controlcenter.web.shared.scenario.TrackNotFoundException;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve.DIRECTION;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test {@link TrackBuilder} for {@link net.wbz.moba.controlcenter.web.shared.track.model.Curve}.
 *
 * @author Daniel Tuerk
 */
public class TrackBuilderCurveTest extends AbstractTrackBuilderTest {

    private TrackBlock endBlock;
    private TrackBlock startBlock;

    @BeforeMethod
    public void beforeMethod() {
        startBlock = createTrackBlock(20, 1);
        endBlock = createTrackBlock(30, 3);
    }

    /**
     * <pre>
     * S
     * # E
     * </pre>
     */
    @Test
    public void testCurve_TopRight() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(1, 1, startBlock);

        testSimpleCurve(Lists.newArrayList(
            startBlockStraight,
                createCurve(1, 2, Curve.DIRECTION.TOP_RIGHT),
            createHorizontalBlockStraight(2, 2, endBlock)),
            startBlockStraight, endBlock
        );
    }

    /**
     * <pre>
     *   S
     * E #
     * </pre>
     */
    @Test
    public void testCurve_TopLeft() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(2, 1, startBlock);

        testSimpleCurve(Lists.newArrayList(
            startBlockStraight,
                createCurve(2, 2, DIRECTION.TOP_LEFT),
            createHorizontalBlockStraight(1, 2, endBlock)), startBlockStraight, endBlock);
    }

    /**
     * <pre>
     * # E
     * S
     * </pre>
     */
    @Test
    public void testCurve_BottomRight() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(1, 2, startBlock);

        testSimpleCurve(Lists.newArrayList(
            startBlockStraight,
                createCurve(1, 1, DIRECTION.BOTTOM_RIGHT),
            createHorizontalBlockStraight(2, 1, endBlock)),
            startBlockStraight, endBlock);
    }

    /**
     * <pre>
     * E #
     *   S
     * </pre>
     */
    @Test
    public void testCurve_BottomLeft() throws TrackNotFoundException {
        BlockStraight startBlockStraight = createVerticalBlockStraight(2, 2, startBlock);

        testSimpleCurve(Lists.newArrayList(
            createHorizontalBlockStraight(1, 1, endBlock),
                createCurve(2, 1, DIRECTION.BOTTOM_LEFT),
            startBlockStraight),
            startBlockStraight, endBlock);
    }

    private void testSimpleCurve(List<? extends AbstractTrackPart> trackParts,
        BlockStraight startBlockStraight, TrackBlock endBlock) throws TrackNotFoundException {
        testSimpleTrack(trackParts, startBlockStraight, endBlock, 3);
    }

}
