package net.wbz.moba.controlcenter.web.server.web.editor;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.Lists;
import java.util.List;
import net.wbz.moba.controlcenter.web.shared.editor.ValidationException;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight.DIRECTION;
import org.junit.Assert;
import org.testng.annotations.Test;

/**
 * Check for same {@link GridPosition} in {@link AbstractTrackPart}s.
 *
 * @author Daniel Tuerk
 */
public class GridPosValidatorTest {

    private final GridPosValidator underTest = new GridPosValidator();

    @Test
    public void testValidateSingleGridPos() throws ValidationException {
        List<AbstractTrackPart> trackParts = Lists
            .newArrayList(createStraight(1, 1), createStraight(2, 1), createStraight(1, 2));
        underTest.validate(trackParts);
    }

    @Test
    public void testValidateMultipleGridPos() throws ValidationException {
        List<AbstractTrackPart> trackParts = Lists
            .newArrayList(createBlockStraight(1, 1, DIRECTION.HORIZONTAL, 10), createStraight(11, 1));
        underTest.validate(trackParts);
    }

    @Test
    public void testValidateFailSingleGridPos() {
        Straight straightA = createStraight(4, 4);
        Straight straightB = createStraight(4, 4);
        List<AbstractTrackPart> trackParts = Lists
            .newArrayList(createStraight(5, 5), straightA, straightB);
        boolean errorThrown = false;
        try {
            underTest.validate(trackParts);
        } catch (ValidationException e) {
            errorThrown = true;
            assertEquals(e.getErrors().size(), 2);
            assertTrue(e.getErrors().containsKey(straightA));
            assertTrue(e.getErrors().containsKey(straightB));
        }
        assertTrue(errorThrown);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateFailMultipleGridPos() throws ValidationException {
        List<AbstractTrackPart> trackParts = Lists
            .newArrayList(createBlockStraight(1, 1, DIRECTION.HORIZONTAL, 10), createStraight(10, 1));
        underTest.validate(trackParts);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateFailMultipleGridPosCrossed() throws ValidationException {
        List<AbstractTrackPart> trackParts = Lists
            .newArrayList(createBlockStraight(2, 4, DIRECTION.HORIZONTAL, 6),
                createBlockStraight(3, 2, DIRECTION.VERTICAL, 6));
        underTest.validate(trackParts);
    }

    @Test
    public void testValidateNewSingleGridPos() throws ValidationException {
        List<AbstractTrackPart> trackParts = Lists
            .newArrayList(createStraightNullId(1, 1), createStraight(2, 1), createStraightNullId(1, 2));
        underTest.validate(trackParts);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateFailNewSingleGridPos() throws ValidationException {
        List<AbstractTrackPart> trackParts = Lists
            .newArrayList(createStraightNullId(1, 1), createStraight(2, 1), createStraightNullId(1, 1));
        underTest.validate(trackParts);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateFailNewSingleGridPos2() throws ValidationException {
        List<AbstractTrackPart> trackParts = Lists
            .newArrayList(createStraightNullId(1, 1), createStraight(1, 1), createStraightNullId(2, 1));
        underTest.validate(trackParts);
    }

    private Straight createStraightNullId(int x, int y) {
        return createTrackPart(Straight.class, x, y, null);
    }

    private Straight createStraight(int x, int y) {
        return createTrackPart(Straight.class, x, y);
    }

    private Straight createBlockStraight(int x, int y, DIRECTION direction, int length) {
        BlockStraight blockStraight = createTrackPart(BlockStraight.class, x, y);
        blockStraight.setDirection(direction);
        blockStraight.setBlockLength(length);
        return blockStraight;
    }

    private <T extends AbstractTrackPart> T createTrackPart(Class<T> clazz, int x, int y) {
        return createTrackPart(clazz, x, y, System.nanoTime());
    }

    private <T extends AbstractTrackPart> T createTrackPart(Class<T> clazz, int x, int y, Long id) {
        T instance = null;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Assert.fail(e.getMessage());
        }
        instance.setId(id);
        instance.setGridPosition(new GridPosition(x, y));
        return instance;
    }

}
