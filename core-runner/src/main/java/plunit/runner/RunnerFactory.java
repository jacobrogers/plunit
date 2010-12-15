package plunit.runner;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import plunit.PlunitTestSuite;
import plunit.loaders.PlunitTestLoader;


public class RunnerFactory {
	private PlunitTestLoader testLoader = new PlunitTestLoader();
	
	public Runner build(String suiteName, Connection connection) throws SQLException {
		CallableStatement statement = connection.prepareCall("begin plunit_wrapper.run_test(?,?,?,?,?,?); end;");
		statement.setString(1, suiteName);
		statement.registerOutParameter(3, OracleTypes.INTEGER);
		statement.registerOutParameter(4, OracleTypes.VARCHAR);
		statement.registerOutParameter(5, OracleTypes.VARCHAR);
		statement.registerOutParameter(6, OracleTypes.VARCHAR);
		PlunitTestSuite testSuite = testLoader.load(suiteName, connection);
		Runner runner = new Runner(suiteName, connection, statement, testSuite);
		return runner;
	}
}
