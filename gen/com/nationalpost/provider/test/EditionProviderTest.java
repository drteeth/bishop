package com.nationalpost.provider.test;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.nationalpost.model.Edition;
import com.nationalpost.provider.EditionProvider;

public class EditionProviderTest extends ProviderTestCase2<EditionProvider>
{
	public EditionProviderTest()
	{
		super( EditionProvider.class, EditionProvider.AUTHORITY );
	}

	private EditionProvider edition;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		edition = new EditionProvider();
	}

	@Override
	public MockContentResolver getMockContentResolver()
	{
		return super.getMockContentResolver();
	}

	private Edition createEdition()
	{
		Edition edition = new Edition();
		
		// todo: fill out default properties

		return edition;
	}

	public void testPreconditions()
	{
		assertNotNull( edition );
	}

	public void testInsertNew()
	{
		ContentResolver r = getMockContentResolver();
		Edition edition = createEdition();
		Uri newEditionUri = r.insert( EditionProvider.CONTENT_URI, EditionProvider.getValues( edition ) );

		assertNotNull( newEditionUri );

		// get it from the database

		Cursor c = r.query( newEditionUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		edition = EditionProvider.getFromCursor( c );

		assertNotNull( edition );
		assertNotNull( edition.get_ID() );
		assertNotNull( edition.getCreated() );
		assertNotNull( edition.getModified() );
	}

	public void testQuery()
	{
		ContentResolver r = getMockContentResolver();
		Edition edition = createEdition();

		Uri editionUri = r.insert( EditionProvider.CONTENT_URI, EditionProvider.getValues( edition ) );

		Cursor c = r.query( editionUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Edition fromDb = EditionProvider.getFromCursor( c );

		assertEquals( editionUri.getPathSegments().get(1), fromDb.get_ID().toString() );
	}

	public void testUpdate()
	{
		String expectedTitle = "updated title";

		ContentResolver r = getMockContentResolver();
		Edition edition = createEdition();

		Uri editionUri = r.insert( EditionProvider.CONTENT_URI, EditionProvider.getValues( edition ) );
		edition.set_ID( Integer.parseInt( editionUri.getPathSegments().get( 1 ) ) );

		//  TODO: change some value
		// edition.setTitle( expectedTitle );

		int count = r.update( editionUri, EditionProvider.getValues( edition ), null, null );

		assertEquals( 1, count );

		Cursor c = r.query( editionUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Edition fromDb = EditionProvider.getFromCursor( c );

		// TODO make sure it's changed.
		//assertEquals( expectedTitle, fromDb.Title );

		assertNotNull( fromDb.getModified() );
	}
}
