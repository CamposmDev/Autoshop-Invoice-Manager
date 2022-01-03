package model;

public class Item implements Billable, Comparable<Item> {
    private String id;
    private String desc;
    private double retailPrice;
    private double listPrice;
    private boolean taxable;
    private int quantity;

    public Item(String id, String desc, double retailPrice, double listPrice, int quantity, boolean taxable) {
        this.id = id;
        this.desc = desc;
        this.retailPrice = retailPrice;
        this.listPrice = listPrice;
        this.quantity = quantity;
        this.taxable = taxable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDes(String desc) {
        this.desc = desc;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public double getListPrice() {
        return listPrice;
    }

    public void setListPrice(double listPrice) {
        this.listPrice = listPrice;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public double bill() {
        return taxable ? TAX_RATE * (retailPrice * quantity) : retailPrice * quantity;
    }

    @Override
    public int compareTo(Item o) {
        return this.id.compareTo(o.id);
    }
}