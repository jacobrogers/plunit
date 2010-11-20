package plunit.loaders;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import plunit.PlunitTest;
import plunit.db.PlunitStatement;

public class SuiteLoader {
	
	public List<PlunitTest> load(List<String> testNames, PlunitStatement plunitStatement, Connection connection) throws SQLException {
		List<PlunitTest> plunitTests = new ArrayList<PlunitTest>();
		for(String name : testNames) {
			String[] splitTestName = name.split("~");
			String splitName = splitTestName[0];
			String description = "null".equals(splitTestName[1]) ? null : splitTestName[1];
			PlunitTest test = new PlunitTest(splitName, description, plunitStatement);
			
			plunitTests.add(test);
		}
		return plunitTests;
	}
}
