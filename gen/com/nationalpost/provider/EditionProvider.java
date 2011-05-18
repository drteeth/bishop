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

import com.nationalpost.model.Edition;

public class EditionProvider extends ContentProvider
{
	public EditionProvider() 
	{}
	
	public static final String AUTHORITY = "com.nationalpost.provider.Edition";
	public static final String DATABASE_TABLE = "editions";
	public static final int ITEMS = 1;
	public static final int ITEM_ID = 2;

	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/editions" );
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.nationalpost.edition";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.nationalpost.edition";
	
	public static final String DEFAULT_SORT_ORDER = "modified DESC";
	
	private SQLiteDatabase m_database;

	private static final UriMatcher uriMatcher;

	static
	{
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		uriMatcher.addURI( AUTHORITY, EditionProvider.DATABASE_TABLE, EditionProvider.ITEMS );
		uriMatcher.addURI( AUTHORITY, EditionProvider.DATABASE_TABLE + "/#", EditionProvider.ITEM_ID );
	}
	
	public static class Columns implements BaseColumns
	{
		
		public static final String TIMESTAMP = "timestamp";
		
		public static final String TITLE = "title";
		
	}
	
	public static ContentValues getValues( Edition _edition )
	{
		ContentValues values = new ContentValues();
		
		
		values.put( EditionProvider.Columns.TIMESTAMP, _edition.getTimestamp().getTime() );
		
		values.put( EditionProvider.Columns.TITLE, _edition.getTitle() );
		
		
		return values;
	}
	
	public static Edition getFromCursor( Cursor c )
	{
		Edition edition = new Edition();
		
		
		long _timestamp = c.getLong( c.getColumnIndex( EditionProvider.Columns.TIMESTAMP ) );
		edition.setTimestamp( new Date( _timestamp ) );
		
		String _title = c.getString( c.getColumnIndex( EditionProvider.Columns.TITLE ) );
		edition.setTitle( _title );
		
		
		
		
		return edition;
	}
	
	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs )
	{
		int count = 0;
		switch( uriMatcher.match( uri ) )
		{
			case EditionProvider.ITEMS:
				count = m_database.delete( EditionProvider.DATABASE_TABLE, selection, selectionArgs );
				break;
			case EditionProvider.ITEM_ID:
				String id = uri.getPathSegments().get( 1 );
				count = m_database.delete( EditionProvider.DATABASE_TABLE, EditionProvider.Columns._ID + " = " + id
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
			case EditionProvider.ITEMS:
				return EditionProvider.CONTENT_TYPE;
			case EditionProvider.ITEM_ID:
				return EditionProvider.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException( "Unsupported URI: " + uri );
		}
	}

	@Override
	public Uri insert( Uri uri, ContentValues values )
	{
		// set the created and modified times
		Long now = System.currentTimeMillis();
		values.put( EditionProvider.Columns.CREATED, now );
		values.put( EditionProvider.Columns.MODIFIED, now );
				
		long rowID = m_database.insert( EditionProvider.DATABASE_TABLE, "", values );
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
		sqlBuilder.setTables( EditionProvider.DATABASE_TABLE );
		if( uriMatcher.match( uri ) == EditionProvider.ITEM_ID )
			sqlBuilder.appendWhere( Columns._ID + " = " + uri.getPathSegments().get( 1 ) );
		Cursor c = sqlBuilder.query( m_database, projection, selection, selectionArgs, null, null, sortOrder );
		c.setNotificationUri( getContext().getContentResolver(), uri );
		return c;
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs )
	{
		values.put( EditionProvider.Columns.MODIFIED, System.currentTimeMillis() );
		int count = 0;
		switch( uriMatcher.match( uri ) )
		{
			case EditionProvider.ITEMS:
				count = m_database.update( EditionProvider.DATABASE_TABLE, values, selection, selectionArgs );
				break;

			case EditionProvider.ITEM_ID:
				count = m_database.update( EditionProvider.DATABASE_TABLE, values, Columns._ID + " = " + uri.getPathSegments().get( 1 )
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
		private static final String CREATE_TABLE = "CREATE TABLE editions( _id INTEGER NOT NULL CONSTRAINT USER_PK PRIMARY KEY AUTOINCREMENT, timestamp Date, title Text )";

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
