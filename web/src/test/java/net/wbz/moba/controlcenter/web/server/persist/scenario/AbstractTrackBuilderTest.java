package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.Collection;
import java.util.List;

import net.wbz.moba.controlcenter.web.shared.scenario.Track;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import junit.framework.Assert;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder.TrackNotFoundException;
import net.wbz.moba.controlcenter.web.server.web.editor.TrackManager;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight.DIRECTION;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch.PRESENTATION;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;

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
@Guice(modules = AbstractTrackBuilderTest.TrackBuilderTestModule.class)
abstract class AbstractTrackBuilderTest {

    @Inject
    private TrackBuilder trackBuilder;
    @Inject
    private TrackManager trackManager;

    @BeforeMethod
    public void beforeMethod() {
        Mockito.reset(trackManager);
    }

    protected Route mockRoute() {
        Route mock = spy(Route.class);
        when(mock.getId()).thenReturn(1L);
        return mock;
    }

    void testSimpleTrack(List<? extends AbstractTrackPart> trackParts, int startAddress, int startBit,
            int endAddress, int endBit, int expectedLength)
            throws TrackNotFoundException {
        mockTrack(trackParts);

        Route route = mockRoute();
        route.setStart(createTrackBlock(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));

        Track track = trackBuilder.build(route);
        Assert.assertEquals(expectedLength, track.getLength());
        Assert.assertEquals(0, track.getTrackBlocks().size());
        Assert.assertEquals(0, track.getTrackFunctions().size());
    }

    void testSwitch(int startAddress, int startBit, int endAddress, int endBit, int switchAddress,
            int switchBit, boolean switchTargetBitState, int expectedTrackLength) throws TrackNotFoundException {
        Route route = mockRoute();
        route.setStart(createTrackBlock(startAddress, startBit, true));
        route.setEnd(createTrackBlock(endAddress, endBit, true));

        Track track = trackBuilder.build(route);
        Assert.assertEquals(expectedTrackLength, track.getLength());
        Assert.assertEquals(0, track.getTrackBlocks().size());
        Assert.assertEquals(1, track.getTrackFunctions().size());
        Assert.assertEquals(new BusDataConfiguration(1, switchAddress, switchBit, switchTargetBitState),
                track.getTrackFunctions().get(0));
    }

    Straight createHorizontalStraight(int x, int y) {
        return createStraight(x, y, DIRECTION.HORIZONTAL);
    }

    Straight createHorizontalStraight(int x, int y, int address, int bit, boolean bitState) {
        Straight straight = createHorizontalStraight(x, y);
        initTrackPart(x, y, address, bit, bitState, straight);
        return straight;
    }

    Straight createVerticalStraight(int x, int y, int address, int bit, boolean bitState) {
        Straight straight = createVerticalStraight(x, y);
        initTrackPart(x, y, address, bit, bitState, straight);
        return straight;
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

    Switch createSwitch(int x, int y, Switch.DIRECTION direction, PRESENTATION presentation,
            BusDataConfiguration toggleFunction) {
        Switch aSwitch = new Switch();
        aSwitch.setCurrentPresentation(presentation);
        aSwitch.setCurrentDirection(direction);
        aSwitch.setGridPosition(new GridPosition(x, y));
        aSwitch.setToggleFunction(toggleFunction);
        return aSwitch;
    }

    Curve createCurve(int x, int y, Curve.DIRECTION direction) {
        Curve curve = new Curve();
        curve.setDirection(direction);
        curve.setGridPosition(new GridPosition(x, y));
        return curve;
    }

    void initTrackPart(int x, int y, int address, int bit, boolean bitState, AbstractTrackPart trackPart) {
        trackPart.setTrackBlock(createTrackBlock(address, bit, bitState));
    }

    TrackBlock createTrackBlock(int address, int bit, boolean bitState) {
        TrackBlock trackBlock = new TrackBlock();
        trackBlock.setBlockFunction(new BusDataConfiguration(1, address, bit, bitState));
        return trackBlock;
    }

    void mockTrack(List<? extends AbstractTrackPart> trackParts) {
        when(trackManager.getTrack()).thenReturn((Collection<AbstractTrackPart>) trackParts);
    }

    TrackBuilder getTrackBuilder() {
        return trackBuilder;
    }

    public static class TrackBuilderTestModule extends AbstractModule {

        @Override
        protected void configure() {
            TrackManager mock = mock(TrackManager.class);
            bind(TrackManager.class).toInstance(mock);
        }
    }
}
