package com.learn.csvToDBTable.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "countries")
public class CountryCSVModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "countryid")
    private UUID countryId;

    @Column(name = "currencycode")
    private String currencyCode;

    @Column(name = "currencysymbol")
    private String currencySymbol;

    @Column(name = "countryname")
    private String countryName;

    public UUID getCountryId() {
        return countryId;
    }

    public void setCountryId(UUID countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    // JPA needs a default constructor for it to work
    // https://www.baeldung.com/jpa-no-argument-constructor-entity-class#:~:text=This%20default%20constructor%20is%20used,and%20its%20newInstance()%20method.
    public CountryCSVModel() {
    }

    public CountryCSVModel(String currencyCode, String currencySymbol, String countryName) {
        this.currencyCode = currencyCode;
        this.currencySymbol = currencySymbol;
        this.countryName = countryName;
    }
}
