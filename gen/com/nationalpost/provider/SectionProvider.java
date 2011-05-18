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

import com.nationalpost.model.Section;

public class SectionProvider extends ContentProvider
{
	public SectionProvider() 
	{}
	
	public static final String AUTHORITY = "com.nationalpost.provider.Section";
	public static final String DATABASE_TABLE = "sections";
	public static final int ITEMS = 1;
	public static final int ITEM_ID = 2;

	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/sections" );
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.nationalpost.section";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.nationalpost.section";
	
	public static final String DEFAULT_SORT_ORDER = "modified DESC";
	
	private SQLiteDatabase m_database;

	private static final UriMatcher uriMatcher;

	static
	{
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		uriMatcher.addURI( AUTHORITY, SectionProvider.DATABASE_TABLE, SectionProvider.ITEMS );
		uriMatcher.addURI( AUTHORITY, SectionProvider.DATABASE_TABLE + "/#", SectionProvider.ITEM_ID );
	}
	
	public static class Columns implements BaseColumns
	{
		
		public static final String ADCONTENTKEY = "adcontentkey";
		
		public static final String ID = "id";
		
		public static final String MOBILETEMPLATE = "mobiletemplate";
		
		public static final String NAME = "name";
		
		public static final String TYPE = "type";
		
	}
	
	public static ContentValues getValues( Section _section )
	{
		ContentValues values = new ContentValues();
		
		
		values.put( SectionProvider.Columns.ADCONTENTKEY, _section.getAdContentKey() );
		
		values.put( SectionProvider.Columns.ID, _section.getID() );
		
		values.put( SectionProvider.Columns.MOBILETEMPLATE, _section.getMobileTemplate() );
		
		values.put( SectionProvider.Columns.NAME, _section.getName() );
		
		values.put( SectionProvider.Columns.TYPE, _section.getType() );
		
		
		return values;
	}
	
	public static Section getFromCursor( Cursor c )
	{
		Section section = new Section();
		
		
		String _adcontentkey = c.getString( c.getColumnIndex( SectionProvider.Columns.ADCONTENTKEY ) );
		section.setAdContentKey( _adcontentkey );
		
		int _id = c.getInt( c.getColumnIndex( SectionProvider.Columns.ID ) );
		section.setID( _id );
		
		String _mobiletemplate = c.getString( c.getColumnIndex( SectionProvider.Columns.MOBILETEMPLATE ) );
		section.setMobileTemplate( _mobiletemplate );
		
		String _name = c.getString( c.getColumnIndex( SectionProvider.Columns.NAME ) );
		section.setName( _name );
		
		String _type = c.getString( c.getColumnIndex( SectionProvider.Columns.TYPE ) );
		section.setType( _type );
		
		
				
		//section.Galleries = c.getArraylist<gallery>( c.getColumnIndex( Section.Columns.GALLERIES ) );
				
		//section.Stories = c.getArraylist<story>( c.getColumnIndex( Section.Columns.STORIES ) );
		
		
		return section;
	}
	
	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs )
	{
		int count = 0;
		switch( uriMatcher.match( uri ) )
		{
			case SectionProvider.ITEMS:
				count = m_database.delete( SectionProvider.DATABASE_TABLE, selection, selectionArgs );
				break;
			case SectionProvider.ITEM_ID:
				String id = uri.getPathSegments().get( 1 );
				count = m_database.delete( SectionProvider.DATABASE_TABLE, SectionProvider.Columns._ID + " = " + id
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
			case SectionProvider.ITEMS:
				return SectionProvider.CONTENT_TYPE;
			case SectionProvider.ITEM_ID:
				return SectionProvider.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException( "Unsupported URI: " + uri );
		}
	}

	@Override
	public Uri insert( Uri uri, ContentValues values )
	{
		// set the created and modified times
		Long now = System.currentTimeMillis();
		values.put( SectionProvider.Columns.CREATED, now );
		values.put( SectionProvider.Columns.MODIFIED, now );
				
		long rowID = m_database.insert( SectionProvider.DATABASE_TABLE, "", values );
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
		sqlBuilder.setTables( SectionProvider.DATABASE_TABLE );
		if( uriMatcher.match( uri ) == SectionProvider.ITEM_ID )
			sqlBuilder.appendWhere( Columns._ID + " = " + uri.getPathSegments().get( 1 ) );
		Cursor c = sqlBuilder.query( m_database, projection, selection, selectionArgs, null, null, sortOrder );
		c.setNotificationUri( getContext().getContentResolver(), uri );
		return c;
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs )
	{
		values.put( SectionProvider.Columns.MODIFIED, System.currentTimeMillis() );
		int count = 0;
		switch( uriMatcher.match( uri ) )
		{
			case SectionProvider.ITEMS:
				count = m_database.update( SectionProvider.DATABASE_TABLE, values, selection, selectionArgs );
				break;

			case SectionProvider.ITEM_ID:
				count = m_database.update( SectionProvider.DATABASE_TABLE, values, Columns._ID + " = " + uri.getPathSegments().get( 1 )
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
		private static final String CREATE_TABLE = "CREATE TABLE sections( _id INTEGER NOT NULL CONSTRAINT USER_PK PRIMARY KEY AUTOINCREMENT, adcontentkey Text, id Integer, mobiletemplate Text, name Text, type Text )";

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
