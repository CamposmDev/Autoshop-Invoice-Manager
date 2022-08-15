package controller;

import app.App;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.database.DB;
import model.ui.FX;
import model.work_order.WorkOrder;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Lists completed work orders in the year and month, also displays recently edited work orders and incompleted work orders
 */
public class MyCompanyController {
    @FXML
    Label lblWorkOrderYearCount, lblWorkOrderMonthCount;
    @FXML
    Label lblYear, lblMonth, lblIncompletedWorkOrderCount;
    @FXML
    TableView<WorkOrder> tvRecentWorkOrders;
    @FXML
    TableColumn<WorkOrder, Integer> colRecentId;
    @FXML
    TableColumn<WorkOrder, String> colRecentCustomer;
    @FXML
    TableColumn<WorkOrder, String> colRecentCompany;
    @FXML
    TableColumn<WorkOrder, String> colRecentVehicle;
    @FXML
    TableColumn<WorkOrder, String> colRecentDateCreated;
    @FXML
    TableColumn<WorkOrder, String> colRecentDateCompleted;
    @FXML
    TableColumn<WorkOrder, String> colRecentInvoiceTotal;
    @FXML
    TableView<WorkOrder> tvIncompletedWorkOrders;
    @FXML
    TableColumn<WorkOrder, Integer> colIncompletedId;
    @FXML
    TableColumn<WorkOrder, String> colIncompletedCustomer;
    @FXML
    TableColumn<WorkOrder, String> colIncompletedCompany;
    @FXML
    TableColumn<WorkOrder, String> colIncompletedVehicle;
    @FXML
    TableColumn<WorkOrder, String> colIncompletedDateCreated;
    @FXML
    TableColumn<WorkOrder, String> colIncompletedDateCompleted;
    @FXML
    TableColumn<WorkOrder, String> colIncompletedInvoiceTotal;

    @FXML
    public void initialize() {
        List<WorkOrder> incompletedWorkOrders = DB.get().workOrders().getIncompletedWorkOrders();
        int incompletedWorkOrderCount = incompletedWorkOrders.size();

        LocalDate currentDate = LocalDate.now();
        String strYear = "(" + currentDate.getYear() + "):";
        String strMonth = "(" + currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "):";

        lblWorkOrderMonthCount.setText(String.valueOf(DB.get().workOrders().getCompletedWorkOrdersThisYear()));
        lblWorkOrderYearCount.setText(String.valueOf(DB.get().workOrders().getCompletedWorkOrdersThisMonth()));
        lblYear.setText(strYear);
        lblMonth.setText(strMonth);
        lblIncompletedWorkOrderCount.setText(String.valueOf(incompletedWorkOrderCount));

        Function<MouseEvent, Boolean> selectWorkOrder = x -> x.getClickCount() == 2 && x.getButton().equals(MouseButton.PRIMARY);

        colRecentId.setCellValueFactory(c -> c.getValue().idProperty());
        colRecentCustomer.setCellValueFactory(c -> c.getValue().getCustomer().nameProperty());
        colRecentCompany.setCellValueFactory(c -> c.getValue().getCustomer().companyProperty());
        colRecentVehicle.setCellValueFactory(c -> c.getValue().vehicleProperty());
        colRecentDateCreated.setCellValueFactory(c -> c.getValue().dateCreatedProperty());
        colRecentDateCompleted.setCellValueFactory(c -> c.getValue().dateCompletedProperty());
        colRecentInvoiceTotal.setCellValueFactory(c -> c.getValue().billProperty());
        tvRecentWorkOrders.getItems().setAll(DB.get().workOrders().getRecents());
        tvRecentWorkOrders.setOnMouseClicked(e -> {
            if (selectWorkOrder.apply(e)) editWorkOrder(tvRecentWorkOrders);
        });

        colIncompletedId.setCellValueFactory(c -> c.getValue().idProperty());
        colIncompletedCustomer.setCellValueFactory(c -> c.getValue().getCustomer().nameProperty());
        colIncompletedCompany.setCellValueFactory(c -> c.getValue().getCustomer().companyProperty());
        colIncompletedVehicle.setCellValueFactory(c -> c.getValue().vehicleProperty());
        colIncompletedDateCreated.setCellValueFactory(c -> c.getValue().dateCreatedProperty());
        colIncompletedDateCompleted.setCellValueFactory(c -> c.getValue().dateCompletedProperty());
        colIncompletedInvoiceTotal.setCellValueFactory(c -> c.getValue().billProperty());
        tvIncompletedWorkOrders.getItems().setAll(incompletedWorkOrders);
        tvIncompletedWorkOrders.setOnMouseClicked(e -> {
            if (selectWorkOrder.apply(e)) editWorkOrder(tvIncompletedWorkOrders);
        });

        FX.autoResizeColumns(tvRecentWorkOrders,75);
        FX.autoResizeColumns(tvIncompletedWorkOrders,75);
    }

    public void editWorkOrder(TableView<WorkOrder> tv) {
        WorkOrder workOrder = tv.getSelectionModel().getSelectedItem();
        if (workOrder != null) {
            WorkOrderWorkspaceController controller = new WorkOrderWorkspaceController(workOrder);
            Parent node = FX.view("Work_Order_Workspace.fxml", controller);
            App.setDisplay(node);
        }
    }
}
