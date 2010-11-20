package plunit.db;

import java.sql.CallableStatement;
import java.sql.SQLException;

import plunit.TestResult;

public class TestResultBuilder {
	
	public TestResult build(CallableStatement testRun) throws SQLException {
		TestResult testResult = new TestResult();
		testResult.setPassing( isTestPassing(testRun));
		testResult.setErrorMessage( errorMessage(testRun));
		testResult.setCallStack( callStack(testRun));
		testResult.setDbmsOutput( dbmsOutput(testRun));
		return testResult;
	}
	private boolean isTestPassing(CallableStatement testRun) throws SQLException {
		Integer isTestPassing = (Integer) testRun.getObject(3);
		return (isTestPassing == 1) ? true : false;
	}
	private String errorMessage(CallableStatement testRun) throws SQLException {
		String errorMessage = (String) testRun.getObject(4);
		return (errorMessage == null) ? "No error message was found" : errorMessage;
	}
	private String callStack(CallableStatement testRun) throws SQLException {
		String callStack = (String) testRun.getObject(5);
		return (callStack == null) ? "No call stack was found" : callStack;
	}
	private String dbmsOutput(CallableStatement testRun) throws SQLException {
		String dbmsOutput = (String) testRun.getObject(6);
		return dbmsOutput;
	}
}
