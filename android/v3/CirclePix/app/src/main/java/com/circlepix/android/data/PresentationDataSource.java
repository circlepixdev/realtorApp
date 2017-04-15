package com.circlepix.android.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.circlepix.android.helpers.BitsHelper;
import com.circlepix.android.types.BackgroundMusicType;
import com.circlepix.android.types.ListingType;
import com.circlepix.android.types.NarrationType;
import com.circlepix.android.types.PhotographyType;
import com.circlepix.android.types.ThemeType;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;

public class PresentationDataSource {

	// Database fields
	private SQLiteDatabase database;
	private PresentationSqlHelper dbHelper;
	private String[] allColumns = {
			PresentationSqlHelper.COLUMN_ID,
			PresentationSqlHelper.COLUMN_NAME,
			PresentationSqlHelper.COLUMN_DESC,
			PresentationSqlHelper.COLUMN_GUID,
			PresentationSqlHelper.COLUMN_LISTINGTYPE,
			PresentationSqlHelper.COLUMN_AUTOPLAY,
			PresentationSqlHelper.COLUMN_PHOTOGTYPE,
			PresentationSqlHelper.COLUMN_MEDIA_MASK,
			PresentationSqlHelper.COLUMN_EXP_MASK,
			PresentationSqlHelper.COLUMN_LEAD_MASK,
			PresentationSqlHelper.COLUMN_COMM_MASK,
			PresentationSqlHelper.COLUMN_COMPANY_LOGO,
			PresentationSqlHelper.COLUMN_COMPANY_NAME,
			PresentationSqlHelper.COLUMN_AGENT_PHOTO,
			PresentationSqlHelper.COLUMN_AGENT_PHONE,
			PresentationSqlHelper.COLUMN_PROP_IMAGE,
			PresentationSqlHelper.COLUMN_PROP_ADDRESS,
			PresentationSqlHelper.COLUMN_SETTINGS_MASK,
			PresentationSqlHelper.COLUMN_BGMUSIC_TYPE,
			PresentationSqlHelper.COLUMN_NARRATION_TYPE,
			PresentationSqlHelper.COLUMN_THEME_TYPE,
			PresentationSqlHelper.COLUMN_CREATED,
			PresentationSqlHelper.COLUMN_MODIFIED
	};

	public PresentationDataSource(Context context) {
		dbHelper = new PresentationSqlHelper(context);
	}

	public void open(boolean writable) throws SQLException {
		if (writable) {
			database = dbHelper.getWritableDatabase();
		} else {
			database = dbHelper.getReadableDatabase();
		}
	}

	public void close() {
		dbHelper.close();
	}

