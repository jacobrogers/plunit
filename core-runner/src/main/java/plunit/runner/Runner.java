package plunit.runner;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import plunit.db.PlunitStatement;
import plunit.loaders.PlunitTestLoader;

public class Runner {
	private PlunitTestLoader testLoader = new PlunitTestLoader();
	private PlunitStatement plunitStatement = new PlunitStatement();
	private String suiteName;
	private Connection connection;
	private CallableStatement callableStatement;
	
	protected Runner() {}
	
	public Runner(String suiteName, Connection connection, CallableStatement callableStatement) {
		this.suiteName = suiteName;
		this.connection = connection;
		this.callableStatement = callableStatement;
	}
	
	public void run(String suiteName, Connection connection) throws SQLException {
		testLoader.load(suiteName, plunitStatement, connection);
	}
	
	protected String getSuiteName() {
		return suiteName;
	}
	protected Connection getConnection() {
		return connection;
	}
	protected CallableStatement getCallableStatement() {
		return callableStatement;
	}
}
