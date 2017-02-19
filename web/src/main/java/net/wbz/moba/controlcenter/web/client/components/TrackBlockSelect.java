package net.wbz.moba.controlcenter.web.client.components;

import java.util.Collection;
import java.util.Map;

import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * TODO doc
 * 
 * @author Daniel Tuerk
 */
public class TrackBlockSelect extends Select {

    private final Map<String, TrackBlock> trackBlockSelectOptions = Maps.newHashMap();
    private String selectedTrackBlockValue;

    @Override
    protected void onLoad() {
        super.onLoad();
        loadTrackBlocks();
    }

    private void loadTrackBlocks() {
        clear();
        trackBlockSelectOptions.clear();

        RequestUtils.getInstance().getTrackEditorService().loadTrackBlocks(new AsyncCallback<Collection<TrackBlock>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Collection<TrackBlock> trackBlocks) {
                DeleteOption deleteOption = new DeleteOption();
                add(deleteOption);

                for (TrackBlock trackBlock : trackBlocks) {

                    if (trackBlock.getId() != null) {
                        Option option = new Option();
                        String value = getValueFromTrackBlock(trackBlock);
                        option.setValue(value);
                        option.setText(trackBlock.getDisplayValue());
                        add(option);
                        trackBlockSelectOptions.put(value, trackBlock);
                    }
                }

                if (selectedTrackBlockValue != null) {
                    setValue(selectedTrackBlockValue, false);
                } else {
                    setValue(deleteOption.getValue(), false);
                }

                refresh();
            }
        });

    }

    private String getValueFromTrackBlock(TrackBlock trackBlock) {
        return String.valueOf(trackBlock.getId());
    }

    public void setSelectedTrackBlockValue(TrackBlock trackBlock) {
        if (trackBlock != null) {
            selectedTrackBlockValue = getValueFromTrackBlock(trackBlock);
            if (selectedTrackBlockValue != null) {
                setValue(selectedTrackBlockValue, false);
            }
        }
    }

    public TrackBlock getSelectedTrackBlockValue() {
        TrackBlock trackBlock = null;
        if (!Strings.isNullOrEmpty(getValue())) {
            trackBlock = trackBlockSelectOptions.get(getValue());
        }
        return trackBlock;
    }

    private final class DeleteOption extends Option {
        DeleteOption() {
            setValue(null);
            setText("-- none --");
        }
    }
}
