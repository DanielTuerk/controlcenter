package net.wbz.moba.controlcenter.web.client.editor.track;

import java.util.Collection;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class BlockEditModal extends Modal {

    /**
     * The provider that holds the list of contacts in the database.
     */
    private ListDataProvider<TrackBlock> dataProvider = new ListDataProvider<>();

    public BlockEditModal() {
        super();
        setFade(true);
        setTitle("Blocks");

        ModalBody modalBody = new ModalBody();
        Widget contentPanel = createBody();
        modalBody.add(contentPanel);
        add(modalBody);

        ModalFooter modalFooter = new ModalFooter();
        Button btnSave = new Button("Save", new ClickHandler() {
            public void onClick(ClickEvent event) {
                RequestUtils.getInstance().getTrackEditorService().saveTrackBlocks(
                        Lists.newArrayList(dataProvider.getList()),
                        new OnlySuccessAsyncCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Notify.notify("track blocks saved!");
                            }
                        });
                hide();
            }
        });
        btnSave.setType(ButtonType.PRIMARY);
        btnSave.setPull(Pull.RIGHT);
        modalFooter.add(btnSave);

        Button btnClose = new Button("Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        btnClose.setType(ButtonType.DANGER);
        btnClose.setPull(Pull.LEFT);
        modalFooter.add(btnClose);
        add(modalFooter);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        loadData();
    }

    private void loadData() {
        // load data
        RequestUtils.getInstance().getTrackEditorService().loadTrackBlocks(new AsyncCallback<Collection<TrackBlock>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Collection<TrackBlock> trackBlocks) {
                dataProvider.setList(Lists.newArrayList(trackBlocks));
                dataProvider.refresh();
            }
        });
    }

    private Widget createBody() {
        FlowPanel widgets = new FlowPanel();
        widgets.add(new Button("add block", IconType.PLUS, new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                dataProvider.getList().add(new TrackBlock());
                dataProvider.refresh();
            }
        }));

        CellTable<TrackBlock> trackBlockDataGrid = new CellTable<>();
        trackBlockDataGrid.setBordered(true);
        trackBlockDataGrid.setCondensed(true);
        trackBlockDataGrid.setStriped(true);
        trackBlockDataGrid.setHover(true);

        final Column<TrackBlock, String> colName = new Column<TrackBlock, String>(new TextInputCell()) {
            @Override
            public String getValue(TrackBlock trackBlock) {
                return trackBlock.getName();
            }
        };
        colName.setFieldUpdater(new FieldUpdater<TrackBlock, String>() {
            @Override
            public void update(int index, TrackBlock object, String value) {
                object.setName(value);
            }
        });
        trackBlockDataGrid.addColumn(colName, "Name");
        final Column<TrackBlock, String> colAddress = new Column<TrackBlock, String>(new TextInputCell()) {
            @Override
            public String getValue(TrackBlock trackBlock) {
                BusDataConfiguration blockFunction = trackBlock.getBlockFunction();
                if (blockFunction != null) {
                    return String.valueOf(blockFunction.getAddress());
                }
                return "";
            }
        };
        colAddress.setFieldUpdater(new FieldUpdater<TrackBlock, String>() {
            @Override
            public void update(int index, TrackBlock trackBlock, String value) {
                if (trackBlock.getBlockFunction() == null) {
                    trackBlock.setBlockFunction(new BusDataConfiguration(1, null, null, null));
                }
                trackBlock.getBlockFunction().setAddress(Integer.parseInt(value));
            }
        });
        trackBlockDataGrid.addColumn(colAddress, "Address");

        final Column<TrackBlock, String> colBit = new Column<TrackBlock, String>(new TextInputCell()) {
            @Override
            public String getValue(TrackBlock trackBlock) {
                BusDataConfiguration blockFunction = trackBlock.getBlockFunction();
                if (blockFunction != null) {
                    return String.valueOf(blockFunction.getBit());
                }
                return "";
            }
        };
        colBit.setFieldUpdater(new FieldUpdater<TrackBlock, String>() {
            @Override
            public void update(int index, TrackBlock trackBlock, String value) {
                if (trackBlock.getBlockFunction() == null) {
                    trackBlock.setBlockFunction(new BusDataConfiguration(1, null, null, null));
                }
                trackBlock.getBlockFunction().setBit(Integer.parseInt(value));
                trackBlock.getBlockFunction().setBitState(true);
            }
        });
        trackBlockDataGrid.addColumn(colBit, "Bit");

        final Column<TrackBlock, String> col4 = new Column<TrackBlock, String>(new ButtonCell(ButtonType.DANGER,
                IconType.TRASH)) {
            @Override
            public String getValue(TrackBlock object) {
                return "";
            }
        };
        col4.setFieldUpdater(new FieldUpdater<TrackBlock, String>() {
            @Override
            public void update(int index, TrackBlock object, String value) {
                RequestUtils.getInstance().getTrackEditorService().deleteTrackBlock(object,
                        new OnlySuccessAsyncCallback<Void>("Block", "can't delete") {
                            @Override
                            public void onSuccess(Void result) {
                                loadData();
                            }
                        });
            }
        });
        trackBlockDataGrid.addColumn(col4, "Delete");

        dataProvider.addDataDisplay(trackBlockDataGrid);
        widgets.add(trackBlockDataGrid);
        return widgets;
    }

}
