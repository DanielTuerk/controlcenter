package net.wbz.moba.controlcenter.web.client.model.track.block;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import net.wbz.moba.controlcenter.web.client.event.track.TrackBlockRemoteListener;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartBlockEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartBlockEvent.STATE;

/**
 * Remote listener for receiving the block state changes to update the {@link BlockPart}.
 *
 * @author Daniel Tuerk
 */
class BlockStateRemoteListener implements TrackBlockRemoteListener {

    private final BlockPart<BlockStraight> blockPart;

    BlockStateRemoteListener(BlockPart<BlockStraight> blockPart) {
        this.blockPart = blockPart;
    }

    @Override
    public void trackBlockStateChanged(TrackPartBlockEvent event) {
        BlockStraight blockStraight = blockPart.getTrackPart();
        handleEvent(event, blockStraight.getLeftTrackBlock(), blockStraight.getMiddleTrackBlock(),
            blockStraight.getRightTrackBlock());
    }

    private void handleEvent(TrackPartBlockEvent event, TrackBlock... trackBlocks) {
        Arrays.stream(trackBlocks).filter(Objects::nonNull).forEach(
            trackBlock -> Optional.ofNullable(trackBlock.getBlockFunction()).ifPresent(busDataConfiguration -> {
                if (event.getConfig().equals(busDataConfiguration)) {
                    if (event.getState() == STATE.USED) {
                        blockPart.usedBlock();
                    } else {
                        blockPart.freeBlock();
                    }
                }
            })
        );
    }

}
