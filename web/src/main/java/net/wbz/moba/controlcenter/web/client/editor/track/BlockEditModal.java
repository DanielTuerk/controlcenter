package net.wbz.moba.controlcenter.web.client.editor.track;

import java.util.Collection;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.PaginationSize;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RangeChangeEvent;

import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock.DRIVING_LEVEL_ADJUST_TYPE;

/**
 * @author Daniel Tuerk
 */
public class BlockEditModal extends Modal {

    private static final String TITLE = "Blocks";

    /**
     * The provider that holds the list of track blocks to save in the database.
     */
    private ListDataProvider<TrackBlock> dataProvider = new ListDataProvider<>();
    private final ModalBody modalBody;
    private final ModalFooter modalFooter;
    private final FlowPanel overviewFooterPanel;
    private final Widget overviewBody;
    private SimplePager simplePager = new SimplePager();
    private Pagination pagination = new Pagination(PaginationSize.SMALL);

    public BlockEditModal() {
        super();
        setFade(true);
        setTitle(TITLE);

        overviewBody = createOverviewBody();
        overviewFooterPanel = createOverviewFooter();

        modalBody = new ModalBody();
        add(modalBody);

        modalFooter = new ModalFooter();
        add(modalFooter);
    }

    private FlowPanel createEditFooter(final BlockEditBody blockEditBody) {
        return createFooter("Apply Changes", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                blockEditBody.applyChanges();
                showOverview();
            }
        }, "Back to overview", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showOverview();
            }
        });
    }

    private FlowPanel createOverviewFooter() {
        return createFooter("Save", new ClickHandler() {
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
        }, "Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
    }

    private FlowPanel createFooter(String btnConfirmText, ClickHandler btnConfirmClickHandler, String btnCancelText,
            ClickHandler btnCancelClickHandler) {
        FlowPanel panel = new FlowPanel();
        Button btnSave = new Button(btnConfirmText, btnConfirmClickHandler);
        btnSave.setType(ButtonType.PRIMARY);
        btnSave.setPull(Pull.RIGHT);
        panel.add(btnSave);

        Button btnClose = new Button(btnCancelText, btnCancelClickHandler);
        btnClose.setType(ButtonType.DANGER);
        btnClose.setPull(Pull.LEFT);
        panel.add(btnClose);
        return panel;
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        showOverview();

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
                dataProvider.flush();

                pagination.rebuild(simplePager);
            }
        });
    }

    @Override
    protected void onShow(Event evt) {
        super.onShow(evt);
        loadData();
    }

    private void showOverview() {
        modalFooter.clear();
        modalFooter.add(overviewFooterPanel);

        modalBody.clear();
        modalBody.add(overviewBody);

        dataProvider.refresh();

        pagination.rebuild(simplePager);
    }

    private void showEdit(TrackBlock trackBlock) {
        modalBody.clear();
        BlockEditBody blockEditBody = new BlockEditBody(trackBlock);
        modalBody.add(blockEditBody);

        modalFooter.clear();
        modalFooter.add(createEditFooter(blockEditBody));
    }

    private Widget createOverviewBody() {
        FlowPanel widgets = new FlowPanel();
        widgets.add(new Button("add block", IconType.PLUS, clickEvent -> {
            TrackBlock trackBlock = new TrackBlock();
            trackBlock.setDrivingLevelAdjustType(DRIVING_LEVEL_ADJUST_TYPE.NONE);

            dataProvider.getList().add(trackBlock);
            dataProvider.flush();

            pagination.rebuild(simplePager);

        }));

        CellTable<TrackBlock> trackBlockDataGrid = new CellTable<>();
        trackBlockDataGrid.setBordered(true);
        trackBlockDataGrid.setCondensed(true);
        trackBlockDataGrid.setStriped(true);
        trackBlockDataGrid.setHover(true);

        trackBlockDataGrid.addColumn(new TextColumn<TrackBlock>() {
            @Override
            public String getValue(TrackBlock trackBlock) {
                return trackBlock.getName();
            }
        }, "Name");
        trackBlockDataGrid.addColumn(new TextColumn<TrackBlock>() {
            @Override
            public String getValue(TrackBlock trackBlock) {
                BusDataConfiguration blockFunction = trackBlock.getBlockFunction();
                if (blockFunction != null) {
                    return getStringValue(blockFunction.getAddress());
                }
                return "";
            }
        }, "Address");
        trackBlockDataGrid.addColumn(new TextColumn<TrackBlock>() {
            @Override
            public String getValue(TrackBlock trackBlock) {
                BusDataConfiguration blockFunction = trackBlock.getBlockFunction();
                if (blockFunction != null) {
                    return getStringValue(blockFunction.getBit());
                }
                return "";
            }
        }, "Bit");

        final Column<TrackBlock, String> colEdit = new Column<TrackBlock, String>(new ButtonCell(ButtonType.DEFAULT,
                IconType.EDIT)) {
            @Override
            public String getValue(TrackBlock object) {
                return "";
            }
        };
        colEdit.setFieldUpdater(new FieldUpdater<TrackBlock, String>() {
            @Override
            public void update(int index, TrackBlock object, String value) {
                showEdit(object);
            }
        });
        trackBlockDataGrid.addColumn(colEdit, "Edit");

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
                                // TODO das schmeißt alle anderen änderungen weg
                                loadData();
                            }
                        });
            }
        });
        trackBlockDataGrid.addColumn(col4, "Delete");

        widgets.add(trackBlockDataGrid);
        widgets.add(pagination);

        trackBlockDataGrid.addRangeChangeHandler(new RangeChangeEvent.Handler() {

            @Override
            public void onRangeChange(final RangeChangeEvent event) {
                pagination.rebuild(simplePager);
            }
        });

        simplePager.setDisplay(trackBlockDataGrid);
        pagination.clear();

        dataProvider.addDataDisplay(trackBlockDataGrid);

        return widgets;
    }

    private String getStringValue(Integer value) {
        return value == null ? "" : String.valueOf(value);
    }

}
