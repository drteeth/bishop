package com.nationalpost.provider.test;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.nationalpost.model.Gallery;
import com.nationalpost.provider.GalleryProvider;

public class GalleryProviderTest extends ProviderTestCase2<GalleryProvider>
{
	public GalleryProviderTest()
	{
		super( GalleryProvider.class, GalleryProvider.AUTHORITY );
	}

	private GalleryProvider gallery;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		gallery = new GalleryProvider();
	}

	@Override
	public MockContentResolver getMockContentResolver()
	{
		return super.getMockContentResolver();
	}

	private Gallery createGallery()
	{
		Gallery gallery = new Gallery();
		
		// todo: fill out default properties

		return gallery;
	}

	public void testPreconditions()
	{
		assertNotNull( gallery );
	}

	public void testInsertNew()
	{
		ContentResolver r = getMockContentResolver();
		Gallery gallery = createGallery();
		Uri newGalleryUri = r.insert( GalleryProvider.CONTENT_URI, GalleryProvider.getValues( gallery ) );

		assertNotNull( newGalleryUri );

		// get it from the database

		Cursor c = r.query( newGalleryUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		gallery = GalleryProvider.getFromCursor( c );

		assertNotNull( gallery );
		assertNotNull( gallery.get_ID() );
		assertNotNull( gallery.getCreated() );
		assertNotNull( gallery.getModified() );
	}

	public void testQuery()
	{
		ContentResolver r = getMockContentResolver();
		Gallery gallery = createGallery();

		Uri galleryUri = r.insert( GalleryProvider.CONTENT_URI, GalleryProvider.getValues( gallery ) );

		Cursor c = r.query( galleryUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Gallery fromDb = GalleryProvider.getFromCursor( c );

		assertEquals( galleryUri.getPathSegments().get(1), fromDb.get_ID().toString() );
	}

	public void testUpdate()
	{
		String expectedTitle = "updated title";

		ContentResolver r = getMockContentResolver();
		Gallery gallery = createGallery();

		Uri editionUri = r.insert( GalleryProvider.CONTENT_URI, GalleryProvider.getValues( gallery ) );
		gallery.set_ID( Integer.parseInt( editionUri.getPathSegments().get( 1 ) ) );

		//  TODO: change some value
		// gallery.setTitle( expectedTitle );

		int count = r.update( editionUri, GalleryProvider.getValues( gallery ), null, null );

		assertEquals( 1, count );

		Cursor c = r.query( editionUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Gallery fromDb = GalleryProvider.getFromCursor( c );

		// TODO make sure it's changed.
		//assertEquals( expectedTitle, fromDb.Title );

		assertNotNull( fromDb.getModified() );
	}
}
