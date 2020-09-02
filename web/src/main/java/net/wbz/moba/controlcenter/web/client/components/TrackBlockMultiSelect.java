package net.wbz.moba.controlcenter.web.client.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import org.gwtbootstrap3.extras.select.client.ui.MultipleSelect;
import org.gwtbootstrap3.extras.select.client.ui.Option;

/**
 * {@link MultipleSelect} for {@link TrackBlock}s.
 *
 * @author Daniel Tuerk
 */
public class TrackBlockMultiSelect extends MultipleSelect {

    private Collection<TrackBlock> trackBlocks;
    private volatile List<String> lastSelectedItems;

    @Override
    protected void onLoad() {
        super.onLoad();
        loadTrackBlocks();
    }

    private void loadTrackBlocks() {
        clear();

        RequestUtils.getInstance().getTrackEditorService()
            .loadTrackBlocks(new OnlySuccessAsyncCallback<Collection<TrackBlock>>() {
                @Override
                public void onSuccess(Collection<TrackBlock> trackBlocks) {
                    TrackBlockMultiSelect.this.trackBlocks = trackBlocks;
                    trackBlocks.stream()
                        .sorted(Comparator.comparing(TrackBlock::getName))
                        .forEach(trackBlock -> {
                            Option option = new Option();
                            String value = getValueFromTrackBlock(trackBlock);
                            option.setValue(value);
                            option.setText(trackBlock.getDisplayValue());
                            add(option);
                        });
                    if (lastSelectedItems != null) {
                        setSelectedValue(lastSelectedItems);
                    }
                    refresh();
                }
            });
    }

    private String getValueFromTrackBlock(TrackBlock trackBlock) {
        return String.valueOf(trackBlock.getId());
    }

    private Optional<TrackBlock> getTrackBlockFromValue(String value) {
        return trackBlocks.stream().filter(x -> value.equals(String.valueOf(x.getId()))).findFirst();
    }

    public List<TrackBlock> getSelected() {
        List<Option> selectedItems = getSelectedItems();
        if (selectedItems == null) {
            return new ArrayList<>();
        }
        return selectedItems.stream()
            .map(x -> getTrackBlockFromValue(x.getValue()).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public void setSelected(List<TrackBlock> trackBlock) {
        if (trackBlock != null && !trackBlock.isEmpty()) {
            this.setSelectedValue(trackBlock.stream().map(this::getValueFromTrackBlock).collect(Collectors.toList()));
        }
    }

    @Override
    protected void setSelectedValue(List<String> value) {
        super.setSelectedValue(value);
        lastSelectedItems = value;
    }
}
