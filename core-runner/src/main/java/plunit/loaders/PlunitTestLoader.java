package plunit.loaders;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import plunit.PlunitTestSuite;
import plunit.Testable;
import plunit.db.LoadSuiteStatement;
import plunit.db.PlunitStatement;

public class PlunitTestLoader {
	private LoadSuiteStatement loadSuiteStatement;
	private SuiteOfSuitesLoader suiteOfSuitesLoader = new SuiteOfSuitesLoader();
	private SuiteLoader suiteLoader = new SuiteLoader();
	
	protected PlunitTestLoader() {}
	
	public PlunitTestLoader(LoadSuiteStatement loadSuiteStatement) {
		this.loadSuiteStatement = loadSuiteStatement;
	}
	
	public PlunitTestSuite load(String suiteName, PlunitStatement plunitStatement, Connection connection) throws SQLException {
		List<String> testList = loadSuiteStatement.loadTests(suiteName, connection);
		
		List<Testable> tests = null;
		if(isSuiteOfSuites(testList)) {
			tests = suiteOfSuitesLoader.load(testList.get(0).toUpperCase(), plunitStatement, connection);
		} else {
			tests = suiteLoader.load(testList, plunitStatement, connection);
		}
		return new PlunitTestSuite(suiteName, tests);
	}
	
	private boolean isSuiteOfSuites(List<String> testList) {
		return testList.size() == 1 && testList.get(0).contains(",");
	}
}
