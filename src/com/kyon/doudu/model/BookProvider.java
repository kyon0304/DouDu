package com.kyon.doudu.model;

import java.util.HashMap;

import com.kyon.doudu.model.BookProviderMetaData.BookTableMetaData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class BookProvider extends ContentProvider {

	private static final String TAG = "BookProvider";
	
	private static HashMap<String, String> sBooksProjectionMap;
	/**
	 * maps from the user column names to the database column names
	 */
	static{
		sBooksProjectionMap = new HashMap<String, String>();
		sBooksProjectionMap.put(BookTableMetaData._ID, 
				BookTableMetaData._ID);
		
		sBooksProjectionMap.put(BookTableMetaData.BOOK_TITLE,
				BookTableMetaData.BOOK_TITLE);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_STITLE,
				BookTableMetaData.BOOK_STITLE);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_AUTHOR, 
				BookTableMetaData.BOOK_AUTHOR);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_TRANSLATOR,
				BookTableMetaData.BOOK_TRANSLATOR);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_ISBN,
				BookTableMetaData.BOOK_ISBN);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_RATE,
				BookTableMetaData.BOOK_RATE);		
		sBooksProjectionMap.put(BookTableMetaData.BOOK_PUBDATE,
				BookTableMetaData.BOOK_PUBDATE);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_COVERIMG,
				BookTableMetaData.BOOK_COVERIMG);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_PUBLISHER,
				BookTableMetaData.BOOK_PUBLISHER);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_PRICE,
				BookTableMetaData.BOOK_PRICE);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_PAGES,
				BookTableMetaData.BOOK_PAGES);
		
		sBooksProjectionMap.put(BookTableMetaData.CREATED_TIME, 
				BookTableMetaData.CREATED_TIME);
		sBooksProjectionMap.put(BookTableMetaData.MODIFIED_TIME, 
				BookTableMetaData.MODIFIED_TIME);
	}
	
	private static final UriMatcher sUriMatcher;
	private static final int INCOMING_BOOK_COLLECTION_URI_INDICATOR = 1;
	private static final int INCOMING_SINGLE_BOOK_URI_INDICATOR = 2;
	static{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "books", 
				INCOMING_BOOK_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "books/#", 
				INCOMING_SINGLE_BOOK_URI_INDICATOR);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, 
					BookProviderMetaData.DATABASE_NAME,
					null,
					BookProviderMetaData.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Auto-generated method stub
			Log.d(TAG, "inner oncreate called");
			db.execSQL("CREATE TABLE " + BookTableMetaData.TABLE_NAME + " ("
					+ BookTableMetaData._ID + " INTEGER PRIMARY KEY,"
					+ BookTableMetaData.BOOK_COVERIMG + " TEXT,"
					+ BookTableMetaData.BOOK_TITLE + " TEXT,"
					+ BookTableMetaData.BOOK_STITLE + " TEXT,"
					+ BookTableMetaData.BOOK_ISBN + " TEXT,"
					+ BookTableMetaData.BOOK_AUTHOR + " TEXT,"
					+ BookTableMetaData.BOOK_TRANSLATOR + " TEXT,"
					+ BookTableMetaData.BOOK_PUBLISHER + " TEXT,"
					+ BookTableMetaData.BOOK_PAGES + " INTEGER,"
					+ BookTableMetaData.BOOK_RATE + " REAL,"
					+ BookTableMetaData.BOOK_PUBDATE + " TEXT,"
					+ BookTableMetaData.BOOK_PRICE + " REAL,"
					+ BookTableMetaData.CREATED_TIME + " INTEGER,"
					+ BookTableMetaData.MODIFIED_TIME + " INTEGER"
					+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Auto-generated method stub
			//TODO: 有点原始，会丢弃整张表
			Log.d(TAG, "inner onupgrade called");
			Log.w(TAG, "Upgrading database from version "
					+ oldVersion + " to "
					+ newVersion + ", which will destroy all old data.");
			db.execSQL("DROP TABLE IF EXISTS "
					+ BookTableMetaData.TABLE_NAME);
			onCreate(db);
		}
	}
	
	private DatabaseHelper mOpenHelper;
	
	@Override
	public boolean onCreate() {
		// Auto-generated method stub
		Log.d(TAG, "main onCreate called");
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		// Auto-generated method stub
		switch (sUriMatcher.match(uri)) {
		case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
			return BookTableMetaData.CONTENT_TYPE;
		case INCOMING_SINGLE_BOOK_URI_INDICATOR:
			return BookTableMetaData.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}
	
	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		// Auto-generated method stub
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch(sUriMatcher.match(uri)){
		case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
			count = db.delete(BookTableMetaData.TABLE_NAME, whereClause, whereArgs);
			break;
		case INCOMING_SINGLE_BOOK_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			count = db.delete(BookTableMetaData.TABLE_NAME, 
					BookTableMetaData._ID + "=" + rowId
					+ (!TextUtils.isEmpty(whereClause) ? "AND (" + whereClause + ')' : ""),
					whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialvalues) {
		// Auto-generated method stub
		if(sUriMatcher.match(uri)
				!= INCOMING_SINGLE_BOOK_URI_INDICATOR) {
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		ContentValues values;
		if(initialvalues != null) {
			values = new ContentValues(initialvalues);
		} else {
			values = new ContentValues();
		}
		
		Long now = Long.valueOf(System.currentTimeMillis());
		
		if(values.containsKey(BookTableMetaData.CREATED_TIME) == false){
			values.put(BookTableMetaData.CREATED_TIME, now);
		}
		if(values.containsKey(BookTableMetaData.MODIFIED_TIME) == false) {
			values.put(BookTableMetaData.MODIFIED_TIME, now);
		}
		
		if(values.containsKey(BookTableMetaData.BOOK_COVERIMG) == false) {
			//TODO: path to default coverIMG
			values.put(BookTableMetaData.BOOK_COVERIMG, "NotSet");
		}
		if(values.containsKey(BookTableMetaData.BOOK_AUTHOR) == false) {
			throw new SQLException(
					"Failed to insert row because Book Author is needed " + uri);
		}
		if(values.containsKey(BookTableMetaData.BOOK_TRANSLATOR) == false) {
			values.put(BookTableMetaData.BOOK_TRANSLATOR, "NotSet");
		}		
		if(values.containsKey(BookTableMetaData.BOOK_ISBN) == false) {
			throw new SQLException(
					"Failed to insert row because Book Author is needed " + uri);
		}
		if(values.containsKey(BookTableMetaData.BOOK_TITLE) == false) {
			throw new SQLException(
					"Failed to insert row because Book Name is needed " + uri);
		}
		if(values.containsKey(BookTableMetaData.BOOK_STITLE) == false) {
			values.put(BookTableMetaData.BOOK_STITLE, "NotSet");
		}
		if(values.containsKey(BookTableMetaData.BOOK_PUBLISHER) == false) {
			values.put(BookTableMetaData.BOOK_PUBLISHER, "NotSet");
		}
		if(values.containsKey(BookTableMetaData.BOOK_PUBDATE) == false) {
			values.put(BookTableMetaData.BOOK_PUBDATE, "NotSet");
		}
		if(values.containsKey(BookTableMetaData.BOOK_PRICE) == false) {
			values.put(BookTableMetaData.BOOK_PRICE, -1.0);
		}
		if(values.containsKey(BookTableMetaData.BOOK_PAGES) == false) {
			values.put(BookTableMetaData.BOOK_PAGES, -1);
		}
		if(values.containsKey(BookTableMetaData.BOOK_RATE) == false) {
			values.put(BookTableMetaData.BOOK_RATE, -1);
		}
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(BookTableMetaData.TABLE_NAME,
				BookTableMetaData.BOOK_TITLE, values);
		if(rowId > 0) {
			Uri insertedBookUri = ContentUris.withAppendedId(
					BookTableMetaData.CONTENT_URI, rowId);
			getContext().getContentResolver()
				.notifyChange(insertedBookUri, null);
			return insertedBookUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch (sUriMatcher.match(uri)) {
		case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
			qb.setTables(BookTableMetaData.TABLE_NAME);
			qb.setProjectionMap(sBooksProjectionMap);
			break;
		case INCOMING_SINGLE_BOOK_URI_INDICATOR:
			qb.setTables(BookTableMetaData.TABLE_NAME);
			qb.setProjectionMap(sBooksProjectionMap);
			qb.appendWhere(BookTableMetaData._ID + "="
					+ uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = BookTableMetaData.DEFAULT_SORT_ODER;
		} else {
			orderBy = sortOrder;
		}
		
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		
		//int i = c.getCount(); example
		
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String whereClause,
			String[] whereArgs) {
		// Auto-generated method stub
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		int count;
		switch(sUriMatcher.match(uri)){
		case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
			count = db.update(BookTableMetaData.TABLE_NAME,
					values, whereClause, whereArgs);
			break;
		case INCOMING_SINGLE_BOOK_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			count = db.update(BookTableMetaData.TABLE_NAME, 
					values, BookTableMetaData._ID + "=" + rowId
					+ (!TextUtils.isEmpty(whereClause) ? "AND (" + whereClause + ')' : ""),
					whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
}