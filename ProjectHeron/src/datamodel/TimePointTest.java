package datamodel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TimePointTest {

	@Test
	void timePointTest() {
		TimePoint p1 = new TimePoint(50, 35, 0);
		TimePoint p2 = new TimePoint(60, 35, 20);
		assertEquals("50.0,35.0@T=0", p1.toString());
		assertEquals("60.0,35.0@T=20", p2.toString());
		assertEquals(10, (int) p1.getDistanceTo(p2));
		assertEquals(10, (int) p2.getDistanceTo(p1));
		assertEquals(0, (int) p1.getDistanceTo(p1));
		assertEquals(-20, p1.getTimeDiffAfter(p2));
		assertEquals(20, p2.getTimeDiffAfter(p1));
		assertEquals(0, p1.getTimeDiffAfter(p1));
		assertEquals(false, p1.equals(p2));
		assertEquals(true, p1.equals(p1));
		TimePoint p3 = new TimePoint(50, 35, 0);
		assertEquals(true, p1.equals(p3));
		assertEquals(true, p3.equals(p1));
		assertEquals(-20, p1.compareTo(p2));
		assertEquals(20, p2.compareTo(p1));
		assertEquals(0, p1.getFrameNum());
		assertEquals(20, p2.getFrameNum());
	}

}
