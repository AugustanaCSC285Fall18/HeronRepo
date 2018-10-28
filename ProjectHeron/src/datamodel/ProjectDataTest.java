package datamodel;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

class ProjectDataTest {

	@Test
	void test() throws FileNotFoundException {
		ProjectData dataTest = new ProjectData("data");
	}

}
