package models;

import static org.junit.Assert.*;

import org.junit.Test;

import models.SearchResult;

/**
 * Test for the{@link SearchResult} class.
 * @author Wei Li
 *
 */
public class SearchResultTest{

	@Test
	public void testGetQuery() {
		SearchResult sr=new SearchResult();
		sr.setQuery("keyword");
		assertEquals("keyword",sr.getQuery());
	}

	@Test
	public void testSetQuery() {
		SearchResult sr=new SearchResult();
		sr.setQuery("keyword");
		assertEquals("keyword",sr.getQuery());
	}

	@Test
	public void testSetStatuses() {
		SearchResult sr=new SearchResult();
		sr.setStatuses(null);
		assertNull(sr.getStatuses());
	}

	@Test
	public void testGetStatuses() {
		SearchResult sr=new SearchResult();
		sr.setStatuses(null);
		assertNull(sr.getStatuses());
	}

}
