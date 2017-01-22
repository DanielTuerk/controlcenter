package net.wbz.moba.controlcenter.web.client.editor.track;

import java.util.Collection;
import java.util.List;

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
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class BlockEditModal extends Modal {

    private List<TrackBlock> trackBlocks;
    private CellTable<TrackBlock> trackBlockDataGrid;
    /**
     * The provider that holds the list of contacts in the database.
     */
    private ListDataProvider<TrackBlock> dataProvider = new ListDataProvider<>();

    public BlockEditModal() {

        setFade(true);
        setTitle("Blocks");

        ModalBody modalBody = new ModalBody();
        Widget contentPanel = createBody();
        modalBody.add(contentPanel);
        add(modalBody);

        ModalFooter modalFooter = new ModalFooter();
        Button btnOk = new Button("Save", new ClickHandler() {
            public void onClick(ClickEvent event) {
                RequestUtils.getInstance().getTrackEditorService().saveTrackBlocks(trackBlocks,
                        new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                Notify.notify("Editor", "error by save block: " + throwable.getMessage(),
                                        IconType.WARNING);
                            }

                            @Override
                            public void onSuccess(Void aVoid) {
                                Notify.notify("track blocks saved!");
                            }
                        });
                hide();
            }
        });
        btnOk.setType(ButtonType.PRIMARY);
        btnOk.setPull(Pull.RIGHT);
        modalFooter.add(btnOk);

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

    private Widget createBody() {

        FlowPanel widgets = new FlowPanel();

        widgets.add(new Button("add block", IconType.PLUS, new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                trackBlocks.add(new TrackBlock());
                reloadTable();
            }
        }));

        trackBlockDataGrid = new CellTable<>();
        trackBlockDataGrid.setBordered(true);
        trackBlockDataGrid.setCondensed(true);
        trackBlockDataGrid.setStriped(true);
        trackBlockDataGrid.setHover(true);
        // dataProvider.addDataDisplay(trackBlockDataGrid);

        trackBlockDataGrid.addColumn(createColumn(new TextInputCell(), new GetValue<String>() {
            @Override
            public String getValue(TrackBlock contact) {
                return contact.getName();
            }
        }, new FieldUpdater<TrackBlock, String>() {
            @Override
            public void update(int index, TrackBlock object, String value) {
                object.setName(value);
            }
        }), "Name");
        trackBlockDataGrid.addColumn(createColumn(new TextInputCell(), new GetValue<String>() {
            @Override
            public String getValue(TrackBlock trackBlock) {
                BusDataConfiguration blockFunction = trackBlock.getBlockFunction();
                if (blockFunction != null) {
                    return String.valueOf(blockFunction.getAddress());
                }
                return null;
            }
        }, new FieldUpdater<TrackBlock, String>() {
            @Override
            public void update(int index, TrackBlock object, String value) {
                if (object.getBlockFunction() == null) {
                    object.setBlockFunction(new BusDataConfiguration(1, null, null, null));
                }
                object.getBlockFunction().setAddress(Integer.parseInt(value));
            }
        }), "Address");

        trackBlockDataGrid.addColumn(createColumn(new TextInputCell(), new GetValue<String>() {
            @Override
            public String getValue(TrackBlock trackBlock) {
                BusDataConfiguration blockFunction = trackBlock.getBlockFunction();
                if (blockFunction != null) {
                    return String.valueOf(blockFunction.getBit());
                }
                return null;
            }
        }, new FieldUpdater<TrackBlock, String>() {
            @Override
            public void update(int index, TrackBlock object, String value) {
                if (object.getBlockFunction() == null) {
                    object.setBlockFunction(new BusDataConfiguration(1, null, null, null));
                }
                object.getBlockFunction().setBit(Integer.parseInt(value));
                object.getBlockFunction().setBitState(true);
            }
        }), "Bit");

        // ActionCell.
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
                trackBlocks.remove(object);
                reloadTable();
            }
        });

        trackBlockDataGrid.addColumn(col4);

        // trackBlockDataGrid.createColumn(createColumn(new ActionCell<>("Delete", new ActionCell.Delegate<TrackBlock>() {
        // @Override
        // public void execute(TrackBlock contact) {
        // trackBlocks.remove(contact);
        // reloadTable();
        // }
        // }), "", new GetValue<TrackBlock>() {
        // @Override
        // public TrackBlock getValue(TrackBlock contact) {
        // return contact;
        // }
        // }, null));

        widgets.add(trackBlockDataGrid);

        return widgets;
    }

    private void reloadTable() {
        // TODO reload table
        // dataProvider.refresh();
        trackBlockDataGrid.setRowCount(trackBlocks.size(), true);
        trackBlockDataGrid.setRowData(0, trackBlocks);

        trackBlockDataGrid.redraw();
    }

    /**
     * Add a column with a header.
     *
     * @param <C> the cell type
     * @param cell the cell used to render the column
     * @param getter the value getter for the cell
     */
    private <C> Column<TrackBlock, C> createColumn(Cell<C> cell,
                                                   final GetValue<C> getter, FieldUpdater<TrackBlock, C> fieldUpdater) {
        Column<TrackBlock, C> column = new Column<TrackBlock, C>(cell) {
            @Override
            public C getValue(TrackBlock object) {
                return getter.getValue(object);
            }
        };
        column.setFieldUpdater(fieldUpdater);
        return column;
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        // load data
        RequestUtils.getInstance().getTrackEditorService().loadTrackBlocks(new AsyncCallback<Collection<TrackBlock>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Collection<TrackBlock> trackBlocks) {
                BlockEditModal.this.trackBlocks = Lists.newArrayList(trackBlocks);
                dataProvider.setList(BlockEditModal.this.trackBlocks);
                dataProvider.refresh();

                reloadTable();
            }
        });
    }

    /**
     * Get a cell value from a record.
     *
     * @param <C> the cell type
     */
    private static interface GetValue<C> {
        C getValue(TrackBlock trackBlock);
    }
}
