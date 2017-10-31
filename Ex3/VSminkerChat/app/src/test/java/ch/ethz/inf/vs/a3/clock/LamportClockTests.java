package ch.ethz.inf.vs.a3.clock;

import org.junit.Assert;
import org.junit.Test;

import ch.ethz.inf.vs.a3.clock.LamportClock;

public class LamportClockTests {
	private final int testTime = 5;

	@Test
	public void setTime() {
		LamportClock testClock = new LamportClock();
		testClock.setTime(testTime);
		Assert.assertEquals(testTime, testClock.getTime());
	}

	@Test
	public void updateWithLowerClock() {
		LamportClock testClock = new LamportClock();
		testClock.setTime(testTime);
		LamportClock oldClock = new LamportClock();
		oldClock.setTime(testTime);
		LamportClock newClock = new LamportClock();
		newClock.setTime(testTime - 1);
		testClock.update(newClock);
		Assert.assertEquals(oldClock.getTime(), testClock.getTime());
	}

	@Test
	public void updateWithHigherClock() {
		LamportClock testClock = new LamportClock();
		testClock.setTime(testTime);
		LamportClock oldClock = new LamportClock();
		oldClock.setTime(testTime);
		LamportClock newClock = new LamportClock();
		newClock.setTime(testTime + 1);
		testClock.update(newClock);
		Assert.assertEquals(testClock.getTime(), newClock.getTime());
	}

	@Test
	public void setClock() {
		LamportClock testClock = new LamportClock();
		LamportClock newClock = new LamportClock();
		newClock.setTime(testTime);
		testClock.setClock(newClock);
		Assert.assertEquals(testTime, testClock.getTime());
	}

	@Test
	public void tick() {
		LamportClock testClock = new LamportClock();
		testClock.setTime(testTime);
		testClock.tick(null);
		Assert.assertEquals(testTime + 1, testClock.getTime());
	}
	
	@Test
	public void compareWithLaterEvent() {
		LamportClock refClock = new LamportClock();
		refClock.setTime(testTime);
		LamportClock incomingClock = new LamportClock();
		incomingClock.setTime(testTime + 1);
		Assert.assertFalse(incomingClock.happenedBefore(refClock));
	}

	@Test
	public void compareWithParallelEvent() {
		LamportClock refClock = new LamportClock();
		refClock.setTime(testTime);
		LamportClock incomingClock = new LamportClock();
		incomingClock.setTime(testTime);
		Assert.assertFalse(incomingClock.happenedBefore(refClock));
	}
	
	@Test
	public void compareWithEarlierEvent() {
		LamportClock refClock = new LamportClock();
		refClock.setTime(testTime);
		LamportClock incomingClock = new LamportClock();
		incomingClock.setTime(testTime - 1);
		Assert.assertTrue(incomingClock.happenedBefore(refClock));
	}
	
	@Test
	public void convertToStringValidClock() {
		LamportClock refClock = new LamportClock();
		refClock.setTime(testTime);		
		Assert.assertEquals(new Integer(testTime).toString(),refClock.toString());
	}
	
	@Test
	public void convertToStringNewClock() {
		LamportClock refClock = new LamportClock();		
		Assert.assertEquals("0", refClock.toString());
	}
	
	@Test
	public void convertValidStringToClock() {
		String input = new Integer(testTime).toString();
		LamportClock refClock = new LamportClock();
		refClock.setClockFromString(input);
		Assert.assertEquals("The lamport time should be" + testTime, testTime, refClock.getTime());
	}
	
	@Test
	public void convertEmptyStringToClock() {
		String input = "";
		LamportClock refClock = new LamportClock();
		refClock.setClockFromString(input);
		Assert.assertEquals("The lamport time should stay unchanged", 0, refClock.getTime());
	}
	
	@Test
	public void convertInvalidStringToClock() {
		String input = "rAnD42";
		LamportClock refClock = new LamportClock();
		refClock.setClockFromString(input);
		Assert.assertEquals("The lamport time should stay unchanged", 0, refClock.getTime());
	}
}