	public Presentation createPresentation(Presentation presentation) {

		ContentValues values = new ContentValues();
		values.put(PresentationSqlHelper.COLUMN_NAME, presentation.getName());
		values.put(PresentationSqlHelper.COLUMN_DESC, presentation.getDescription());
		values.put(PresentationSqlHelper.COLUMN_GUID, presentation.getGuid());
		values.put(PresentationSqlHelper.COLUMN_LISTINGTYPE, presentation.getListingType().ordinal());
		values.put(PresentationSqlHelper.COLUMN_AUTOPLAY, presentation.isAutoplay());
		values.put(PresentationSqlHelper.COLUMN_PHOTOGTYPE, presentation.getPhotographyType().ordinal());
		values.put(PresentationSqlHelper.COLUMN_MEDIA_MASK, makeMediaMask(presentation));
		values.put(PresentationSqlHelper.COLUMN_EXP_MASK, makeExposureMask(presentation));
		values.put(PresentationSqlHelper.COLUMN_LEAD_MASK, makeLeadMask(presentation));
		values.put(PresentationSqlHelper.COLUMN_COMM_MASK, makeCommunicationsMask(presentation));
		values.put(PresentationSqlHelper.COLUMN_COMPANY_LOGO, presentation.getCompanyLogo());
		values.put(PresentationSqlHelper.COLUMN_COMPANY_NAME, presentation.getCompanyName());
		values.put(PresentationSqlHelper.COLUMN_AGENT_PHOTO, presentation.getAgentPhoto());
		values.put(PresentationSqlHelper.COLUMN_AGENT_PHONE, presentation.getAgentPhoneNum());
		values.put(PresentationSqlHelper.COLUMN_PROP_IMAGE, presentation.getPropertyImage());
		values.put(PresentationSqlHelper.COLUMN_PROP_ADDRESS, presentation.getPropertyAddress());
		values.put(PresentationSqlHelper.COLUMN_SETTINGS_MASK, makeOtherSettingsMask(presentation));
		values.put(PresentationSqlHelper.COLUMN_BGMUSIC_TYPE, presentation.getMusic().ordinal());
		values.put(PresentationSqlHelper.COLUMN_NARRATION_TYPE, presentation.getNarration().ordinal());
		values.put(PresentationSqlHelper.COLUMN_THEME_TYPE, presentation.getTheme().ordinal());
		values.put(PresentationSqlHelper.COLUMN_CREATED, presentation.getCreated().getTime());
		values.put(PresentationSqlHelper.COLUMN_MODIFIED, presentation.getModified().getTime());

		long insertId = database.insert(PresentationSqlHelper.TABLE_PRESENTATION, null, values);
		Cursor cursor = database.query(PresentationSqlHelper.TABLE_PRESENTATION,
				allColumns, PresentationSqlHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Presentation newPresentation = cursorToPresentation(cursor);
		cursor.close();
		return newPresentation;
	}

	public void updatePresentation(Presentation presentation) throws Exception {

		ContentValues values = new ContentValues();
		values.put(PresentationSqlHelper.COLUMN_NAME, presentation.getName());
		values.put(PresentationSqlHelper.COLUMN_DESC, presentation.getDescription());
		values.put(PresentationSqlHelper.COLUMN_GUID, presentation.getGuid());
		values.put(PresentationSqlHelper.COLUMN_LISTINGTYPE, presentation.getListingType().ordinal());
		values.put(PresentationSqlHelper.COLUMN_AUTOPLAY, presentation.isAutoplay());
		values.put(PresentationSqlHelper.COLUMN_PHOTOGTYPE, presentation.getPhotographyType().ordinal());
		values.put(PresentationSqlHelper.COLUMN_MEDIA_MASK, makeMediaMask(presentation));
		values.put(PresentationSqlHelper.COLUMN_EXP_MASK, makeExposureMask(presentation));
		values.put(PresentationSqlHelper.COLUMN_LEAD_MASK, makeLeadMask(presentation));
		values.put(PresentationSqlHelper.COLUMN_COMM_MASK, makeCommunicationsMask(presentation));
		values.put(PresentationSqlHelper.COLUMN_COMPANY_LOGO, presentation.getCompanyLogo());
		values.put(PresentationSqlHelper.COLUMN_COMPANY_NAME, presentation.getCompanyName());
		values.put(PresentationSqlHelper.COLUMN_AGENT_PHOTO, presentation.getAgentPhoto());
		values.put(PresentationSqlHelper.COLUMN_AGENT_PHONE, presentation.getAgentPhoneNum());
		values.put(PresentationSqlHelper.COLUMN_PROP_IMAGE, presentation.getPropertyImage());
		values.put(PresentationSqlHelper.COLUMN_PROP_ADDRESS, presentation.getPropertyAddress());
		values.put(PresentationSqlHelper.COLUMN_SETTINGS_MASK, makeOtherSettingsMask(presentation));
		values.put(PresentationSqlHelper.COLUMN_BGMUSIC_TYPE, presentation.getMusic().ordinal());
		values.put(PresentationSqlHelper.COLUMN_NARRATION_TYPE, presentation.getNarration().ordinal());
		values.put(PresentationSqlHelper.COLUMN_THEME_TYPE, presentation.getTheme().ordinal());
		values.put(PresentationSqlHelper.COLUMN_CREATED, presentation.getCreated().getTime());
		values.put(PresentationSqlHelper.COLUMN_MODIFIED, presentation.getModified().getTime());

		int numUpdates = database.update(PresentationSqlHelper.TABLE_PRESENTATION, values, dbHelper.makeWhereClause(presentation.getId()), null);
		if (numUpdates < 1)
			throw new Exception("The changes were not saved. Please try again.");
	}

	public void deletePresentation(Presentation presentation) {
		long id = presentation.getId();
		System.out.println("Presentation deleted with id: " + id);
		database.delete(PresentationSqlHelper.TABLE_PRESENTATION, PresentationSqlHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Presentation> getAllPresentations() {
		List<Presentation> presentations = new ArrayList<Presentation>();

		Cursor cursor = database.query(PresentationSqlHelper.TABLE_PRESENTATION,
				allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Presentation presentation = cursorToPresentation(cursor);
			presentations.add(presentation);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return presentations;
	}

	public Presentation fetch(long presentationId) {
		Cursor cursor = dbHelper.fetchData(presentationId);
		cursor.moveToFirst();
		Presentation presentation = cursorToPresentation(cursor);
		cursor.close();
		return presentation;
	}

	private Presentation cursorToPresentation(Cursor cursor) {

		int i = 0;
		Presentation presentation = new Presentation();
		presentation.setId(cursor.getLong(i++));
		presentation.setName(cursor.getString(i++));
		presentation.setDescription(cursor.getString(i++));
		presentation.setGuid(cursor.getString(i++));
		presentation.setListingType(ListingType.getByOrdinal(cursor.getInt(i++)));
		presentation.setAutoplay(cursor.getInt(i++) != 0);
		presentation.setPhotographyType(PhotographyType.getByOrdinal(cursor.getInt(i++)));
		BitSet mediaMask = BitsHelper.convert(cursor.getLong(i++));
		presentation.setMediaPropertySite(mediaMask.get(1));
		presentation.setMediaListingVideo(mediaMask.get(2));
		presentation.setMediaQRCodes(mediaMask.get(3));
		presentation.setMedia24HourInfo(mediaMask.get(4));
		presentation.setMediaShortCode(mediaMask.get(5));
		presentation.setMediaFlyers(mediaMask.get(6));
		presentation.setMediaDvds(mediaMask.get(7));
		presentation.setMediaMobile(mediaMask.get(8));
		BitSet expMask = BitsHelper.convert(cursor.getLong(i++));
		presentation.setExpRealPortals(expMask.get(1));
		presentation.setExpPersonalSite(expMask.get(2));
		presentation.setExpCompanySite(expMask.get(3));
		presentation.setExpBlogger(expMask.get(4));
		presentation.setExpYouTube(expMask.get(5));
		presentation.setExpFacebook(expMask.get(6));
		presentation.setExpTwitter(expMask.get(7));
		presentation.setExpCraigslist(expMask.get(8));
		presentation.setExpLinkedin(expMask.get(9));
		presentation.setExpPinterest(expMask.get(10));
		presentation.setExpSeoBoost(expMask.get(11));
		BitSet leadMask = BitsHelper.convert(cursor.getLong(i++));
		presentation.setLeadPropertySite(leadMask.get(1));
		presentation.setLeadLeadBee(leadMask.get(2));
		presentation.setLeadFacebook(leadMask.get(3));
		presentation.setLeadMobile(leadMask.get(4));
		presentation.setLeadOpenHouseAnnce(leadMask.get(5));
		BitSet commMask = BitsHelper.convert(cursor.getLong(i++));
		presentation.setCommEmail(commMask.get(1));
		presentation.setCommBatchText(commMask.get(2));
		presentation.setCommStats(commMask.get(3));
		presentation.setCompanyLogo(cursor.getString(i++));
		presentation.setCompanyName(cursor.getString(i++));
		presentation.setAgentPhoto(cursor.getString(i++));
		presentation.setAgentPhoneNum(cursor.getString(i++));
		presentation.setPropertyImage(cursor.getString(i++));
		presentation.setPropertyAddress(cursor.getString(i++));
		BitSet otherMask = BitsHelper.convert(cursor.getLong(i++));
		presentation.setDisplayCompanyLogo(otherMask.get(1));
		presentation.setDisplayCompanyName(otherMask.get(2));
		presentation.setDisplayAgentImage(otherMask.get(3));
		presentation.setDisplayAgentName(otherMask.get(4));
		presentation.setDisplayPropImage(otherMask.get(5));
		presentation.setDisplayPropAddress(otherMask.get(6));
		presentation.setMusic(BackgroundMusicType.getByOrdinal(cursor.getInt(i++)));
		presentation.setNarration(NarrationType.getByOrdinal(cursor.getInt(i++)));
		presentation.setTheme(ThemeType.getByOrdinal(cursor.getInt(i++)));
		presentation.setCreated(new Date(cursor.getLong(i++)));
		presentation.setModified(new Date(cursor.getLong(i++)));

		return presentation;
	}

	private long makeMediaMask(Presentation presentation) {

		BitSet bs = new BitSet(16);
		if (presentation.isMediaPropertySite())
			bs.set(1);
		if (presentation.isMediaListingVideo())
			bs.set(2);
		if (presentation.isMediaQRCodes())
			bs.set(3);
		if (presentation.isMedia24HourInfo())
			bs.set(4);
		if (presentation.isMediaShortCode())
			bs.set(5);
		if (presentation.isMediaFlyers())
			bs.set(6);
		if (presentation.isMediaDvds())
			bs.set(7);
		if (presentation.isMediaMobile())
			bs.set(8);

		return BitsHelper.convert(bs);
	}

	private long makeExposureMask(Presentation presentation) {

		BitSet bs = new BitSet(16);
		if (presentation.isExpRealPortals())
			bs.set(1);
		if (presentation.isExpPersonalSite())
			bs.set(2);
		if (presentation.isExpCompanySite())
			bs.set(3);
		if (presentation.isExpBlogger())
			bs.set(4);
		if (presentation.isExpYouTube())
			bs.set(5);
		if (presentation.isExpFacebook())
			bs.set(6);
		if (presentation.isExpTwitter())
			bs.set(7);
		if (presentation.isExpCraigslist())
			bs.set(8);
		if (presentation.isExpLinkedin())
			bs.set(9);
		if (presentation.isExpPinterest())
			bs.set(10);
		if (presentation.isExpSeoBoost())
			bs.set(11);

		return BitsHelper.convert(bs);
	}

	private long makeLeadMask(Presentation presentation) {

		BitSet bs = new BitSet(16);
		if (presentation.isLeadPropertySite())
			bs.set(1);
		if (presentation.isLeadLeadBee())
			bs.set(2);
		if (presentation.isLeadFacebook())
			bs.set(3);
		if (presentation.isLeadMobile())
			bs.set(4);
		if (presentation.isLeadOpenHouseAnnce())
			bs.set(5);

		return BitsHelper.convert(bs);
	}

	private long makeCommunicationsMask(Presentation presentation) {

		BitSet bs = new BitSet(16);
		if (presentation.isCommEmail())
			bs.set(1);
		if (presentation.isCommBatchText())
			bs.set(2);
		if (presentation.isCommStats())
			bs.set(3);

		return BitsHelper.convert(bs);
	}

	private long makeOtherSettingsMask(Presentation presentation) {

		BitSet bs = new BitSet(16);
		if (presentation.isDisplayCompanyLogo())
			bs.set(1);
		if (presentation.isDisplayCompanyName())
			bs.set(2);
		if (presentation.isDisplayAgentImage())
			bs.set(3);
		if (presentation.isDisplayAgentName())
			bs.set(4);
		if (presentation.isDisplayPropImage())
			bs.set(5);
		if (presentation.isDisplayPropAddress())
			bs.set(6);

		return BitsHelper.convert(bs);
	}

}
