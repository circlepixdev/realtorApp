package com.circlepix.android.data;

import android.util.Log;

import com.circlepix.android.types.BackgroundMusicType;
import com.circlepix.android.types.ListingType;
import com.circlepix.android.types.NarrationType;
import com.circlepix.android.types.PhotographyType;
import com.circlepix.android.types.ThemeType;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;

public class Presentation {

	private long id;
	private String name = "New Presentation";
	private String description = "";
	private String guid = UUID.randomUUID().toString();
	private Date created = new Date();
	private Date modified = new Date();
	private ListingType listingType = ListingType.residential;
	private boolean autoplay = true;
	private PhotographyType photographyType = PhotographyType.professional;

	private boolean mediaPropertySite = true;
	private boolean mediaListingVideo = true;
	private boolean mediaQRCodes = true;
	private boolean media24HourInfo = true;
	private boolean mediaShortCode = true;
	private boolean mediaFlyers = true;
	private boolean mediaDvds = true;
	private boolean mediaMobile = true;

	private boolean expRealPortals = true;
	private boolean expPersonalSite = true;
	private boolean expCompanySite = true;
	private boolean expBlogger = true;
	private boolean expYouTube = true;
	private boolean expFacebook = true;
	private boolean expTwitter = true;
	private boolean expCraigslist = true;
	private boolean expLinkedin = true;
	private boolean expPinterest = true;
	private boolean expSeoBoost = true;

	private boolean leadPropertySite = true;
	private boolean leadLeadBee = true;
	private boolean leadFacebook = true;
	private boolean leadMobile = true;
	private boolean leadOpenHouseAnnce = true;

	private boolean commEmail = true;
	private boolean commBatchText = true;
	private boolean commStats = true;

	private String companyLogo = "";
	private String companyName = "";
	private String agentPhoto = "";
	private String agentPhoneNum = "";

	private boolean displayCompanyLogo = true;
	private boolean displayCompanyName = true;
	private boolean displayAgentImage = true;
	private boolean displayAgentName = true;
	private boolean displayPropAddress = false;
	private boolean displayPropImage = false;
	private String propertyAddress = "";
	private String propertyImage = "";

	private BackgroundMusicType music = BackgroundMusicType.none;
	private NarrationType narration = NarrationType.male;
	private ThemeType theme = ThemeType.circlepix;

	private boolean selected = false;

	public Presentation() {}

	public Presentation(String name, String desc) {
		this.name = name;
		this.description = desc;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public ListingType getListingType() {
		return listingType;
	}

	public void setListingType(ListingType listingType) {
		this.listingType = listingType;
	}

	public boolean isDisplayPropImage() {
		return displayPropImage;
	}

	public void setDisplayPropImage(boolean displayPropImage) {
		this.displayPropImage = displayPropImage;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}

	public String getPropertyImage() {
		return propertyImage;
	}

	public void setPropertyImage(String propertyImage) {
		this.propertyImage = propertyImage;
	}

	public boolean isAutoplay() {
		return autoplay;
	}

	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
	}

	public PhotographyType getPhotographyType() {
		return photographyType;
	}

	public void setPhotographyType(PhotographyType photographyType) {
		this.photographyType = photographyType;
	}

	public boolean isMediaPropertySite() {
		return mediaPropertySite;
	}

	public void setMediaPropertySite(boolean mediaPropertySite) {
		this.mediaPropertySite = mediaPropertySite;
	}

	public boolean isMediaListingVideo() {
		return mediaListingVideo;
	}

	public void setMediaListingVideo(boolean mediaListingVideo) {
		this.mediaListingVideo = mediaListingVideo;
	}

	public boolean isMediaQRCodes() {
		return mediaQRCodes;
	}

	public void setMediaQRCodes(boolean mediaQRCodes) {
		this.mediaQRCodes = mediaQRCodes;
	}

	public boolean isMedia24HourInfo() {
		return media24HourInfo;
	}

