package com.nationalpost.provider.test;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.nationalpost.model.Story;
import com.nationalpost.provider.StoryProvider;

public class StoryProviderTest extends ProviderTestCase2<StoryProvider>
{
	public StoryProviderTest()
	{
		super( StoryProvider.class, StoryProvider.AUTHORITY );
	}

	private StoryProvider story;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		story = new StoryProvider();
	}

	@Override
	public MockContentResolver getMockContentResolver()
	{
		return super.getMockContentResolver();
	}

	private Story createStory()
	{
		Story story = new Story();
		
		// todo: fill out default properties

		return story;
	}

	public void testPreconditions()
	{
		assertNotNull( story );
	}

	public void testInsertNew()
	{
		ContentResolver r = getMockContentResolver();
		Story story = createStory();
		Uri newStoryUri = r.insert( StoryProvider.CONTENT_URI, StoryProvider.getValues( story ) );

		assertNotNull( newStoryUri );

		// get it from the database

		Cursor c = r.query( newStoryUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		story = StoryProvider.getFromCursor( c );

		assertNotNull( story );
		assertNotNull( story.get_ID() );
		assertNotNull( story.getCreated() );
		assertNotNull( story.getModified() );
	}

	public void testQuery()
	{
		ContentResolver r = getMockContentResolver();
		Story story = createStory();

		Uri storyUri = r.insert( StoryProvider.CONTENT_URI, StoryProvider.getValues( story ) );

		Cursor c = r.query( storyUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Story fromDb = StoryProvider.getFromCursor( c );

		assertEquals( storyUri.getPathSegments().get(1), fromDb.get_ID().toString() );
	}

	public void testUpdate()
	{
		String expectedTitle = "updated title";

		ContentResolver r = getMockContentResolver();
		Story story = createStory();

		Uri editionUri = r.insert( StoryProvider.CONTENT_URI, StoryProvider.getValues( story ) );
		story.set_ID( Integer.parseInt( editionUri.getPathSegments().get( 1 ) ) );

		//  TODO: change some value
		// story.setTitle( expectedTitle );

		int count = r.update( editionUri, StoryProvider.getValues( story ), null, null );

		assertEquals( 1, count );

		Cursor c = r.query( editionUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Story fromDb = StoryProvider.getFromCursor( c );

		// TODO make sure it's changed.
		//assertEquals( expectedTitle, fromDb.Title );

		assertNotNull( fromDb.getModified() );
	}
}
