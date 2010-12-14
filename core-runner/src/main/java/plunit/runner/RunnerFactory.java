package plunit.runner;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;


public class RunnerFactory {
	public Runner build(String suiteName, Connection connection) throws SQLException {
		CallableStatement statement = connection.prepareCall("begin plunit_wrapper.run_test(?,?,?,?,?,?); end;");
		statement.setString(1, suiteName);
		statement.registerOutParameter(3, OracleTypes.INTEGER);
		statement.registerOutParameter(4, OracleTypes.VARCHAR);
		statement.registerOutParameter(5, OracleTypes.VARCHAR);
		statement.registerOutParameter(6, OracleTypes.VARCHAR);
		Runner runner = new Runner(suiteName, connection, statement);
		return runner;
	}
}
