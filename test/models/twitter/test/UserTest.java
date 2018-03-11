package models.twitter.test;

import static org.junit.Assert.*;

import org.junit.Test;

import models.twitter.User;

/**
 * Tests for the {@link User} class.
 * @author Wei Li
 *
 */
public class UserTest{

	@Test
	public void testGetLocation() {
		User user=new User();
		user.setLocation("Montreal");
		assertEquals("Montreal",user.getLocation());
	}

	@Test
	public void testSetLocation() {
		User user=new User();
		user.setLocation("Montreal");
		assertEquals("Montreal",user.getLocation());
	}

	@Test
	public void testGetDescription() {
		User user=new User();
		user.setDescription("student");
		assertEquals("student",user.getDescription());
	}

	@Test
	public void testSetDescription() {
		User user=new User();
		user.setDescription("student");
		assertEquals("student",user.getDescription());
	}

	@Test
	public void testGetFollowers() {
		User user=new User();
		user.setFollowers("100");
		assertEquals("100",user.getFollowers());	
	}

	@Test
	public void testSetFollowers() {
		User user=new User();
		user.setFollowers("100");
		assertEquals("100",user.getFollowers());	
	}

	@Test
	public void testGetFriends() {
		User user=new User();
		user.setFriends("145");
		assertEquals("145",user.getFriends());		
	}

	@Test
	public void testSetFriends() {
		User user=new User();
		user.setFriends("145");
		assertEquals("145",user.getFriends());	
	}

	@Test
	public void testGetId() {
		User user=new User();
		user.setId(123);
		assertEquals(123,user.getId());	
	}

	@Test
	public void testSetId() {
		User user=new User();
		user.setId(123);
		assertEquals(123,user.getId());		
		}

	@Test
	public void testGetName() {
		User user=new User();
		user.setName("Bob");
		assertEquals("Bob",user.getName());		
		}

	@Test
	public void testSetName() {
		User user=new User();
		user.setName("Bob");
		assertEquals("Bob",user.getName());
		}

	@Test
	public void testGetScreenName() {
		User user=new User();
		user.setScreenName("Bill");
		assertEquals("Bill",user.getScreenName());	
		}

	@Test
	public void testSetScreenName() {
		User user=new User();
		user.setScreenName("Bill");
		assertEquals("Bill",user.getScreenName());	}

}
