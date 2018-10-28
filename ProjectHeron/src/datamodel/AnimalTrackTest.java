package datamodel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AnimalTrackTest {

	@Test
	void animalTrackTest() {
		AnimalTrack animalTest = new AnimalTrack("Chick");
		
		assertEquals("Chick", animalTest.getAnimalID());
		animalTest.add(new TimePoint(20, 20, 0));
		assertEquals("AnimalTrack[id=Chick,numPts=1 startFrame=0 endFrame=0]", animalTest.toString());
		assertEquals(1, animalTest.size());		
		assertEquals(new TimePoint(20, 20, 0), animalTest.getTimePointAtTime(0));
		assertNull(animalTest.getTimePointAtTime(10));
		animalTest.add(30, 20, 10);	
		assertEquals(false, animalTest.alreadyHasTime(5));
		assertEquals(true, animalTest.alreadyHasTime(10));
		animalTest.add(new TimePoint(35, 25, 15));
		assertEquals(new TimePoint(35, 25, 15), animalTest.getFinalTimePoint());
		assertEquals(new TimePoint(20, 20, 0), animalTest.TimePointAtIndex(0));
		assertEquals(new TimePoint(30, 20, 10), animalTest.TimePointAtIndex(1));
		assertEquals(true, animalTest.alreadyHasTime(0));
		assertEquals(new TimePoint(20,20,0), animalTest.getClosestTimePoint(5));
		assertEquals(new TimePoint(30,20,10), animalTest.getClosestTimePoint(11));
		assertEquals(new TimePoint(35, 25, 15), animalTest.getClosestTimePoint(30));
	}
}
