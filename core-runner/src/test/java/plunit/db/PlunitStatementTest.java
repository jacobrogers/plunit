package plunit.db;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import plunit.TestResult;

@RunWith(MockitoJUnitRunner.class)
public class PlunitStatementTest {
	private String expectedSql = "begin plunit_wrapper.run_test(?,?,?,?,?,?); end;";
	private PlunitStatement plunitStatement;
	@Mock Connection mockConnection;
	@Mock CallableStatement statement;
	@Mock TestResultBuilder testResultBuilder;
	
	@Before
	public void setup() throws SQLException {
		when(mockConnection.prepareCall(expectedSql)).thenReturn(statement);
		plunitStatement = new PlunitStatement("suite name", mockConnection);
		plunitStatement.testResultBuilder = testResultBuilder;
	}
	
	@Test
	public void setupCallableStatement() throws SQLException {
		InOrder inOrder = inOrder(statement);
		
		new PlunitStatement("suite name", mockConnection);
		
		verifySetup(inOrder);
	}
	@Test
	public void runTest() throws SQLException {
		TestResult expectedResult = new TestResult();
		when(testResultBuilder.build(statement)).thenReturn(expectedResult);
		InOrder inOrder = inOrder(statement, testResultBuilder);
		
		TestResult actualResult = plunitStatement.execute("test name");
		
		verifySetup(inOrder);
		
		inOrder.verify(statement).setString(2, "test name");
		inOrder.verify(statement).execute();
		inOrder.verify(testResultBuilder).build(statement);
		
		assertSame(expectedResult, actualResult);
	}
	private void verifySetup(InOrder inOrder) throws SQLException {
		inOrder.verify(statement).setString(1, "suite name");
		inOrder.verify(statement).registerOutParameter(3, OracleTypes.INTEGER);
		inOrder.verify(statement).registerOutParameter(4, OracleTypes.VARCHAR);
		inOrder.verify(statement).registerOutParameter(5, OracleTypes.VARCHAR);
		inOrder.verify(statement).registerOutParameter(6, OracleTypes.VARCHAR);
	}
}
