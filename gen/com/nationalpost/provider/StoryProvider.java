package com.nationalpost.provider;

import java.util.Date;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.nationalpost.model.Story;

public class StoryProvider extends ContentProvider
{
	public StoryProvider() 
	{}
	
	public static final String AUTHORITY = "com.nationalpost.provider.Story";
	public static final String DATABASE_TABLE = "stories";
	public static final int ITEMS = 1;
	public static final int ITEM_ID = 2;

	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/stories" );
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.nationalpost.story";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.nationalpost.story";
	
	public static final String DEFAULT_SORT_ORDER = "modified DESC";
	
	private SQLiteDatabase m_database;

	private static final UriMatcher uriMatcher;

	static
	{
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		uriMatcher.addURI( AUTHORITY, StoryProvider.DATABASE_TABLE, StoryProvider.ITEMS );
		uriMatcher.addURI( AUTHORITY, StoryProvider.DATABASE_TABLE + "/#", StoryProvider.ITEM_ID );
	}
	
	public static class Columns implements BaseColumns
	{
		
		public static final String ABSTRACT = "abstract";
		
		public static final String ALLOWCOMMENTS = "allowcomments";
		
		public static final String BODY = "body";
		
		public static final String BYLINE = "byline";
		
		public static final String COPYRIGHT = "copyright";
		
		public static final String EDITION = "edition";
		
		public static final String ID = "id";
		
		public static final String SUBTITLE = "subtitle";
		
		public static final String TITLE = "title";
		
		public static final String URL = "url";
		
		public static final String VERSION = "version";
		
	}
	
	public static ContentValues getValues( Story _story )
	{
		ContentValues values = new ContentValues();
		
		
		values.put( StoryProvider.Columns.ABSTRACT, _story.getAbstract() );
		
		values.put( StoryProvider.Columns.ALLOWCOMMENTS, _story.getAllowComments().toString() );
		
		values.put( StoryProvider.Columns.BODY, _story.getBody() );
		
		values.put( StoryProvider.Columns.BYLINE, _story.getByline() );
		
		values.put( StoryProvider.Columns.COPYRIGHT, _story.getCopyright() );
		
		values.put( StoryProvider.Columns.EDITION, _story.getEdition().getTime() );
		
		values.put( StoryProvider.Columns.ID, _story.getID() );
		
		values.put( StoryProvider.Columns.SUBTITLE, _story.getSubTitle() );
		
		values.put( StoryProvider.Columns.TITLE, _story.getTitle() );
		
		values.put( StoryProvider.Columns.URL, _story.getURL() );
		
		values.put( StoryProvider.Columns.VERSION, _story.getVersion().getTime() );
		
		
		return values;
	}
	
