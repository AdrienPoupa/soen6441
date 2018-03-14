import static org.junit.Assert.*;

import org.junit.Test;

import models.User;

/**
 * Tests for the {@link User} class.
 * @author Wei Li
 *
 */
public class UserTest{

    /**
     * Getter test for Location
     */
	@Test
	public void testGetLocation() {
		User user=new User();
		user.setLocation("Montreal");
		assertEquals("Montreal",user.getLocation());
	}

    /**
     * Setter test for Location
     */
	@Test
	public void testSetLocation() {
		User user=new User();
		user.setLocation("Montreal");
		assertEquals("Montreal",user.getLocation());
	}

    /**
     * Getter test for Description
     */
	@Test
	public void testGetDescription() {
		User user=new User();
		user.setDescription("student");
		assertEquals("student",user.getDescription());
	}

    /**
     * Setter test for Description
     */
	@Test
	public void testSetDescription() {
		User user=new User();
		user.setDescription("student");
		assertEquals("student",user.getDescription());
	}

    /**
     * Getter test for Followers
     */
	@Test
	public void testGetFollowers() {
		User user=new User();
		user.setFollowers("100");
		assertEquals("100",user.getFollowers());	
	}

    /**
     * Setter test for Followers
     */
	@Test
	public void testSetFollowers() {
		User user=new User();
		user.setFollowers("100");
		assertEquals("100",user.getFollowers());	
	}

    /**
     * Getter test for Friends
     */
	@Test
	public void testGetFriends() {
		User user=new User();
		user.setFriends("145");
		assertEquals("145",user.getFriends());		
	}

    /**
     * Setter test for Friends
     */
	@Test
	public void testSetFriends() {
		User user=new User();
		user.setFriends("145");
		assertEquals("145",user.getFriends());	
	}

    /**
     * Getter test for Id
     */
	@Test
	public void testGetId() {
		User user=new User();
		user.setId(123);
		assertEquals(123,user.getId());	
	}

    /**
     * Setter test for Id
     */
	@Test
	public void testSetId() {
		User user=new User();
		user.setId(123);
		assertEquals(123,user.getId());		
		}

    /**
     * Getter test for Name
     */
	@Test
	public void testGetName() {
		User user=new User();
		user.setName("Bob");
		assertEquals("Bob",user.getName());		
		}

    /**
     * Setter test for Name
     */
	@Test
	public void testSetName() {
		User user=new User();
		user.setName("Bob");
		assertEquals("Bob",user.getName());
		}

    /**
     * Getter test for ScreenName
     */
	@Test
	public void testGetScreenName() {
		User user=new User();
		user.setScreenName("Bill");
		assertEquals("Bill",user.getScreenName());	
		}

    /**
     * Setter test for ScreenName
     */
	@Test
	public void testSetScreenName() {
		User user=new User();
		user.setScreenName("Bill");
		assertEquals("Bill",user.getScreenName());	}

}
