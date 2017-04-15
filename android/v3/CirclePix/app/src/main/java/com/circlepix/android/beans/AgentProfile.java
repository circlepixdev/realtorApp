package com.circlepix.android.beans;

import java.util.ArrayList;

/**
 * Created by keuahnlumanog on 11/12/2016.
 */

public class AgentProfile {
    // Agent Info
    private String id;
    private String firstName;
    private String lastName;
    private String agency;
    private String phoneNumber;
    private String cellNumber;
    private String cellProvider;
    private Boolean textNotifications;
    private String faxNumber;
    private String email;
    private String website;
    private String address;
    private String zipcode;
    private String city;
    private String county;
    private String state;
    private String office;
    private String leadBeePin;
    private String productNumber;
    private String billingType;
    private String stateLicenseNumber;

    // Agent Bio
    private String youtubeId;
    private String biography;

    // Social Media Links
    private ArrayList<SocialMediaLinks> socialMediaLinks;

    // Agent Logo
    private String agentLogo;


    // Set up Getters & Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAgency() {
        return agency;
    }
    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCellNumber() {
        return cellNumber;
    }
    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getCellProvider() {
        return cellProvider;
    }
    public void setCellProvider(String cellProvider) {
        this.cellProvider = cellProvider;
    }

    public Boolean getTextNotifications() {
        return textNotifications;
    }
    public void setTextNotifications(Boolean textNotifications) {
        this.textNotifications = textNotifications;
    }

    public String getFaxNumber() {
        return faxNumber;
    }
    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }
    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getOffice() {
        return office;
    }
    public void setOffice(String office) {
        this.office = office;
    }

    public String getLeadBeePin() {
        return leadBeePin;
    }
    public void setLeadBeePin(String leadBeePin) {
        this.leadBeePin = leadBeePin;
    }

    public String getProductNumber() {
        return productNumber;
    }
    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getBillingType() {
        return billingType;
    }
    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }

    public String getStateLicenseNumber() {
        return stateLicenseNumber;
    }
    public void setStateLicenseNumber(String stateLicenseNumber) {
        this.stateLicenseNumber = stateLicenseNumber;
    }

    public String getYoutubeId() {
        return youtubeId;
    }
    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getBiography() {
        return biography;
    }
    public void setBiography(String biography) {
        this.biography = biography;
    }

    public ArrayList<SocialMediaLinks> socialMediaLinks() {
        return socialMediaLinks;
    }
    public void setListingDesc(ArrayList<SocialMediaLinks> socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }

    public String getAgentLogo() {
        return agentLogo;
    }
    public void setAgentLogo(String agentLogo) {
        this.agentLogo = agentLogo;
    }


}
