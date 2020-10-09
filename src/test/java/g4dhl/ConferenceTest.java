package g4dhl;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class ConferenceTest {

	@Test
	public void setConferenceNameTest() {
		Conference conference = new Conference();
		conference.setConferenceName("Eastern Conference");
		Assert.assertEquals("Eastern Conference", conference.getConferenceName());
	}

	@Test
	public void setDivisionsTest() {
		Conference conference = new Conference();
		ArrayList<IDivision> divisions = new ArrayList<>();
		conference.setDivisions(divisions);
		Assert.assertEquals(divisions, conference.getDivisions());
	}
}
