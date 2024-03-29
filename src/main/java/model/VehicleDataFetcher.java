package model;

import com.fasterxml.jackson.databind.JsonNode;
import model.work_order.Vehicle;

import java.io.IOException;

/**
 * Wrapper class for VINMaster. Fetches and used data required to create a Vehicle object.
 * @see VINMaster
 * @see Vehicle
 */
public class VehicleDataFetcher {
    private final JsonNode node;

    /**
     * Decodes the passed {VIN} and holds onto the result.
     * @param VIN To be decoded
     * @throws IOException If the connection to the API failed
     */
    public VehicleDataFetcher(final String VIN) throws IOException {
        VINMaster master = new VINMaster();
        this.node = master.decode(VIN);
    }

    /**
     * Fetches the vehicle info and fetches the data required to create a Vehicle object;
     * @return Vehicle object if node is not null, otherwise null
     */
    public Vehicle get() {
        String vin = node.get(VField.VIN.name()).asText();
        String year = node.get(VField.ModelYear.name()).asText();
        String make = Strings.toTitle(node.get(VField.Make.name()).asText());
        String model = node.get(VField.Model.name()).asText();
        String engine = node.get(VField.DisplacementL.name()).asText();
        /* Sometimes the engine has a leading decimal number, so we format it */
        if (!engine.isEmpty()) {
            try {
                double x = Double.parseDouble(engine);
                engine = String.format("%.1f%c", x, 'L');
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
        String cylCount = node.get(VField.EngineCylinders.name()).asText();
        if (!cylCount.isEmpty()) engine += " V" + cylCount;
        String fuelType = node.get(VField.FuelTypePrimary.name()).asText();
        if (!fuelType.isEmpty()) engine += ' ' + fuelType;
        String transmission = node.get(VField.TransmissionStyle.name()).asText();
        return new Vehicle(vin, year, make, model, "", "", engine, transmission);
    }

    /**
     * Checks if the data fetched is a success
     * @return True if fetch successful, otherwise false
     */
    public boolean isFetchSuccess() {
        if (node == null) return false;
        String errCode = node.get(VField.ErrorCode.name()).asText();
        return errCode.equals("0");
    }

    /**
     * Fetches error codes from the fetched JSON
     * @return Error codes delimited by a comma. If {node} is null, then returns "No Codes"
     */
    public String getErrorCodes() {
        if (node == null) return "No Codes";
        return node.get(VField.ErrorCode.name()).asText();
    }

    /**
     * Fetches the error text from the fetched JSON
     * @return Text explaining the error codes, if {node} is null, then returns "No Errors"
     */
    public String getErrorText() {
        if (node == null) return "No Errors";
        var errText = node.get(VField.ErrorText.name()).asText();
        var tokens = errText.split(";");
        StringBuilder text = new StringBuilder();
        for (var token : tokens) {
            text.append(token).append('\n');
        }
        return text.toString();
    }
}