	public static Story getFromCursor( Cursor c )
	{
		Story story = new Story();
		
		
		String _abstract = c.getString( c.getColumnIndex( StoryProvider.Columns.ABSTRACT ) );
		story.setAbstract( _abstract );
		
		String _allowcomments = c.getString( c.getColumnIndex( StoryProvider.Columns.ALLOWCOMMENTS ) );
		story.setAllowComments( Boolean.valueOf( _allowcomments ) );
		
		String _body = c.getString( c.getColumnIndex( StoryProvider.Columns.BODY ) );
		story.setBody( _body );
		
		String _byline = c.getString( c.getColumnIndex( StoryProvider.Columns.BYLINE ) );
		story.setByline( _byline );
		
		String _copyright = c.getString( c.getColumnIndex( StoryProvider.Columns.COPYRIGHT ) );
		story.setCopyright( _copyright );
		
		long _edition = c.getLong( c.getColumnIndex( StoryProvider.Columns.EDITION ) );
		story.setEdition( new Date( _edition ) );
		
		int _id = c.getInt( c.getColumnIndex( StoryProvider.Columns.ID ) );
		story.setID( _id );
		
		String _subtitle = c.getString( c.getColumnIndex( StoryProvider.Columns.SUBTITLE ) );
		story.setSubTitle( _subtitle );
		
		String _title = c.getString( c.getColumnIndex( StoryProvider.Columns.TITLE ) );
		story.setTitle( _title );
		
		String _url = c.getString( c.getColumnIndex( StoryProvider.Columns.URL ) );
		story.setURL( _url );
		
		long _version = c.getLong( c.getColumnIndex( StoryProvider.Columns.VERSION ) );
		story.setVersion( new Date( _version ) );
		
		
				
		//story.Images = c.getArraylist<image>( c.getColumnIndex( Story.Columns.IMAGES ) );
				
		//story.RelatedStories = c.getArraylist<story>( c.getColumnIndex( Story.Columns.RELATEDSTORIES ) );
				
		//story.Videos = c.getArraylist<video>( c.getColumnIndex( Story.Columns.VIDEOS ) );
		
		
		return story;
	}
	
	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs )
	{
		int count = 0;
		switch( uriMatcher.match( uri ) )
		{
			case StoryProvider.ITEMS:
				count = m_database.delete( StoryProvider.DATABASE_TABLE, selection, selectionArgs );
				break;
			case StoryProvider.ITEM_ID:
				String id = uri.getPathSegments().get( 1 );
				count = m_database.delete( StoryProvider.DATABASE_TABLE, StoryProvider.Columns._ID + " = " + id
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
				break;
			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );
		}
		getContext().getContentResolver().notifyChange( uri, null );
		return count;
	}

	@Override
	public String getType( Uri uri )
	{
		switch( uriMatcher.match( uri ) )
		{
			case StoryProvider.ITEMS:
				return StoryProvider.CONTENT_TYPE;
			case StoryProvider.ITEM_ID:
				return StoryProvider.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException( "Unsupported URI: " + uri );
		}
	}

	@Override
	public Uri insert( Uri uri, ContentValues values )
	{
		// set the created and modified times
		Long now = System.currentTimeMillis();
		values.put( StoryProvider.Columns.CREATED, now );
		values.put( StoryProvider.Columns.MODIFIED, now );
				
		long rowID = m_database.insert( StoryProvider.DATABASE_TABLE, "", values );
		if( rowID > 0 )
		{
			Uri _uri = ContentUris.withAppendedId( CONTENT_URI, rowID );
			getContext().getContentResolver().notifyChange( _uri, null );
			return _uri;
		}

		throw new UnsupportedOperationException( "Failled to insert: " + uri.toString() );
	}

	@Override
	public boolean onCreate()
	{
		Context context = getContext();
		SQLiteConnectionManager dbHelper = new SQLiteConnectionManager( context );
		m_database = dbHelper.getWritableDatabase();
		return ( m_database != null );
	}

	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder )
	{
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables( StoryProvider.DATABASE_TABLE );
		if( uriMatcher.match( uri ) == StoryProvider.ITEM_ID )
			sqlBuilder.appendWhere( Columns._ID + " = " + uri.getPathSegments().get( 1 ) );
		Cursor c = sqlBuilder.query( m_database, projection, selection, selectionArgs, null, null, sortOrder );
		c.setNotificationUri( getContext().getContentResolver(), uri );
		return c;
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs )
	{
		values.put( StoryProvider.Columns.MODIFIED, System.currentTimeMillis() );
		int count = 0;
		switch( uriMatcher.match( uri ) )
		{
			case StoryProvider.ITEMS:
				count = m_database.update( StoryProvider.DATABASE_TABLE, values, selection, selectionArgs );
				break;

			case StoryProvider.ITEM_ID:
				count = m_database.update( StoryProvider.DATABASE_TABLE, values, Columns._ID + " = " + uri.getPathSegments().get( 1 )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
				break;
			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );
		}
		getContext().getContentResolver().notifyChange( uri, null );

		return count;
	}
	
	public class SQLiteConnectionManager extends SQLiteOpenHelper
	{

		private static final String DATABASENAME = "todo.db";
		private static final int DATABASE_VERSION = 1;
		private static final String CREATE_TABLE = "CREATE TABLE stories( _id INTEGER NOT NULL CONSTRAINT USER_PK PRIMARY KEY AUTOINCREMENT, abstract Text, allowcomments Boolean, body Text, byline Text, copyright Text, edition Date, id Integer, subtitle Text, title Text, url Text, version Date )";

		public SQLiteConnectionManager( Context context )
		{
			super( context, DATABASENAME, null, DATABASE_VERSION );
		}
		
		@Override
		public void onCreate( SQLiteDatabase db )
		{
			db.execSQL( CREATE_TABLE );
			Log.d( "@G SQLConnectionFactory", " CREATE_LEADSOURCE Table " );
		}
		
		@Override
		public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
		{
		}
	}
	
}
