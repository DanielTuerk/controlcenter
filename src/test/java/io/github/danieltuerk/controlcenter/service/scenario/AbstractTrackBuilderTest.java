package io.github.danieltuerk.controlcenter.service.scenario;

import io.github.danieltuerk.controlcenter.service.track.TrackProvider;
import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import io.github.danieltuerk.controlcenter.shared.scenario.Track;
import io.github.danieltuerk.controlcenter.shared.scenario.TrackNotFoundException;
import io.github.danieltuerk.controlcenter.shared.track.model.*;
import io.github.danieltuerk.controlcenter.shared.track.model.Straight.DIRECTION;
import io.github.danieltuerk.controlcenter.shared.track.model.Turnout.PRESENTATION;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

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
        when(mock.getName()).thenReturn("mock-route");
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
        assertEquals(0, track.trackBlocks().size());
        assertEquals(0, track.trackFunctions().size());
    }

    void testTurnout(int switchAddress,
        int switchBit, boolean switchTargetBitState, int expectedTrackLength,
        BlockStraight startBlockStraight, TrackBlock endBlock) throws TrackNotFoundException {
        Route route = mockRoute();
        route.setStart(startBlockStraight);
        route.setEnd(endBlock);

        Track track = trackBuilder.build(route);
        assertEquals(expectedTrackLength, track.getLength());
        assertEquals(0, track.trackBlocks().size());
        assertEquals(1, track.trackFunctions().size());
        assertEquals(new BusDataConfiguration(1, switchAddress, switchBit, switchTargetBitState),
            track.trackFunctions().getFirst());
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
        straight.setId(System.nanoTime());
        straight.setDirection(direction);
        straight.setGridPosition(new GridPosition(x, y));
        return straight;
    }

    BlockStraight createBlockStraight(int x, int y, DIRECTION direction,
        int blockLength, TrackBlock trackBlock) {
        BlockStraight straight = new BlockStraight();
        straight.setId(System.nanoTime());
        straight.setDirection(direction);
        straight.setGridPosition(new GridPosition(x, y));
        straight.setMiddleTrackBlock(trackBlock);
        straight.setBlockLength(blockLength);
        return straight;
    }

    Turnout createTurnout(int x, int y, Turnout.DIRECTION direction, PRESENTATION presentation,
            BusDataConfiguration toggleFunction) {
        Turnout aTurnout = new Turnout();
        aTurnout.setId(System.nanoTime());
        aTurnout.setCurrentPresentation(presentation);
        aTurnout.setCurrentDirection(direction);
        aTurnout.setGridPosition(new GridPosition(x, y));
        aTurnout.setToggleFunction(toggleFunction);
        return aTurnout;
    }

    Curve createCurve(int x, int y, Curve.DIRECTION direction) {
        Curve curve = new Curve();
        curve.setId(System.nanoTime());
        curve.setDirection(direction);
        curve.setGridPosition(new GridPosition(x, y));
        return curve;
    }

    TrackBlock createTrackBlock(int address, int bit) {
        return createTrackBlock(address, bit, true);
    }

    TrackBlock createTrackBlock(int address, int bit, boolean bitState) {
        TrackBlock trackBlock = new TrackBlock();
        trackBlock.setId(System.nanoTime());
        trackBlock.setBlockFunction(new BusDataConfiguration(1, address, bit, bitState));
        return trackBlock;
    }

    BlockStraight createBlockStraight(int startAddress, int startBit, boolean state) {
        BlockStraight blockStraight = new BlockStraight();
        blockStraight.setId(System.nanoTime());
        blockStraight.setMiddleTrackBlock(createTrackBlock(startAddress, startBit, state));
        return blockStraight;
    }

    void mockTrack(List<? extends AbstractTrackPart> trackParts) {
        when(trackProvider.getTrack()).thenReturn(new ArrayList<>(trackParts));
    }

    TrackBuilder getTrackBuilder() {
        return trackBuilder;
    }

}
