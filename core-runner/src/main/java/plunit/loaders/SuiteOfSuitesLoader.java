package plunit.loaders;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import plunit.PlunitTestSuite;
import plunit.Testable;
import plunit.db.LoadSuiteStatement;
import plunit.db.PlunitStatement;

public class SuiteOfSuitesLoader {
	private SuiteLoader suiteLoader = new SuiteLoader();
	private LoadSuiteStatement loadSuiteStatement = new LoadSuiteStatement();
	
	public List<Testable> load(String suiteName, PlunitStatement plunitStatement,	Connection connection) throws SQLException {
		List<Testable> suites = new ArrayList<Testable>();
		for(String suite : suiteName.split(",")) {
			List<String> suiteTests = loadSuiteStatement.loadTests(suite, connection);
			List<Testable> tests = suiteLoader.load(suiteTests, plunitStatement, connection);
			suites.add(new PlunitTestSuite(suite, tests, connection));
		}
		return suites;
	}
}
