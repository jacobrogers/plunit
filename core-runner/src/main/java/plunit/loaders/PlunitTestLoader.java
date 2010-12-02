package plunit.loaders;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import plunit.PlunitTest;
import plunit.PlunitTestSuite;
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
	
	public List<PlunitTest> load(String testName, PlunitStatement plunitStatement, Connection connection) throws SQLException {
		List<String> testList = loadSuiteStatement.loadTests(testName, connection);
		
		if(isSuiteOfSuites(testList)) {
			return suiteOfSuitesLoader.load(testList.get(0).toUpperCase(), plunitStatement, connection);
		} else {
			return suiteLoader.load(testList, plunitStatement, connection);
		}
	}
	
	private boolean isSuiteOfSuites(List<String> testList) {
		return testList.size() == 1 && testList.get(0).contains(",");
	}
}
