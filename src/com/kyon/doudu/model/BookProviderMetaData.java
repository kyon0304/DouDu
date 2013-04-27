package com.kyon.doudu.model;

import android.net.Uri;
import android.provider.BaseColumns;

public class BookProviderMetaData {
	public static final String AUTHORITY = "com.kyon.provider.BookProvider";
	
	public static final String DATABASE_NAME = "book.db";
	public static final int DATABASE_VERSION = 1;
	public static final String BOOKS_TABLE_NAME = "books";
	
	private BookProviderMetaData() {}
	public static final class BookTableMetaData implements BaseColumns {
	
		private BookTableMetaData() {}
		public static final String TABLE_NAME = "books";
		
		public static final Uri CONTENT_URI = 
				Uri.parse("content://" + AUTHORITY + "/books");
		public static final String CONTENT_TYPE = 
				"vnd.android.cursor.dir/vnd.kyondoudu.book";
		public static final String CONTENT_ITEM_TYPE =
				"vnd.android.cursor.item/vnd.kyondoudu.book";
		
		public static final String DEFAULT_SORT_ODER = "modified DESC";
		
		//string type
		public static final String BOOK_COVERIMG = "coverimg";
		//string type
		public static final String BOOK_TITLE = "title";
		//string type
		public static final String BOOK_STITLE = "subtitle";
		//string type
		public static final String BOOK_ISBN = "isbn";
		//string type
		public static final String BOOK_AUTHOR = "author";
		//string type
		public static final String BOOK_TRANSLATOR = "translator";
		//string type
		public static final String BOOK_PUBLISHER = "publisher";
		//string type
		public static final String BOOK_PUBDATE = "pubdate";
		//float type
		public static final String BOOK_RATE = "rate";
		//integer type
		public static final String BOOK_PAGES = "pages";
		//string type
		public static final String BOOK_PRICE = "price";
		
		//integer from System.currentTimeMillis()
		public static final String CREATED_TIME = "created";
		//integer from System.currentTimeMillis()
		public static final String MODIFIED_TIME = "modified";
	}
}