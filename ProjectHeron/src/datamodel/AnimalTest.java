package datamodel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import datamodel.*;
class AnimalTest {

	@Test
	void test() {
		AnimalTrack animal = new AnimalTrack("Chicken");
		assertEquals("Chicken",animal.getAnimalID());
	}

}
