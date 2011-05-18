package com.nationalpost.provider.test;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.nationalpost.model.Section;
import com.nationalpost.provider.SectionProvider;

public class SectionProviderTest extends ProviderTestCase2<SectionProvider>
{
	public SectionProviderTest()
	{
		super( SectionProvider.class, SectionProvider.AUTHORITY );
	}

	private SectionProvider section;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		section = new SectionProvider();
	}

	@Override
	public MockContentResolver getMockContentResolver()
	{
		return super.getMockContentResolver();
	}

	private Section createSection()
	{
		Section section = new Section();
		
		// todo: fill out default properties

		return section;
	}

	public void testPreconditions()
	{
		assertNotNull( section );
	}

	public void testInsertNew()
	{
		ContentResolver r = getMockContentResolver();
		Section section = createSection();
		Uri newSectionUri = r.insert( SectionProvider.CONTENT_URI, SectionProvider.getValues( section ) );

		assertNotNull( newSectionUri );

		// get it from the database

		Cursor c = r.query( newSectionUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		section = SectionProvider.getFromCursor( c );

		assertNotNull( section );
		assertNotNull( section.get_ID() );
		assertNotNull( section.getCreated() );
		assertNotNull( section.getModified() );
	}

	public void testQuery()
	{
		ContentResolver r = getMockContentResolver();
		Section section = createSection();

		Uri sectionUri = r.insert( SectionProvider.CONTENT_URI, SectionProvider.getValues( section ) );

		Cursor c = r.query( sectionUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Section fromDb = SectionProvider.getFromCursor( c );

		assertEquals( sectionUri.getPathSegments().get(1), fromDb.get_ID().toString() );
	}

	public void testUpdate()
	{
		String expectedTitle = "updated title";

		ContentResolver r = getMockContentResolver();
		Section section = createSection();

		Uri editionUri = r.insert( SectionProvider.CONTENT_URI, SectionProvider.getValues( section ) );
		section.set_ID( Integer.parseInt( editionUri.getPathSegments().get( 1 ) ) );

		//  TODO: change some value
		// section.setTitle( expectedTitle );

		int count = r.update( editionUri, SectionProvider.getValues( section ), null, null );

		assertEquals( 1, count );

		Cursor c = r.query( editionUri, null, null, null, null );
		assertEquals( 1, c.getCount() );
		assertTrue( c.moveToFirst() );

		Section fromDb = SectionProvider.getFromCursor( c );

		// TODO make sure it's changed.
		//assertEquals( expectedTitle, fromDb.Title );

		assertNotNull( fromDb.getModified() );
	}
}
