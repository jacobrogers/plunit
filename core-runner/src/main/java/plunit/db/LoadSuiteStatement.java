package plunit.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OracleTypes;

public class LoadSuiteStatement {
	private static final String loadTestStatement = "begin ? := plunit_wrapper.get_tests(?); end;";
	private Connection connection;
	
	public LoadSuiteStatement() {}
	
	public LoadSuiteStatement(Connection connection) {
		this.connection = connection;
	}
	
	public List<String> loadTests(String suiteName, Connection connection) throws SQLException {
		CallableStatement cstmt = connection.prepareCall(loadTestStatement);
		cstmt.setString(2, suiteName);
		cstmt.registerOutParameter(1, OracleTypes.CURSOR);
		
		List<String> testNames = new ArrayList<String>();
		cstmt.execute();
		ResultSet results = (ResultSet) cstmt.getObject(1);
		while(results.next()) {
			String columnOne = results.getString(1);
			if(columnOne.contains(",")) {
				testNames.add(columnOne);
				break;
			}
			testNames.add(columnOne + "~" + results.getString(2));
		}
		return testNames;
	}
	public List<String> loadTests(String suiteName) throws SQLException {
		return loadTests(suiteName, connection);
	}
}