	public void setMedia24HourInfo(boolean media24HourInfo) {
		this.media24HourInfo = media24HourInfo;
	}

	public boolean isMediaShortCode() {
		return mediaShortCode;
	}

	public void setMediaShortCode(boolean mediaShortCode) {
		this.mediaShortCode = mediaShortCode;
	}

	public boolean isMediaFlyers() {
		return mediaFlyers;
	}

	public void setMediaFlyers(boolean mediaFlyers) {
		this.mediaFlyers = mediaFlyers;
	}

	public boolean isMediaDvds() {
		return mediaDvds;
	}

	public void setMediaDvds(boolean mediaDvds) {
		this.mediaDvds = mediaDvds;
	}

	public boolean isMediaMobile() {
		return mediaMobile;
	}

	public void setMediaMobile(boolean mediaMobile) {
		this.mediaMobile = mediaMobile;
	}

	public boolean isExpRealPortals() {
		return expRealPortals;
	}

	public void setExpRealPortals(boolean expRealPortals) {
		this.expRealPortals = expRealPortals;
	}

	public boolean isExpPersonalSite() {
		return expPersonalSite;
	}

	public void setExpPersonalSite(boolean expPersonalSite) {
		this.expPersonalSite = expPersonalSite;
	}

	public boolean isExpCompanySite() {
		return expCompanySite;
	}

	public void setExpCompanySite(boolean expCompanySite) {
		this.expCompanySite = expCompanySite;
	}

	public boolean isExpBlogger() {
		return expBlogger;
	}

	public void setExpBlogger(boolean expBlogger) {
		this.expBlogger = expBlogger;
	}

	public boolean isExpYouTube() {
		return expYouTube;
	}

	public void setExpYouTube(boolean expYouTube) {
		this.expYouTube = expYouTube;
	}

	public boolean isExpFacebook() {
		return expFacebook;
	}

	public void setExpFacebook(boolean expFacebook) {
		this.expFacebook = expFacebook;
	}

	public boolean isExpTwitter() {
		return expTwitter;
	}

	public void setExpTwitter(boolean expTwitter) {
		this.expTwitter = expTwitter;
	}

	public boolean isExpCraigslist() {
		return expCraigslist;
	}

	public void setExpCraigslist(boolean expCraigslist) {
		this.expCraigslist = expCraigslist;
	}

	public boolean isExpLinkedin() {
		return expLinkedin;
	}

	public void setExpLinkedin(boolean expLinkedin) {
		this.expLinkedin = expLinkedin;
	}

	public boolean isExpPinterest() {
		return expPinterest;
	}

	public void setExpPinterest(boolean expPinterest) {
		this.expPinterest = expPinterest;
	}

	public boolean isExpSeoBoost() {
		return expSeoBoost;
	}

	public void setExpSeoBoost(boolean expSeoBoost) {
		this.expSeoBoost = expSeoBoost;
	}

	public boolean isLeadPropertySite() {
		return leadPropertySite;
	}

	public void setLeadPropertySite(boolean leadPropertySite) {
		this.leadPropertySite = leadPropertySite;
	}

	public boolean isLeadLeadBee() {
		return leadLeadBee;
	}

	public void setLeadLeadBee(boolean leadLeadBee) {
		this.leadLeadBee = leadLeadBee;
	}

	public boolean isLeadFacebook() {
		return leadFacebook;
	}

	public void setLeadFacebook(boolean leadFacebook) {
		this.leadFacebook = leadFacebook;
	}

	public boolean isLeadMobile() {
		return leadMobile;
	}

	public void setLeadMobile(boolean leadMobile) {
		this.leadMobile = leadMobile;
	}

	public boolean isLeadOpenHouseAnnce() {
		return leadOpenHouseAnnce;
	}

	public void setLeadOpenHouseAnnce(boolean leadOpenHouseAnnce) {
		this.leadOpenHouseAnnce = leadOpenHouseAnnce;
	}

