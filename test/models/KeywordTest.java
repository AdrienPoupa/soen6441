package models;

import static org.junit.Assert.*;

import org.junit.Test;

import models.Keyword;
/**
 * Test for the{@link Keyword} class.
 * @author Wei Li
 *
 */
public class KeywordTest {

	@Test
	public void testGetKeyword() {
		Keyword keyword=new Keyword();
		keyword.setKeyword("Concordia");
		assertEquals("Concordia",keyword.getKeyword());
	
	}

	@Test
	public void testSetKeyword() {
		Keyword keyword=new Keyword();
		keyword.setKeyword("Concordia");
		assertEquals("Concordia",keyword.getKeyword());
	}
}
