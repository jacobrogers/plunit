package plunit;

import java.sql.SQLException;

import plunit.db.PlunitStatement;

public interface Testable {
	void run() throws SQLException, InvalidPlunitTestException;
	void reset() throws SQLException;
	boolean isPassing();
	TestResult getResult();
	String getName();
	String getDescription();
	TestState getState();
	void setPlunitStatement(PlunitStatement plunitStatement);
}
