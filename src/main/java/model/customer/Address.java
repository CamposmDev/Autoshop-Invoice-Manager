package model.customer;

import javafx.beans.property.SimpleStringProperty;
import model.State;

public class Address {
    private String street, city, state, zip;

    public Address(String street, String city, String state, String zip) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public SimpleStringProperty streetProperty() {
        return new SimpleStringProperty(street);
    }

    public SimpleStringProperty cityProperty() {
        return new SimpleStringProperty(city);
    }

    public SimpleStringProperty stateProperty() {
        return new SimpleStringProperty(state);
    }

    public SimpleStringProperty zipProperty() {
        return new SimpleStringProperty(zip);
    }

    @Override
    public String toString() {
        String s = "";
        if (!street.isBlank()) s += street;
        if (!city.isBlank()) s += ' ' + city;
        if (!state.isBlank()) s += ", " + State.valueOf(state);
        if (!zip.isBlank()) s += ' ' + zip;
        return s;
    }
}
