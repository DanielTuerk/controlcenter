package net.wbz.moba.controlcenter.service.scenario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import java.util.List;
import java.util.regex.Pattern;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.Track;
import net.wbz.moba.controlcenter.shared.scenario.TrackNotFoundException;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.Curve;
import net.wbz.moba.controlcenter.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.shared.track.model.Straight;
import net.wbz.moba.controlcenter.shared.track.model.Straight.DIRECTION;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.shared.track.model.Turnout;
import net.wbz.moba.controlcenter.shared.track.model.Turnout.PRESENTATION;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

/**
 * Test the {@link TrackBuilder} with different track layouts.
 * S - start
 * E - end
 * W - waypoint
 * X - switch
 * # track part with {@link GridPosition}
 *
 * @author Daniel Tuerk
 */

abstract class AbstractTrackBuilderTest {

    @Inject
    TrackBuilder trackBuilder;
    @InjectMock
    TrackProvider trackProvider;

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(trackProvider);
        trackBuilder.setTimeoutEnabled(false);
    }

    protected void matches(String regex, String message) {
        assertTrue(Pattern.compile(regex).matcher(message).matches(), "message did not match regex");
    }

    protected Route mockRoute() {
        Route mock = spy(Route.class);
        when(mock.getId()).thenReturn(1L);
        return mock;
    }

    void testSimpleTrack(List<? extends AbstractTrackPart> trackParts, BlockStraight startBlockStraight,
        TrackBlock endBlock,
        int expectedLength)
            throws TrackNotFoundException {
        mockTrack(trackParts);

        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);

        Track track = trackBuilder.build(route);
        assertEquals(expectedLength, track.getLength());
        assertEquals(0, track.getTrackBlocks().size());
        assertEquals(0, track.getTrackFunctions().size());
    }

    void testTurnout(int switchAddress,
        int switchBit, boolean switchTargetBitState, int expectedTrackLength,
        BlockStraight startBlockStraight, TrackBlock endBlock) throws TrackNotFoundException {
        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);

        Track track = trackBuilder.build(route);
        assertEquals(expectedTrackLength, track.getLength());
        assertEquals(0, track.getTrackBlocks().size());
        assertEquals(1, track.getTrackFunctions().size());
        assertEquals(new BusDataConfiguration(1, switchAddress, switchBit, switchTargetBitState),
            track.getTrackFunctions().getFirst());
    }

    Straight createHorizontalStraight(int x, int y) {
        return createStraight(x, y, DIRECTION.HORIZONTAL);
    }

    BlockStraight createHorizontalBlockStraight(int x, int y, TrackBlock trackBlock) {
        return createHorizontalBlockStraight(x, y, 0, trackBlock);
    }

    BlockStraight createHorizontalBlockStraight(int x, int y, int blockLength, TrackBlock trackBlock) {
        return createBlockStraight(x, y, DIRECTION.HORIZONTAL, blockLength, trackBlock);
    }

    BlockStraight createVerticalBlockStraight(int x, int y, TrackBlock trackBlock) {
        return createVerticalBlockStraight(x, y, 0, trackBlock);
    }

    BlockStraight createVerticalBlockStraight(int x, int y, int blockLength, TrackBlock trackBlock) {
        return createBlockStraight(x, y, DIRECTION.VERTICAL, blockLength, trackBlock);
    }

    Straight createVerticalStraight(int x, int y) {
        return createStraight(x, y, DIRECTION.VERTICAL);
    }

    Straight createStraight(int x, int y, DIRECTION direction) {
        Straight straight = new Straight();
        straight.setDirection(direction);
        straight.setGridPosition(new GridPosition(x, y));
        return straight;
    }

    BlockStraight createBlockStraight(int x, int y, DIRECTION direction,
        int blockLength, TrackBlock trackBlock) {
        BlockStraight straight = new BlockStraight();
        straight.setDirection(direction);
        straight.setGridPosition(new GridPosition(x, y));
        straight.setMiddleTrackBlock(trackBlock);
        straight.setBlockLength(blockLength);
        return straight;
    }

    Turnout createTurnout(int x, int y, Turnout.DIRECTION direction, PRESENTATION presentation,
            BusDataConfiguration toggleFunction) {
        Turnout aTurnout = new Turnout();
        aTurnout.setCurrentPresentation(presentation);
        aTurnout.setCurrentDirection(direction);
        aTurnout.setGridPosition(new GridPosition(x, y));
        aTurnout.setToggleFunction(toggleFunction);
        return aTurnout;
    }

    Curve createCurve(int x, int y, Curve.DIRECTION direction) {
        Curve curve = new Curve();
        curve.setDirection(direction);
        curve.setGridPosition(new GridPosition(x, y));
        return curve;
    }

    TrackBlock createTrackBlock(int address, int bit) {
        TrackBlock trackBlock = new TrackBlock();
        trackBlock.setBlockFunction(new BusDataConfiguration(1, address, bit, true));
        return trackBlock;
    }

    TrackBlock createTrackBlock(int address, int bit, boolean bitState) {
        TrackBlock trackBlock = new TrackBlock();
        trackBlock.setBlockFunction(new BusDataConfiguration(1, address, bit, bitState));
        return trackBlock;
    }

    BlockStraight createBlockStraight(int startAddress, int startBit, boolean state) {
        BlockStraight blockStraight = new BlockStraight();
        blockStraight.setMiddleTrackBlock(createTrackBlock(startAddress, startBit, state));
        return blockStraight;
    }

    void mockTrack(List<? extends AbstractTrackPart> trackParts) {
        when(trackProvider.getTrack()).thenReturn(Lists.newArrayList(trackParts));
    }

    TrackBuilder getTrackBuilder() {
        return trackBuilder;
    }

}
