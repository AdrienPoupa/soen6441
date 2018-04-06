import static org.junit.Assert.*;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import models.Status;
import models.User;

/**
 * Tests for the {@link Status} class.
 * @author Wei Li
 *
 */
public class StatusTest{

    /**
     * Getter test for User
     */
	@Test
	public void testGetUser() {
		Status status = new Status();
		User user = new User();
		status.setUser(user);
		assertEquals(user, status.getUser());
	}

    /**
     * Setter test for User
     */
	@Test
	public void testSetUser() {
		Status status = new Status();
		User user = new User();
		status.setUser(user);
		assertEquals(user, status.getUser());
	}

    /**
     * Getter test for FullText
     */
	@Test
	public void testGetFullText() {
		Status status = new Status();
		status.setFullText("Have a nice day!");
		assertEquals("Have a nice day!", status.getFullText());
	}

    /**
     * Setter test for FullText
     */
	@Test
	public void testSetFullText() {
		Status status = new Status();
		status.setFullText("Have a nice day!");
		assertEquals("Have a nice day!", status.getFullText());
	}

	/**
	 * Getter test for type, which always returns "status"
	 */
	@Test
	public void testGetType() {
		String type = new Status().getType();
		assertEquals("status", type);
	}

	/**
	 * Getter test for FullText
	 */
	@Test
	public void testGetId() {
		Status status = new Status();
		status.setId("1");
		assertEquals("1", status.getId());
	}

	/**
	 * Setter test for FullText
	 */
	@Test
	public void testSetId() {
		Status status = new Status();
		status.setId("1");
		assertEquals("1", status.getId());
	}

	/**
	 * Getter test for FullText
	 */
	@Test
	public void testGetQuery() {
		Status status = new Status();
		status.setQuery("concordia");
		assertEquals("concordia", status.getQuery());
	}

	/**
	 * Setter test for FullText
	 */
	@Test
	public void testSetQuery() {
		Status status = new Status();
		status.setQuery("concordia");
		assertEquals("concordia", status.getQuery());
	}

	/**
	 * Verify Equals and HashCode
	 */
	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Status.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.withIgnoredFields("user")
				.verify();
	}

}
