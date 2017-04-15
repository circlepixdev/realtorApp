package com.circlepix.android.beans;

import java.util.ArrayList;

/**
 * Created by Keuahn on 9/21/2016.
 */

public class ListingInformation {
    private String homeId = "";
    private String code = "";
    private String address = "";
    private String addressLine1 = "";
    private String addressLine2 = "";
    private String zipcode = "";
    private String city = "";
    private String county = "";
    private String state = "";
    private String propertyType = "";
    private String price = "";
    private String listingType = "";
    private String squareFootage = "";
    private String bedrooms = "";
    private String fullBaths = "";
    private String threeQuaterBaths = "";
    private String halfBaths = "";
    private String quarterBaths = "";
    private String mlsNum = "";
    private String altmlsNum = "";
    private String image = "";
    private ArrayList<ListingDescription> listingDesc = new ArrayList<>();
    private String comments = "";
    private String tourURL = "";
    private String socialMediaSites = "";
    private String realEstateSites = "";


    // Set up Getters & Setters
    public String getId() {
        return homeId;
    }
    public void setId(String id) {
        this.homeId = id;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressLine1() {
        return addressLine1;
    }
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
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

    public String getPropertyType() {
        return propertyType;
    }
    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public String getListingType() {
        return listingType;
    }
    public void setListingType(String listingType) {
        this.listingType = listingType;
    }

    public String getSquareFootage() {
        return squareFootage;
    }
    public void setSquareFootage(String squareFootage) {
        this.squareFootage = squareFootage;
    }

    public String getBedrooms() {
        return bedrooms;
    }
    public void setBedrooms(String bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getFullBaths() {
        return fullBaths;
    }
    public void setFullBaths(String fullBaths) {
        this.fullBaths = fullBaths;
    }

    public String getThreeQuaterBaths() {
        return threeQuaterBaths;
    }
    public void setThreeQuaterBaths(String threeQuaterBaths) {
        this.threeQuaterBaths = threeQuaterBaths;
    }

    public String getHalfBaths() {
        return halfBaths;
    }
    public void setHalfBaths(String halfBaths) {
        this.halfBaths = halfBaths;
    }

    public String getQuarterBaths() {
        return quarterBaths;
    }
    public void setQuarterBaths(String quarterBaths) {
        this.quarterBaths = quarterBaths;
    }

    public String getMlsNum() {
        return mlsNum;
    }
    public void setMlsNum(String mlsNum) {
        this.mlsNum = mlsNum;
    }

    public String getAltmlsNum() {
        return altmlsNum;
    }
    public void setAltmlsNum(String altmlsNum) {
        this.altmlsNum = altmlsNum;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }


    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTourURL() {
        return tourURL;
    }
    public void setTourURL(String tourURL) {
        this.tourURL = tourURL;
    }

    public String getSocialMediaSites() {
        return socialMediaSites;
    }
    public void setSocialMediaSites(String socialMediaSites) {
        this.socialMediaSites = socialMediaSites;
    }

    public String getRealEstateSites() {
        return realEstateSites;
    }
    public void setRealEstateSites(String realEstateSites) {
        this.realEstateSites = realEstateSites;
    }
  /*  public Boolean getSocialMediaSites() {
        return socialMediaSites;
    }
    public void setSocialMediaSites(Boolean socialMediaSites) {
        this.socialMediaSites = socialMediaSites;
    }

    public Boolean getRealEstateSites() {
        return realEstateSites;
    }
    public void setRealEstateSites(Boolean realEstateSites) {
        this.realEstateSites = realEstateSites;
    }*/

    public ArrayList<ListingDescription> getListingDesc() {
        return listingDesc;
    }
    public void setListingDesc(ArrayList<ListingDescription> listingDesc) {
        this.listingDesc = listingDesc;
    }


}
