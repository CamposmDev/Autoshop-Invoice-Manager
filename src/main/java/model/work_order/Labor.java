package model.work_order;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Labor extends Product implements Comparable<Labor> {
    private double billedHrs, rate;
    private boolean taxable;

    public Labor(String name, String desc, double billedHrs, double rate, boolean taxable) {
        super(name, desc);
        this.billedHrs = billedHrs;
        this.rate = rate;
        this.taxable = taxable;
    }

    public double getBilledHrs() {
        return billedHrs;
    }

    public void setBilledHrs(double billedHrs) {
        this.billedHrs = billedHrs;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    @Override
    public double subtotal() {
        return billedHrs * rate;
    }

    @Override
    public double tax(double taxRate) {
        return taxable ? taxRate * subtotal() : 0;
    }

    @Override
    public double bill(double taxRate) {
        return taxable ? tax(taxRate) + (billedHrs * rate) : billedHrs * rate;
    }

    @Override
    public String toString() {
        return "Labor{" + super.toString() +
                ", billedHrs=" + billedHrs +
                ", rate=" + rate +
                ", taxable=" + taxable +
                '}';
    }

    public SimpleObjectProperty<Double> billedHrsProperty() {
        return new SimpleObjectProperty<>(billedHrs);
    }

    public SimpleStringProperty rateProperty() {
        return new SimpleStringProperty(String.format("%.2f", rate));
    }

    public SimpleStringProperty subtotalProperty() {
        return new SimpleStringProperty(String.format("$ %.2f", subtotal()));
    }

    public SimpleStringProperty billProperty(double taxRate) {
        return new SimpleStringProperty(String.format("$ %.2f", bill(taxRate)));
    }

    @Override
    public int compareTo(Labor o) {
       return getName().compareTo(o.getName());
    }
}