	public boolean isCommEmail() {
		return commEmail;
	}

	public void setCommEmail(boolean commEmail) {
		this.commEmail = commEmail;
	}

	public boolean isCommBatchText() {
		return commBatchText;
	}

	public void setCommBatchText(boolean commBatchText) {
		this.commBatchText = commBatchText;
	}

	public boolean isCommStats() {
		return commStats;
	}

	public void setCommStats(boolean commStats) {
		this.commStats = commStats;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAgentPhoto() {
		return agentPhoto;
	}

	public void setAgentPhoto(String agentPhoto) {
		this.agentPhoto = agentPhoto;
	}

	public String getAgentPhoneNum() {
		return agentPhoneNum;
	}

	public void setAgentPhoneNum(String agentPhoneNum) {
		this.agentPhoneNum = agentPhoneNum;
	}

	public boolean isDisplayCompanyLogo() {
		return displayCompanyLogo;
	}

	public void setDisplayCompanyLogo(boolean displayCompanyLogo) {
		this.displayCompanyLogo = displayCompanyLogo;
	}

	public boolean isDisplayCompanyName() {
		return displayCompanyName;
	}

	public void setDisplayCompanyName(boolean displayCompanyName) {
		this.displayCompanyName = displayCompanyName;
	}

	public boolean isDisplayAgentImage() {
		return displayAgentImage;
	}

	public void setDisplayAgentImage(boolean displayAgentImage) {
		this.displayAgentImage = displayAgentImage;
	}

	public boolean isDisplayAgentName() {
		return displayAgentName;
	}

	public void setDisplayAgentName(boolean displayAgentName) {
		this.displayAgentName = displayAgentName;
	}

	public boolean isDisplayPropAddress() {
		return displayPropAddress;
	}

	public void setDisplayPropAddress(boolean displayPropAddress) {
		this.displayPropAddress = displayPropAddress;
	}

	public BackgroundMusicType getMusic() {
		return music;
	}

	public void setMusic(BackgroundMusicType music) {
		this.music = music;
	}

	public NarrationType getNarration() {
		return narration;
	}

	public void setNarration(NarrationType narration) {
		this.narration = narration;
	}

	public ThemeType getTheme() {
		return theme;
	}

	public void setTheme(ThemeType theme) {
		this.theme = theme;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public void setJsonData(String jsonString) throws JSONException, IllegalAccessException, IllegalArgumentException {
		// Get fields from json and save them
		JSONObject json = jsonString != null ?
				new JSONObject(jsonString) : new JSONObject();

		Field[] fields = Presentation.class.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if (json.has(fieldName)) {
				Object o = json.get(fieldName);
				if (o != null) {
					String val = o.toString();
					if (val.length() > 0) {
						if (field.getType() == String.class) {
							field.set(this, val);
						}
						else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
							field.set(this, Boolean.parseBoolean(val));
						}
						else if (field.getType() == BackgroundMusicType.class) {
							if (BackgroundMusicType.getValueByName(val) != null) {
								field.set(this, BackgroundMusicType.getValueByName(val));
							}
						}
						else if (field.getType() == ListingType.class) {
							if (ListingType.getValueByName(val) != null) {
								field.set(this, ListingType.getValueByName(val));
							}
						}
						else if (field.getType() == NarrationType.class) {
							if (NarrationType.getValueByName(val) != null) {
								field.set(this, NarrationType.getValueByName(val));
							}
						}
						else if (field.getType() == PhotographyType.class) {
							if (PhotographyType.getValueByName(val) != null) {
								field.set(this, PhotographyType.getValueByName(val));
							}
						}
						else if (field.getType() == ThemeType.class) {
							if (ThemeType.getValueByName(val) != null) {
								field.set(this, ThemeType.getValueByName(val));
							}
						}
						else {
							Log.d("Presentation", "Unhandled type: " + field.getType().toString());
						}
					}
				}
			}
		}
	}

	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
