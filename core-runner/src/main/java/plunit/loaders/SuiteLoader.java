package plunit.loaders;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import plunit.PlunitTest;
import plunit.Testable;
import plunit.db.PlunitStatement;

public class SuiteLoader {
	public List<Testable> load(List<String> testNames, PlunitStatement plunitStatement, Connection connection) throws SQLException {
		List<Testable> plunitTests = new ArrayList<Testable>();
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
