package net.wbz.moba.controlcenter.web.client.train;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.components.modal.DeleteModal;
import net.wbz.moba.controlcenter.web.client.components.table.DeleteButtonColumn;
import net.wbz.moba.controlcenter.web.client.components.table.EditButtonColumn;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.train.TrainDataChangedRemoteListener;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

/**
 * @author Daniel Tuerk
 */
public class TrainPanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);
    private final TrainDataChangedRemoteListener trainDataChangedRemoteListener;
    @UiField
    Pagination pagination;
    @UiField
    CellTable<Train> trainTable;
    private SimplePager simplePager = new SimplePager();
    private ListDataProvider<Train> dataProvider = new ListDataProvider<>();

    public TrainPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        trainDataChangedRemoteListener = anEvent -> loadTrains();

        trainTable.addColumn(new TextColumn<Train>() {
            @Override
            public String getValue(Train object) {
                return object.getName();
            }
        }, "Name");
        trainTable.addColumn(new TextColumn<Train>() {
            @Override
            public String getValue(Train object) {
                return String.valueOf(object.getAddress());
            }
        }, "Address");
        trainTable.addColumn(new TextColumn<Train>() {
            @Override
            public String getValue(Train train) {
                // TODO refactor to component like list
                StringBuilder sb = new StringBuilder();
                if (train.getFunctions() != null) {
                    for (TrainFunction trainFunction : train.getFunctions()) {
                        BusDataConfiguration configuration = trainFunction.getConfiguration();
                        if (configuration != null) {
                            sb.append(trainFunction.getAlias())
                                .append(" (")
                                .append(configuration.getAddress()).append(" - ").append(configuration.getBit())
                                .append(" )").append("\n");
                        }
                    }
                }
                return sb.toString();
            }
        }, "Extra Functions");

        trainTable.addColumn(new EditButtonColumn<Train>() {
            @Override
            public void onAction(Train object) {
                showEdit(object);
            }
        }, "Edit");

        trainTable.addColumn(new DeleteButtonColumn<Train>() {
            @Override
            public void onAction(Train object) {
                showDelete(object);
            }
        }, "Delete");

        trainTable.addRangeChangeHandler(event -> pagination.rebuild(simplePager));

        simplePager.setDisplay(trainTable);
        pagination.clear();

        dataProvider.addDataDisplay(trainTable);

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(trainDataChangedRemoteListener);

        loadTrains();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(trainDataChangedRemoteListener);
    }

    private void showDelete(final Train train) {
        new DeleteModal("Delete train " + train.getName() + "?") {

            @Override
            public void onConfirm() {
                RequestUtils.getInstance().getTrainEditorService().deleteTrain(train.getId(),
                    RequestUtils.VOID_ASYNC_CALLBACK);
                hide();
            }
        }.show();
    }

    @UiHandler("btnCreateTrain")
    void onClick(ClickEvent ignored) {
        new TrainCreateModal(new Train()).show();
    }

    private void loadTrains() {
        RequestUtils.getInstance().getTrainEditorService().getTrains(
            new OnlySuccessAsyncCallback<Collection<Train>>() {
                @Override
                public void onSuccess(Collection<Train> result) {
                    dataProvider.setList(Lists.newArrayList(result));
                    dataProvider.flush();
                    dataProvider.refresh();
                    pagination.rebuild(simplePager);
                }
            });
    }

    private void showEdit(Train train) {
        new TrainEditModal(train).show();
    }

    interface Binder extends UiBinder<Widget, TrainPanel> {

    }

}
