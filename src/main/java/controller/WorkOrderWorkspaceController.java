package controller;

import app.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import model.State;
import model.customer.Address;
import model.customer.Customer;
import model.database.DB;
import model.ui.AlertFactory;
import model.ui.FX;
import model.work_order.AutoPart;
import model.work_order.Labor;
import model.work_order.Vehicle;
import model.work_order.WorkOrder;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.textfield.TextFields;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class WorkOrderWorkspaceController {
    protected WorkOrder workOrder;
    @FXML
    TextField tfFirstName, tfLastName, tfPhone, tfEmail, tfCompany, tfAddress, tfCity, tfState, tfZip;
    @FXML
    TextField tfVin, tfLicensePlate, tfColor, tfYear, tfMake, tfModel, tfEngine, tfTransmission, tfMileageIn, tfMileageOut;
    @FXML
    TableView<AutoPart> tvParts;
    @FXML
    TableColumn<AutoPart, String> colPartNumber, colPartDesc;
    @FXML
    TableColumn<AutoPart, Integer> colPartQuantity;
    @FXML
    TableColumn<AutoPart, String> colPartRetailPrice, colPartListPrice;
    @FXML
    TableColumn<AutoPart, String> colPartTotal;
    @FXML
    TableView<Labor> tvLabor;
    @FXML
    TableColumn<Labor, String> colLaborCode, colLaborDesc;
    @FXML
    TableColumn<Labor, Double> colLaborBilledHrs;
    @FXML
    TableColumn<Labor, String> colLaborRate;
    @FXML
    TableColumn<Labor, String> colLaborTotal;
    @FXML
    TextField tfPartsTotal, tfTaxTotal, tfDiscount, tfLaborTotal, tfSubtotal, tfTotal;
    @FXML
    TextField tfWorkOrderId, tfDateCreated;
    @FXML
    DatePicker dateCompletedPicker;
    @FXML
    Button btCus, btVeh;
    @FXML
    PopOver customerPopOver, vehiclePopOver;

    public WorkOrderWorkspaceController() { // New Work Order
        this.workOrder = new WorkOrder();
    }

    public WorkOrderWorkspaceController(WorkOrder workOrder) { // Update Work Order
        this.workOrder = workOrder;
    }

    /**
     * Initializes what columns hold what values binds tables to
     * display parts and labor of a work order
     */
    @FXML
    public void initialize() throws IOException {
        // Bind TextFields for auto-completion
        TextFields.bindAutoCompletion(tfAddress, DB.get().customers().getUniqueStreets());
        TextFields.bindAutoCompletion(tfCity, DB.get().customers().getUniqueCities());
        TextFields.bindAutoCompletion(tfState, State.list());
        TextFields.bindAutoCompletion(tfZip, DB.get().customers().getUniqueZips());
        TextFields.bindAutoCompletion(tfYear, DB.get().vehicles().getUniqueYear());
        TextFields.bindAutoCompletion(tfMake, DB.get().vehicles().getUniqueMake());
        TextFields.bindAutoCompletion(tfModel, DB.get().vehicles().getUniqueModel());
        TextFields.bindAutoCompletion(tfColor, DB.get().vehicles().getUniqueColor());
        TextFields.bindAutoCompletion(tfEngine, DB.get().vehicles().getUniqueEngine());
        TextFields.bindAutoCompletion(tfTransmission, DB.get().vehicles().getUniqueYear());

        // Add Listeners to text fields vin and license plate
        tfVin.textProperty().addListener((o,x,y) -> tfVin.setText(y.toUpperCase()));
        tfLicensePlate.textProperty().addListener((o,x,y) -> tfLicensePlate.setText(y.toUpperCase()));

        // Bind columns to appropriate fields in WorkOrder
        colPartNumber.setCellValueFactory(c -> c.getValue().nameProperty());
        colPartDesc.setCellValueFactory(c -> c.getValue().descProperty());
        colPartQuantity.setCellValueFactory(c -> c.getValue().quantityProperty());
        colPartRetailPrice.setCellValueFactory(c -> c.getValue().retailPriceProperty());
        colPartListPrice.setCellValueFactory(c -> c.getValue().listPriceProperty());
        colPartTotal.setCellValueFactory(c -> c.getValue().subtotalProperty());
        colLaborCode.setCellValueFactory(c -> c.getValue().nameProperty());
        colLaborDesc.setCellValueFactory(c -> c.getValue().descProperty());
        colLaborBilledHrs.setCellValueFactory(c -> c.getValue().billedHrsProperty());
        colLaborRate.setCellValueFactory(c -> c.getValue().rateProperty());
        colLaborTotal.setCellValueFactory(c -> c.getValue().subtotalProperty());

        // Set double-click function for parts and labor tables
        // Set Parts and Labor Items
        final int DOUBLE_CLICK = 2;
        Function<MouseEvent, Boolean> doubleClicked = x -> x.getButton().equals(MouseButton.PRIMARY) && x.getClickCount() == DOUBLE_CLICK;
        tvParts.setOnMouseClicked(e -> {
            if (doubleClicked.apply(e))
                editPart();
        });
        tvParts.setItems(workOrder.itemList());
        tvLabor.setOnMouseClicked(e -> {
            if (doubleClicked.apply(e))
                editLabor();
        });
        tvLabor.setItems(workOrder.laborList());

        // Set date created value to current date
        tfDateCreated.setText(workOrder.getDateCreated().toLocalDate().format(DateTimeFormatter.ofPattern("MM/dd/u")));
        dateCompletedPicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate x) {
                return x != null ? x.format(DateTimeFormatter.ofPattern("MM/dd/u")) : null;
            }

            @Override
            public LocalDate fromString(String s) {
                return LocalDate.now();
            }
        });

        if (workOrder.isNew()) {
            tfWorkOrderId.setText(String.valueOf(DB.get().workOrders().getNextId()));
        } else {
            loadCustomer(workOrder.getCustomer());
            loadVehicle(workOrder.getVehicle());
            tfWorkOrderId.setText(String.valueOf(workOrder.getId()));
            if (workOrder.getDateCompleted() != null) {
                dateCompletedPicker.setValue(workOrder.getDateCompleted().toLocalDate());
            }
            updateTotals();
        }

        FXMLLoader fxmlLoader = FX.load("Customer_Table.fxml");
        customerPopOver = new PopOver(fxmlLoader.load());
        CustomerTableController customerTableController = fxmlLoader.getController();
        customerTableController.connect(this);
        fxmlLoader = FX.load("Vehicle_Table.fxml");
        vehiclePopOver = new PopOver(fxmlLoader.load());
        VehicleTableController vehicleTableController = fxmlLoader.getController();
        vehicleTableController.connect(this);
    }

    public void showCustomer() throws IOException {
        customerPopOver.show(btCus);
    }

    public void showVehicle() throws IOException {
        vehiclePopOver.show(btVeh);
    }

    public void print() {
        save();
        AlertFactory.showPrintWorkOrder(workOrder, this);
    }

    public void save() {
        buildWorkOrder();
        if (workOrder.isNew()) {
            DB.get().workOrders().add(workOrder);
        } else {
            DB.get().workOrders().update(workOrder);
            DB.get().deleteProductsMarkedForDeletion();
        }
    }

    public void saveAndClose() {
        save();
        close();
    }

    public void close() {
        DB.get().clearAllProductsMarkedForDeletion();
        App.clearDisplay();
    }

    public Customer buildCustomer() {
        String firstName = tfFirstName.getText();
        String lastName = tfLastName.getText();
        String phone = tfPhone.getText();
        String email = tfEmail.getText();
        String company = tfCompany.getText();
        String street = tfAddress.getText();
        String city = tfCity.getText();
        String state = tfState.getText();
        String zip = tfZip.getText();
        Address address = new Address(street, city, state, zip);
        Customer customer = new Customer(firstName, lastName, phone, email, company, address);
        return customer;
    }

    public Vehicle buildVehicle() {
        String vin = tfVin.getText();
        String licensePlate = tfLicensePlate.getText();
        String color = tfColor.getText();
        int year = Integer.parseInt(tfYear.getText());
        String make = tfMake.getText();
        String model = tfModel.getText();
        String engine = tfEngine.getText();
        String transmission = tfTransmission.getText();
        String mileageIn = tfMileageIn.getText();
        String mileageOut = tfMileageOut.getText();
        Vehicle vehicle = new Vehicle(vin, year, make, model, licensePlate, color,
                engine, transmission, mileageIn, mileageOut);
        return vehicle;
    }

    public void loadCustomer(@NotNull Customer customer) {
        tfFirstName.setText(customer.getFirstName());
        tfLastName.setText(customer.getLastName());
        tfPhone.setText(customer.getPhone());
        tfEmail.setText(customer.getEmail());
        tfCompany.setText(customer.getCompany());
        tfAddress.setText(customer.getAddress().getStreet());
        tfCity.setText(customer.getAddress().getCity());
        tfState.setText(customer.getAddress().getState());
        tfZip.setText(customer.getAddress().getZip());
    }

    public void loadVehicle(@NotNull Vehicle vehicle) {
        tfVin.setText(vehicle.getVin());
        tfLicensePlate.setText(vehicle.getLicensePlate());
        tfColor.setText(vehicle.getColor());
        tfYear.setText(String.valueOf(vehicle.getYear()));
        tfMake.setText(vehicle.getMake());
        tfModel.setText(vehicle.getModel());
        tfEngine.setText(vehicle.getEngine());
        tfTransmission.setText(vehicle.getTransmission());
//        tfMileageIn.setText(vehicle.getMileageIn());
//        tfMileageOut.setText(vehicle.getMileageOut());
    }

    public void buildWorkOrder() {
        Customer customer = buildCustomer();
        Vehicle vehicle = buildVehicle();
        workOrder.setCustomer(customer);
        workOrder.setVehicle(vehicle);
        LocalDate dateCompleted = dateCompletedPicker.getValue();
        if (dateCompleted != null) {
            workOrder.setDateCompleted(Date.valueOf(dateCompleted));
        }
    }

    public void addPart() {
        AlertFactory.showAddPart(workOrder);
        updateTotals();
    }

    public void editPart() {
        AutoPart selectedItem = tvParts.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            AlertFactory.showEditPart(workOrder, selectedItem);
            updateTotals();
        }
    }

    public void deletePart() {
        AutoPart autoPart = tvParts.getSelectionModel().getSelectedItem();
        if (autoPart != null) {
            if (!autoPart.isNew()) {
                DB.get().addProductMarkedForDeletion(autoPart);
            }
            workOrder.removeItem(autoPart);
            updateTotals();
        }
    }

    public void addLabor() {
        AlertFactory.showAddLabor(workOrder);
        updateTotals();
    }

    public void editLabor() {
        Labor labor = tvLabor.getSelectionModel().getSelectedItem();
        if (labor != null) {
            AlertFactory.showEditLabor(workOrder, labor);
            updateTotals();
        }
    }

    public void deleteLabor() {
        Labor labor = tvLabor.getSelectionModel().getSelectedItem();
        if (labor != null) {
            if (!labor.isNew()) {
                DB.get().addProductMarkedForDeletion(labor);
            }
            workOrder.removeLabor(labor);
            updateTotals();
        }
    }

    public void updateTotals() {
        tfPartsTotal.setText(String.format("%.2f", workOrder.partsSubtotal()));
        tfTaxTotal.setText(String.format("%.2f", workOrder.tax()));
        tfLaborTotal.setText(String.format("%.2f", workOrder.laborSubtotal()));
        tfSubtotal.setText(String.format("%.2f", workOrder.subtotal()));
        tfTotal.setText(String.format("%.2f", workOrder.bill()));
    }
}
