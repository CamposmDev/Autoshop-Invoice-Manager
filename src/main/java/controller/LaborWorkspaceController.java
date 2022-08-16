package controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import model.Preferences;
import model.tps.AddLaborTransaction;
import model.work_order.AutoPart;
import model.work_order.Labor;
import model.work_order.WorkOrder;

import java.util.Iterator;

public class LaborWorkspaceController {
    @FXML
    Text lblId;
    @FXML
    TextField tfLaborCode;
    @FXML
    TextArea taDesc;
    @FXML
    TextField tfBilledHrs, tfRate;
    @FXML
    CheckBox cbTaxable;
    @FXML
    CheckBox cbAutoGen;

    @FXML
    public void initialize() {
        tfLaborCode.setText("0");
        tfBilledHrs.setText("1.0");
        tfRate.setText(Preferences.get().getLaborRate().toString());

        // Initialize listeners for fields
        tfLaborCode.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB) {
                taDesc.requestFocus();
                taDesc.positionCaret(taDesc.getText().length());
            }
        });
        taDesc.textProperty().addListener((ov,strOld,strCurrent) -> {
            if (strCurrent.contains("\t")) {
                taDesc.setText(strOld);
                tfBilledHrs.requestFocus();
            }
        });
        tfRate.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB) cbTaxable.requestFocus();
        });
    }

    public void saveLabor(WorkOrder workOrder) {
        Labor labor = buildLabor(workOrder);
        AddLaborTransaction transaction = new AddLaborTransaction(workOrder, labor);
        workOrder.getTps().addTransaction(transaction);
    }

    public void updateLabor(WorkOrder workOrder, Labor oldLabor) {
        Labor newLabor = buildLabor(workOrder);
        workOrder.updateLabor(oldLabor, newLabor);
    }

    public void loadLabor(Labor labor) {
        lblId.setText(String.valueOf(labor.getId()));
        tfLaborCode.setText(labor.getName());
        taDesc.setText(labor.getDesc());
        tfBilledHrs.setText(String.valueOf(labor.getBilledHrs()));
        tfRate.setText(String.valueOf(labor.getRate()));
        cbTaxable.setSelected(labor.isTaxable());
    }

    public Labor buildLabor(WorkOrder workOrder) {
        int id = Integer.parseInt(lblId.getText());
        String laborCode = tfLaborCode.getText();
        String desc = taDesc.getText();

        if (cbAutoGen.isSelected()) {
            Iterator<AutoPart> iter = workOrder.autoPartIterator();
            StringBuilder sb = new StringBuilder("Installed ");
            while (iter.hasNext()) {
                sb.append(iter.next().getDesc());
                if (iter.hasNext())
                    sb.append(", ");
            }
            desc = sb.toString();
        }

        double billedHrs, rate;
        try {
            billedHrs = Double.parseDouble(tfBilledHrs.getText());
        } catch (NumberFormatException e) {
            billedHrs = 0.0;
        }
        try {
            rate = Double.parseDouble(tfRate.getText());
        } catch (NumberFormatException e) {
            rate = 0.0;
        }
        boolean taxable = cbTaxable.isSelected();
        Labor labor = new Labor(laborCode, desc, billedHrs, rate, taxable);
        labor.setId(id);
        return labor;
    }

    public void genDesc() {
        taDesc.setDisable(cbAutoGen.isSelected());
        taDesc.clear();
    }
}
