package model;

import app.App;
import model.ui.GUIScale;
import model.ui.Theme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Preferences {
    private static Preferences singleton;
    private static final String PREF_FILE = "./preferences.config";

    public static Preferences get() {
        if (singleton == null) init();
        return singleton;
    }

    public static void init() {
        singleton = new Preferences();
    }

    private String company, address, city, tempCompany, tempAddress, tempCity;
    private State state, tempState;
    private String zip, phone, repairShopId, tempZip, tempPhone, tempRepairShopId;
    private String title, tempTitle;
    private Double laborRate, tempLaborRate;
    private Double taxRate, tempTaxRate;
    private GUIScale guiScale, tempGuiScale;
    private Theme theme, tempTheme;
    private Boolean confirmExit, tempConfirmExit;
    private List<Observable> observables;

    private Preferences() {
        defaultBuild();
        readFile();
        App.get().setScale(GUIScale.getStyleClass(this.guiScale));
        App.get().setTheme(theme);
        System.out.println(this);
    }

    public void defaultBuild() {
        company = "Your Company";
        address = "123 Some Street";
        city = "Some City";
        state = State.UNKNOWN;
        zip = "11355";
        phone = "000-000-0000";
        repairShopId = "0000000";
        title = "Title Goes Here";
        laborRate = 90.0;
        taxRate = 1.08625;
        guiScale = GUIScale.Small;
        theme = Theme.Light;
        confirmExit = false;
        observables = new LinkedList<>();
    }

    public void readFile() {
        try {
            File file = new File(PREF_FILE);
            if (file.exists()) {
                Scanner in = new Scanner(file);
                while (in.hasNextLine()) {
                    String currentLine = in.nextLine();
                    String[] arr = currentLine.split("=");
                    if (arr.length == 2) {
                        String prop = arr[0];
                        String value = arr[1];
                        switch (prop) {
                            case "company":
                                this.company = value;
                                break;
                            case "address":
                                this.address = value;
                            case "city":
                                this.city = value;
                                break;
                            case "state":
                                this.state = State.valueOfName(value);
                                break;
                            case "zip":
                                this.zip = value;
                                break;
                            case "phone":
                                this.phone = value;
                                break;
                            case "repair-shop-id":
                                this.repairShopId = value;
                                break;
                            case "title":
                                this.title = value;
                                break;
                            case "labor-rate":
                                try {
                                    Double laborRate = Double.parseDouble(value);
                                    this.laborRate = laborRate;
                                } catch (NumberFormatException e) {
                                    System.out.println("Failed to parse labor rate");
                                }
                                break;
                            case "tax-rate":
                                try {
                                    Double taxRate = Double.parseDouble(value);
                                    this.taxRate = taxRate;
                                } catch (NumberFormatException e) {
                                    System.out.println("Failed to parse tax rate");
                                }
                                break;
                            case "gui-scale":
                                try {
                                    this.guiScale = GUIScale.valueOf(value);
                                } catch (IllegalArgumentException e) {
                                    guiScale = GUIScale.Small;
                                }
                                break;
                            case "theme":
                                try {
                                    this.theme = Theme.valueOf(value);
                                } catch (IllegalArgumentException e) {
                                    theme = Theme.Light;
                                }
                                break;
                            case "confirm-exit":
                                try {
                                    confirmExit = Boolean.valueOf(value);
                                } catch (IllegalArgumentException e) {
                                    confirmExit = true;
                                }
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            /* Assign the new values if they're not null */
            if (tempCompany != null) company = tempCompany;
            if (tempAddress != null) address = tempAddress;
            if (tempCity != null) city = tempCity;
            if (tempState != null) state = tempState;
            if (tempZip != null) zip = tempZip;
            if (tempPhone != null) phone = tempPhone;
            if (tempRepairShopId != null) repairShopId = tempRepairShopId;
            if (tempTitle != null) title = tempTitle;
            if (tempLaborRate != null) laborRate = tempLaborRate;
            if (tempTaxRate != null) taxRate = tempTaxRate;
            if (tempGuiScale != null) guiScale = tempGuiScale;
            if (tempTheme != null) theme = tempTheme;
            if (tempConfirmExit != null) confirmExit = tempConfirmExit;
            /* Save the instance variables */
            PrintWriter pw = new PrintWriter(PREF_FILE);
            pw.println("company=" + company);
            pw.println("address=" + address);
            pw.println("city=" + city);
            pw.println("state=" + state);
            pw.println("zip=" + zip);
            pw.println("phone=" + phone);
            pw.println("repair-shop-id=" + repairShopId);
            pw.println("title=" + title);
            pw.println("labor-rate=" + laborRate);
            pw.println("tax-rate=" + taxRate);
            pw.println("gui-scale=" + guiScale);
            pw.println("theme=" + theme);
            pw.println("confirm-exit=" + confirmExit);
            pw.close();
            System.out.println("Saved " + PREF_FILE);
            /* Update every observable */
            for (Observable o : observables) o.update();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            String styleClass = GUIScale.getStyleClass(this.guiScale);
            App.get().setScale(styleClass);
            App.get().setTheme(theme);
        }
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.tempCompany = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.tempAddress = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.tempCity = city;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.tempState = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.tempZip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.tempPhone = phone;
    }

    public String getRepairShopId() {
        return repairShopId;
    }

    public void setRepairShopId(String repairShopId) {
        this.tempRepairShopId = repairShopId;
    }

    public String getSpecialTitle() {
        return title;
    }

    public void setSpecialTitle(String title) {
        this.tempTitle = title;
    }

    public Double getLaborRate() {
        return laborRate;
    }

    public void setLaborRate(Double laborRate) {
        this.tempLaborRate = laborRate;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public String getTaxRatePrettyString() {
        return (taxRate * 100) + " %";
    }

    public void setTaxRate(Double taxRate) {
        this.tempTaxRate = taxRate;
    }

    public GUIScale getGuiScale() {
        return guiScale;
    }

    public void setGuiScale(GUIScale guiScale) {
        this.tempGuiScale = guiScale;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.tempTheme = theme;
    }

    public Boolean getConfirmExit() {
        return confirmExit;
    }

    public void setConfirmExit(Boolean confirmExit) {
        this.tempConfirmExit = confirmExit;
    }

    public void addObserver(Observable observable) {
        this.observables.add(observable);
    }

    public void removeObserver(Observable observable) {
        this.observables.remove(observable);
    }

    @Override
    public String toString() {
        return company + " " + address + " " + city + ", " + state + " " + zip +
                ",\n" + phone + ", " + repairShopId +
                ",\n" + title +
                ",\n" + laborRate + ", " + taxRate + ", " + guiScale + ", " + theme +
                "\nconfirm-exit=" + confirmExit;
    }
}
