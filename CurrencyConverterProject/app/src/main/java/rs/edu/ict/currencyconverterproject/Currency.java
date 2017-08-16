package rs.edu.ict.currencyconverterproject;

import java.io.Serializable;

/**
 * Created by David on 2017-01-24.
 */

public class Currency implements Serializable{
    private String currencyName;
    private double currencyValue;
    private String countryCode;
    private String countryName;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Currency() {
    }

    public Currency(String currencyName, double currencyValue) {
        this.currencyName = currencyName;
        this.currencyValue = currencyValue;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public double getCurrencyValue() {
        return currencyValue;
    }

    public void setCurrencyValue(double currencyValue) {
        this.currencyValue = currencyValue;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


    @Override
    public String toString() {
        return "Currency: '" + currencyName + '\'';
    }
}
