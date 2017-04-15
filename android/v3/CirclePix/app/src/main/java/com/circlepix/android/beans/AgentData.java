package com.circlepix.android.beans;


import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AgentData {

    private static AgentData instance;
    private Realtor realtor;
    private List<Listing> listings;
    private ArrayList<ListingInformation> listingInformation;
    private boolean offlineMode;
    private boolean isLoggedIn;

    private AgentData() {
    }

    public static AgentData getInstance() {
        if (instance == null) {
            instance = new AgentData();
        }
        return instance;
    }

    public Realtor getRealtor() {
        return realtor;
    }

    public void setRealtor(Realtor realtor) {
        this.realtor = realtor;
    }

    public List<Listing> getListings() {
        return listings;
    }

    public void setListings(List<Listing> listings) {
        this.listings = listings;
    }

    // new: created by KBL
    public ArrayList<ListingInformation> getListingInformation() {
        return listingInformation;
    }

    public void setListingInformation(ArrayList<ListingInformation> listingInformation) {
        this.listingInformation = listingInformation;
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public void parseResponseString(String responseString) {
        // Parse the API response into the agent object
        Document doc = Jsoup.parse(responseString);

        Elements realtors = doc.select("realtor");
        Elements realtorId = realtors.select("id");
        Elements realtorCode = realtors.select("code");
        Elements realtorName = realtors.select("name");
        Elements realtorVideo = realtors.select("currentyoutubevideo");
        Elements realtorImage = realtors.select("agentimage");
        Elements realtorAgency = realtors.select("agency");
        Elements realtorPhone = realtors.select("phone");
        Elements realtorMobile = realtors.select("mobile");
        Elements realtorEmail = realtors.select("email");
        Elements realtorLogo = realtors.select("agentLogo");

        Realtor realtor = new Realtor();
        realtor.setId(realtorId.text());
        realtor.setCode(realtorCode.text());
        realtor.setName(realtorName.text());
        realtor.setVideo((realtorVideo == null)?"":realtorVideo.text());
        if(realtorImage.text().contains("%2F")){
            realtor.setImage((realtorImage == null)?"":realtorImage.text().replace("%2F", "/"));
        }else{
            realtor.setImage((realtorImage == null)?"":realtorImage.text());
        }

        if(realtorLogo.text().contains("%2F")){
            realtor.setLogo((realtorLogo == null)?"":realtorLogo.text().replace("%2F", "/"));
        }else{
            realtor.setLogo((realtorLogo == null)?"":realtorLogo.text());
        }

        realtor.setAgency(realtorAgency.text());
        realtor.setEmail(realtorEmail.text());
        realtor.setPhone((realtorPhone == null)?"":realtorPhone.text());
        realtor.setMobile((realtorMobile == null)?"":realtorMobile.text());
        this.setRealtor(realtor);

        List<Listing> listings = new ArrayList<Listing>();
        if (!this.isOfflineMode()) {
            Elements elems = doc.select("listing");

            for (Element listing : elems) {
                Listing l = new Listing();
                l.setId(listing.select("listingid").text());
                l.setCode(listing.select("listingcode").text());
                l.setAddress1(listing.select("address1").text());
                l.setAddress2(listing.select("address2").text());
                l.setImage(listing.select("listingImage").text());
                listings.add(l);
            }
        }
        this.setListings(listings);
    }

}
