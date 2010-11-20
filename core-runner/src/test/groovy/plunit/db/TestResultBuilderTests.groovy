package plunit.db;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import plunit.TestResult;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;
import org.junit.runner.RunWith;
import java.sql.CallableStatement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
class TestResultBuilderTests {
	
	TestResultBuilder builder = new TestResultBuilder()
	@Mock CallableStatement statement
	
	@Before
	public void setup() {
		when(statement.getObject(3)).thenReturn 1
	}
	@Test
	void testIsPassingIfResultIsOne() {
		when(statement.getObject(3)).thenReturn 1
		
		TestResult result = builder.build(statement)
		
		assert result.isPassing()
	}
	@Test
	void testIsPassingIfResultIsNotOne() {
		when(statement.getObject(3)).thenReturn 0
		
		TestResult result = builder.build(statement)
		
		assert !result.isPassing()
	}
	@Test
	void errorMessageIsSet() {
		when(statement.getObject(4)).thenReturn "error message"
		
		TestResult result = builder.build(statement)
		
		assert "error message" == result.getErrorMessage()
	}
	@Test
	public void nullErrorMessage() {
		when(statement.getObject(4)).thenReturn null
		
		TestResult result = builder.build(statement)
		
		assert "No error message was found" == result.getErrorMessage()
	}
	@Test
	void callStackIsSet() {
		when(statement.getObject(5)).thenReturn "call stack"
		
		TestResult result = builder.build(statement)
		
		assert "call stack" == result.getCallStack()
	}
	@Test
	void nullCallStack() {
		when(statement.getObject(5)).thenReturn null
		
		TestResult result = builder.build(statement)
		
		assert "No call stack was found" == result.getCallStack()
	}
	@Test
	void dbmsOutputIsSet() {
		when(statement.getObject(6)).thenReturn "dbms output"
		
		TestResult result = builder.build(statement)
		
		assert "dbms output" == result.getDbmsOutput()
	}
}
