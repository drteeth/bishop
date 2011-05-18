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

import com.nationalpost.model.Image;

public class ImageProvider extends ContentProvider
{
	public ImageProvider() 
	{}
	
	public static final String AUTHORITY = "com.nationalpost.provider.Image";
	public static final String DATABASE_TABLE = "images";
	public static final int ITEMS = 1;
	public static final int ITEM_ID = 2;

	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/images" );
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.nationalpost.image";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.nationalpost.image";
	
	public static final String DEFAULT_SORT_ORDER = "modified DESC";
	
	private SQLiteDatabase m_database;

	private static final UriMatcher uriMatcher;

	static
	{
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		uriMatcher.addURI( AUTHORITY, ImageProvider.DATABASE_TABLE, ImageProvider.ITEMS );
		uriMatcher.addURI( AUTHORITY, ImageProvider.DATABASE_TABLE + "/#", ImageProvider.ITEM_ID );
	}
	
	public static class Columns implements BaseColumns
	{
		
		public static final String CAPTION = "caption";
		
		public static final String CREDIT = "credit";
		
		public static final String DISTRIBUTOR = "distributor";
		
		public static final String URL = "url";
		
	}
	
	public static ContentValues getValues( Image _image )
	{
		ContentValues values = new ContentValues();
		
		
		values.put( ImageProvider.Columns.CAPTION, _image.getCaption() );
		
		values.put( ImageProvider.Columns.CREDIT, _image.getCredit() );
		
		values.put( ImageProvider.Columns.DISTRIBUTOR, _image.getDistributor() );
		
		values.put( ImageProvider.Columns.URL, _image.getURL() );
		
		
		return values;
	}
	
	public static Image getFromCursor( Cursor c )
	{
		Image image = new Image();
		
		
		String _caption = c.getString( c.getColumnIndex( ImageProvider.Columns.CAPTION ) );
		image.setCaption( _caption );
		
		String _credit = c.getString( c.getColumnIndex( ImageProvider.Columns.CREDIT ) );
		image.setCredit( _credit );
		
		String _distributor = c.getString( c.getColumnIndex( ImageProvider.Columns.DISTRIBUTOR ) );
		image.setDistributor( _distributor );
		
		String _url = c.getString( c.getColumnIndex( ImageProvider.Columns.URL ) );
		image.setURL( _url );
		
		
		
		
		return image;
	}
	
	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs )
	{
		int count = 0;
		switch( uriMatcher.match( uri ) )
		{
			case ImageProvider.ITEMS:
				count = m_database.delete( ImageProvider.DATABASE_TABLE, selection, selectionArgs );
				break;
			case ImageProvider.ITEM_ID:
				String id = uri.getPathSegments().get( 1 );
				count = m_database.delete( ImageProvider.DATABASE_TABLE, ImageProvider.Columns._ID + " = " + id
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
			case ImageProvider.ITEMS:
				return ImageProvider.CONTENT_TYPE;
			case ImageProvider.ITEM_ID:
				return ImageProvider.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException( "Unsupported URI: " + uri );
		}
	}

	@Override
	public Uri insert( Uri uri, ContentValues values )
	{
		// set the created and modified times
		Long now = System.currentTimeMillis();
		values.put( ImageProvider.Columns.CREATED, now );
		values.put( ImageProvider.Columns.MODIFIED, now );
				
		long rowID = m_database.insert( ImageProvider.DATABASE_TABLE, "", values );
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
		sqlBuilder.setTables( ImageProvider.DATABASE_TABLE );
		if( uriMatcher.match( uri ) == ImageProvider.ITEM_ID )
			sqlBuilder.appendWhere( Columns._ID + " = " + uri.getPathSegments().get( 1 ) );
		Cursor c = sqlBuilder.query( m_database, projection, selection, selectionArgs, null, null, sortOrder );
		c.setNotificationUri( getContext().getContentResolver(), uri );
		return c;
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs )
	{
		values.put( ImageProvider.Columns.MODIFIED, System.currentTimeMillis() );
		int count = 0;
		switch( uriMatcher.match( uri ) )
		{
			case ImageProvider.ITEMS:
				count = m_database.update( ImageProvider.DATABASE_TABLE, values, selection, selectionArgs );
				break;

			case ImageProvider.ITEM_ID:
				count = m_database.update( ImageProvider.DATABASE_TABLE, values, Columns._ID + " = " + uri.getPathSegments().get( 1 )
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
		private static final String CREATE_TABLE = "CREATE TABLE images( _id INTEGER NOT NULL CONSTRAINT USER_PK PRIMARY KEY AUTOINCREMENT, caption Text, credit Text, distributor Text, url Text )";

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
