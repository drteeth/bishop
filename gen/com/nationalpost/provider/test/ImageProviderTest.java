package com.nationalpost.provider.test;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.nationalpost.model.Image;
import com.nationalpost.provider.ImageProvider;

public class ImageProviderTest extends ProviderTestCase2<ImageProvider>
{
	public ImageProviderTest()
	{
		super( ImageProvider.class, ImageProvider.AUTHORITY );
	}

	private ImageProvider image;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		image = new ImageProvider();
	}

	@Override
	public MockContentResolver getMockContentResolver()
	{
		return super.getMockContentResolver();
	}

	private Image createImage()
	{
		Image image = new Image();
		
		// todo: fill out default properties

		return image;
	}

	public void testPreconditions()
	{
		assertNotNull( image );
	}

	public void testInsertNew()
	{
		ContentResolver r = getMockContentResolver();
		Image image = createImage();
		Uri newImageUri = r.insert( ImageProvider.CONTENT_URI, ImageProvider.getValues( image ) );

		assertNotNull( newImageUri );

		// get it from the database

		Cursor c = r.query( newImageUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		image = ImageProvider.getFromCursor( c );

		assertNotNull( image );
		assertNotNull( image.get_ID() );
		assertNotNull( image.getCreated() );
		assertNotNull( image.getModified() );
	}

	public void testQuery()
	{
		ContentResolver r = getMockContentResolver();
		Image image = createImage();

		Uri imageUri = r.insert( ImageProvider.CONTENT_URI, ImageProvider.getValues( image ) );

		Cursor c = r.query( imageUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Image fromDb = ImageProvider.getFromCursor( c );

		assertEquals( imageUri.getPathSegments().get(1), fromDb.get_ID().toString() );
	}

	public void testUpdate()
	{
		String expectedTitle = "updated title";

		ContentResolver r = getMockContentResolver();
		Image image = createImage();

		Uri editionUri = r.insert( ImageProvider.CONTENT_URI, ImageProvider.getValues( image ) );
		image.set_ID( Integer.parseInt( editionUri.getPathSegments().get( 1 ) ) );

		//  TODO: change some value
		// image.setTitle( expectedTitle );

		int count = r.update( editionUri, ImageProvider.getValues( image ), null, null );

		assertEquals( 1, count );

		Cursor c = r.query( editionUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Image fromDb = ImageProvider.getFromCursor( c );

		// TODO make sure it's changed.
		//assertEquals( expectedTitle, fromDb.Title );

		assertNotNull( fromDb.getModified() );
	}
}
