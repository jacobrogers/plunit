package plunit.db;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oracle.jdbc.OracleTypes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoadTestStatementTest {
	private LoadSuiteStatement statement = new LoadSuiteStatement();
	
	@Mock Connection connection;
	@Mock ResultSet resultSet;
	@Mock CallableStatement callableStatement;
	
	@Before
	public void setup() throws SQLException {
		when(connection.prepareCall(anyString())).thenReturn(callableStatement);
		when(callableStatement.getObject(1)).thenReturn(resultSet);
	}
	@Test
	public void statementPreparesCallWithCorrectSqlWhenInitializing() throws SQLException {
		String expectedSql = "begin ? := plunit_wrapper.get_tests(?); end;";
		
		statement.loadTests("suiteName", connection);
		verify(connection).prepareCall(expectedSql);
	}
	@Test
	public void statementSetsTestNameOnCallableStatement() throws SQLException {
		statement.loadTests("suiteName", connection);
		
		verify(callableStatement).setString(2, "suiteName");
	}
	@Test
	public void statementRegistersCursorTypeAsOutParameterOnCallableStatement() throws SQLException {
		statement.loadTests("suiteName", connection);
		
		verify(callableStatement).registerOutParameter(1, OracleTypes.CURSOR);
	}
	@Test
	public void statementExecutesCallableStatementWhenExecuted() throws SQLException {
		statement.loadTests("suiteName", connection);
		
		verify(callableStatement).execute();
	}
	@Test
	public void statementReturnsListOfTestsFromResultSetReturnedFromCallableStatement() throws SQLException {
		List<String> expectedTestList = new ArrayList<String>();
		expectedTestList.add("Test_One");
		expectedTestList.add("Test_Two");
		when(resultSet.getString(1)).thenReturn("Package_one").thenReturn("Test1,Test2");
		when(resultSet.getString(2)).thenReturn("Test_Two");
		when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
		
		List<String> actualTestList = statement.loadTests("suiteName", connection);
		
		assertEquals(Arrays.asList("Package_one~Test_Two", "Test1,Test2"), actualTestList);
	}
}
