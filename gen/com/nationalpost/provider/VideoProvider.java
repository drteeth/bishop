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

import com.nationalpost.model.Video;

public class VideoProvider extends ContentProvider
{
	public VideoProvider() 
	{}
	
	public static final String AUTHORITY = "com.nationalpost.provider.Video";
	public static final String DATABASE_TABLE = "videos";
	public static final int ITEMS = 1;
	public static final int ITEM_ID = 2;

	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/videos" );
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.nationalpost.video";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.nationalpost.video";
	
	public static final String DEFAULT_SORT_ORDER = "modified DESC";
	
	private SQLiteDatabase m_database;

	private static final UriMatcher uriMatcher;

	static
	{
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		uriMatcher.addURI( AUTHORITY, VideoProvider.DATABASE_TABLE, VideoProvider.ITEMS );
		uriMatcher.addURI( AUTHORITY, VideoProvider.DATABASE_TABLE + "/#", VideoProvider.ITEM_ID );
	}
	
	public static class Columns implements BaseColumns
	{
		
		public static final String CAPTION = "caption";
		
		public static final String THUMBNAIL = "thumbnail";
		
		public static final String URL = "url";
		
	}
	
	public static ContentValues getValues( Video _video )
	{
		ContentValues values = new ContentValues();
		
		
		values.put( VideoProvider.Columns.CAPTION, _video.getCaption() );
		
		values.put( VideoProvider.Columns.THUMBNAIL, _video.getThumbnail() );
		
		values.put( VideoProvider.Columns.URL, _video.getURL() );
		
		
		return values;
	}
	
	public static Video getFromCursor( Cursor c )
	{
		Video video = new Video();
		
		
		String _caption = c.getString( c.getColumnIndex( VideoProvider.Columns.CAPTION ) );
		video.setCaption( _caption );
		
		String _thumbnail = c.getString( c.getColumnIndex( VideoProvider.Columns.THUMBNAIL ) );
		video.setThumbnail( _thumbnail );
		
		String _url = c.getString( c.getColumnIndex( VideoProvider.Columns.URL ) );
		video.setURL( _url );
		
		
		
		
		return video;
	}
	
	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs )
	{
		int count = 0;
		switch( uriMatcher.match( uri ) )
		{
			case VideoProvider.ITEMS:
				count = m_database.delete( VideoProvider.DATABASE_TABLE, selection, selectionArgs );
				break;
			case VideoProvider.ITEM_ID:
				String id = uri.getPathSegments().get( 1 );
				count = m_database.delete( VideoProvider.DATABASE_TABLE, VideoProvider.Columns._ID + " = " + id
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
			case VideoProvider.ITEMS:
				return VideoProvider.CONTENT_TYPE;
			case VideoProvider.ITEM_ID:
				return VideoProvider.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException( "Unsupported URI: " + uri );
		}
	}

	@Override
	public Uri insert( Uri uri, ContentValues values )
	{
		// set the created and modified times
		Long now = System.currentTimeMillis();
		values.put( VideoProvider.Columns.CREATED, now );
		values.put( VideoProvider.Columns.MODIFIED, now );
				
		long rowID = m_database.insert( VideoProvider.DATABASE_TABLE, "", values );
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
		sqlBuilder.setTables( VideoProvider.DATABASE_TABLE );
		if( uriMatcher.match( uri ) == VideoProvider.ITEM_ID )
			sqlBuilder.appendWhere( Columns._ID + " = " + uri.getPathSegments().get( 1 ) );
		Cursor c = sqlBuilder.query( m_database, projection, selection, selectionArgs, null, null, sortOrder );
		c.setNotificationUri( getContext().getContentResolver(), uri );
		return c;
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs )
	{
		values.put( VideoProvider.Columns.MODIFIED, System.currentTimeMillis() );
		int count = 0;
		switch( uriMatcher.match( uri ) )
		{
			case VideoProvider.ITEMS:
				count = m_database.update( VideoProvider.DATABASE_TABLE, values, selection, selectionArgs );
				break;

			case VideoProvider.ITEM_ID:
				count = m_database.update( VideoProvider.DATABASE_TABLE, values, Columns._ID + " = " + uri.getPathSegments().get( 1 )
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
		private static final String CREATE_TABLE = "CREATE TABLE videos( _id INTEGER NOT NULL CONSTRAINT USER_PK PRIMARY KEY AUTOINCREMENT, caption Text, thumbnail Text, url Text )";

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
