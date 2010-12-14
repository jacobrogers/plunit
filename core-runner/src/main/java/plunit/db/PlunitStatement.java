package plunit.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import plunit.TestResult;

public class PlunitStatement {
	private static String RUN_TEST_CALLABLE_STATEMENT = "begin plunit_wrapper.run_test(?,?,?,?,?,?); end;";
	private CallableStatement runTestCstmt;
	protected TestResultBuilder testResultBuilder = new TestResultBuilder();
	
	public PlunitStatement() {}
	
	public PlunitStatement(String suiteName, Connection dbConnection) throws SQLException {
		runTestCstmt = dbConnection.prepareCall(RUN_TEST_CALLABLE_STATEMENT);
		runTestCstmt.setString(1, suiteName);
		runTestCstmt.registerOutParameter(3, OracleTypes.INTEGER);
		runTestCstmt.registerOutParameter(4, OracleTypes.VARCHAR);
		runTestCstmt.registerOutParameter(5, OracleTypes.VARCHAR);
		runTestCstmt.registerOutParameter(6, OracleTypes.VARCHAR);
	}
	
	public TestResult execute(String testName) {
		try {
			runTest(testName);
			return testResultBuilder.build(runTestCstmt);
		} catch(SQLException e) {
			return buildErrorResult(e);
		}
	}

	public void close() throws SQLException {
		runTestCstmt.close();
	}
	
	private void runTest(String testName) throws SQLException {
		runTestCstmt.setString(2, testName);
		runTestCstmt.execute();
	}
	
	private TestResult buildErrorResult(SQLException e) {
		TestResult testResult = new TestResult();
		testResult = new TestResult();
		testResult.setPassing(false);
		testResult.setErrorMessage(e.getMessage());
		return testResult;
	}
}
