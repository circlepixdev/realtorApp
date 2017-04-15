package com.circlepix.android.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PresentationSqlHelper extends SQLiteOpenHelper {

	public static final String TABLE_PRESENTATION = "presentation";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESC = "description";
	public static final String COLUMN_GUID = "guid";
	public static final String COLUMN_LISTINGTYPE = "listing_type";
	public static final String COLUMN_AUTOPLAY = "autoplay";
	public static final String COLUMN_PHOTOGTYPE = "photog_type";
	public static final String COLUMN_MEDIA_MASK = "media_mask";
	public static final String COLUMN_EXP_MASK = "exp_mask";
	public static final String COLUMN_LEAD_MASK = "lead_mask";
	public static final String COLUMN_COMM_MASK = "comm_mask";
	public static final String COLUMN_COMPANY_LOGO = "company_logo";
	public static final String COLUMN_COMPANY_NAME = "company_name";
	public static final String COLUMN_AGENT_PHOTO = "agent_photo";
	public static final String COLUMN_AGENT_PHONE = "agent_phone";
	public static final String COLUMN_PROP_IMAGE = "prop_image";
	public static final String COLUMN_PROP_ADDRESS = "prop_address";
	public static final String COLUMN_SETTINGS_MASK = "settings_mask";
	public static final String COLUMN_BGMUSIC_TYPE = "bgmusic_type";
	public static final String COLUMN_NARRATION_TYPE = "narration_type";
	public static final String COLUMN_THEME_TYPE = "theme_type";
	public static final String COLUMN_CREATED = "created";
	public static final String COLUMN_MODIFIED = "modified";

	private static final String DATABASE_NAME = "presentations.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PRESENTATION + "( "
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_DESC + " text not null, "
			+ COLUMN_GUID + " text not null, "
			+ COLUMN_LISTINGTYPE + " integer not null, "
			+ COLUMN_AUTOPLAY + " integer not null, "
			+ COLUMN_PHOTOGTYPE + " integer not null, "
			+ COLUMN_MEDIA_MASK + " BIGINT not null, "
			+ COLUMN_EXP_MASK + " BIGINT not null, "
			+ COLUMN_LEAD_MASK + " BIGINT not null, "
			+ COLUMN_COMM_MASK + " BIGINT not null, "
			+ COLUMN_COMPANY_LOGO + " text not null, "
			+ COLUMN_COMPANY_NAME + " text not null, "
			+ COLUMN_AGENT_PHOTO + " text not null, "
			+ COLUMN_AGENT_PHONE + " text not null, "
			+ COLUMN_PROP_IMAGE + " text not null, "
			+ COLUMN_PROP_ADDRESS + " text not null, "
			+ COLUMN_SETTINGS_MASK + " BIGINT not null, "
			+ COLUMN_BGMUSIC_TYPE + " integer not null, "
			+ COLUMN_NARRATION_TYPE + " integer not null, "
			+ COLUMN_THEME_TYPE + " integer not null, "
			+ COLUMN_CREATED + " BIGINT not null, "
			+ COLUMN_MODIFIED + " BIGINT not null "
			+ ");";
	private static final String SELECT_ROW = "select "
			+ COLUMN_ID + ", "
			+ COLUMN_NAME + ", "
			+ COLUMN_DESC + ", "
			+ COLUMN_GUID + ", "
			+ COLUMN_LISTINGTYPE + ", "
			+ COLUMN_AUTOPLAY + ", "
			+ COLUMN_PHOTOGTYPE + ", "
			+ COLUMN_MEDIA_MASK + ", "
			+ COLUMN_EXP_MASK + ", "
			+ COLUMN_LEAD_MASK + ", "
			+ COLUMN_COMM_MASK + ", "
			+ COLUMN_COMPANY_LOGO + ", "
			+ COLUMN_COMPANY_NAME + ", "
			+ COLUMN_AGENT_PHOTO + ", "
			+ COLUMN_AGENT_PHONE + ", "
			+ COLUMN_PROP_IMAGE + ", "
			+ COLUMN_PROP_ADDRESS + ", "
			+ COLUMN_SETTINGS_MASK + ", "
			+ COLUMN_BGMUSIC_TYPE + ", "
			+ COLUMN_NARRATION_TYPE + ", "
			+ COLUMN_THEME_TYPE + ", "
			+ COLUMN_CREATED + ", "
			+ COLUMN_MODIFIED
			+ " from "
			+ TABLE_PRESENTATION;


	public PresentationSqlHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PresentationSqlHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESENTATION);
		onCreate(db);
	}

	public Cursor fetchData(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = SELECT_ROW + " where " + makeWhereClause(id);
		Cursor res = db.rawQuery(sql, null);
		return res;
	}

	public String makeWhereClause(long id) {
		return String.format(COLUMN_ID + "=%d", id);
	}

}
