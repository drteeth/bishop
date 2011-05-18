package com.nationalpost.provider.test;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.nationalpost.model.Video;
import com.nationalpost.provider.VideoProvider;

public class VideoProviderTest extends ProviderTestCase2<VideoProvider>
{
	public VideoProviderTest()
	{
		super( VideoProvider.class, VideoProvider.AUTHORITY );
	}

	private VideoProvider video;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		video = new VideoProvider();
	}

	@Override
	public MockContentResolver getMockContentResolver()
	{
		return super.getMockContentResolver();
	}

	private Video createVideo()
	{
		Video video = new Video();
		
		// todo: fill out default properties

		return video;
	}

	public void testPreconditions()
	{
		assertNotNull( video );
	}

	public void testInsertNew()
	{
		ContentResolver r = getMockContentResolver();
		Video video = createVideo();
		Uri newVideoUri = r.insert( VideoProvider.CONTENT_URI, VideoProvider.getValues( video ) );

		assertNotNull( newVideoUri );

		// get it from the database

		Cursor c = r.query( newVideoUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		video = VideoProvider.getFromCursor( c );

		assertNotNull( video );
		assertNotNull( video.get_ID() );
		assertNotNull( video.getCreated() );
		assertNotNull( video.getModified() );
	}

	public void testQuery()
	{
		ContentResolver r = getMockContentResolver();
		Video video = createVideo();

		Uri videoUri = r.insert( VideoProvider.CONTENT_URI, VideoProvider.getValues( video ) );

		Cursor c = r.query( videoUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Video fromDb = VideoProvider.getFromCursor( c );

		assertEquals( videoUri.getPathSegments().get(1), fromDb.get_ID().toString() );
	}

	public void testUpdate()
	{
		String expectedTitle = "updated title";

		ContentResolver r = getMockContentResolver();
		Video video = createVideo();

		Uri editionUri = r.insert( VideoProvider.CONTENT_URI, VideoProvider.getValues( video ) );
		video.set_ID( Integer.parseInt( editionUri.getPathSegments().get( 1 ) ) );

		//  TODO: change some value
		// video.setTitle( expectedTitle );

		int count = r.update( editionUri, VideoProvider.getValues( video ), null, null );

		assertEquals( 1, count );

		Cursor c = r.query( editionUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Video fromDb = VideoProvider.getFromCursor( c );

		// TODO make sure it's changed.
		//assertEquals( expectedTitle, fromDb.Title );

		assertNotNull( fromDb.getModified() );
	}
}
