package com.nationalpost.provider.test;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.nationalpost.model.Publication;
import com.nationalpost.provider.PublicationProvider;

public class PublicationProviderTest extends ProviderTestCase2<PublicationProvider>
{
	public PublicationProviderTest()
	{
		super( PublicationProvider.class, PublicationProvider.AUTHORITY );
	}

	private PublicationProvider publication;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		publication = new PublicationProvider();
	}

	@Override
	public MockContentResolver getMockContentResolver()
	{
		return super.getMockContentResolver();
	}

	private Publication createPublication()
	{
		Publication publication = new Publication();
		
		// todo: fill out default properties

		return publication;
	}

	public void testPreconditions()
	{
		assertNotNull( publication );
	}

	public void testInsertNew()
	{
		ContentResolver r = getMockContentResolver();
		Publication publication = createPublication();
		Uri newPublicationUri = r.insert( PublicationProvider.CONTENT_URI, PublicationProvider.getValues( publication ) );

		assertNotNull( newPublicationUri );

		// get it from the database

		Cursor c = r.query( newPublicationUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		publication = PublicationProvider.getFromCursor( c );

		assertNotNull( publication );
		assertNotNull( publication.get_ID() );
		assertNotNull( publication.getCreated() );
		assertNotNull( publication.getModified() );
	}

	public void testQuery()
	{
		ContentResolver r = getMockContentResolver();
		Publication publication = createPublication();

		Uri publicationUri = r.insert( PublicationProvider.CONTENT_URI, PublicationProvider.getValues( publication ) );

		Cursor c = r.query( publicationUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Publication fromDb = PublicationProvider.getFromCursor( c );

		assertEquals( publicationUri.getPathSegments().get(1), fromDb.get_ID().toString() );
	}

	public void testUpdate()
	{
		String expectedTitle = "updated title";

		ContentResolver r = getMockContentResolver();
		Publication publication = createPublication();

		Uri editionUri = r.insert( PublicationProvider.CONTENT_URI, PublicationProvider.getValues( publication ) );
		publication.set_ID( Integer.parseInt( editionUri.getPathSegments().get( 1 ) ) );

		//  TODO: change some value
		// publication.setTitle( expectedTitle );

		int count = r.update( editionUri, PublicationProvider.getValues( publication ), null, null );

		assertEquals( 1, count );

		Cursor c = r.query( editionUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Publication fromDb = PublicationProvider.getFromCursor( c );

		// TODO make sure it's changed.
		//assertEquals( expectedTitle, fromDb.Title );

		assertNotNull( fromDb.getModified() );
	}
}
