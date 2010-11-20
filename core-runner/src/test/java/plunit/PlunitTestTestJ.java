package plunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import plunit.db.PlunitStatement;

public class PlunitTestTestJ {
	PlunitTest plunitTest;
	MockPlunitStatement mockPlunitStatement;
	
	@Mock PlunitStatement mockitoPlunitStatement;
	
	@Before
	public void setup() {
		mockPlunitStatement = new MockPlunitStatement();
		plunitTest = new PlunitTest("testName", null, mockPlunitStatement);
	}
	@Test
	public void nameIsSetThroughConstructor() {
		String testName = "testName";
		PlunitTest test = new PlunitTest(testName, null, null);
		assertEquals(testName, test.getName());
	}
	@Test
	public void testInitialStateIsNotRan() {
		assertEquals(TestState.NOT_RAN, new PlunitTest(null, null, null).getState());
	}
	@Test
	public void plunitTestExecutesStatementWhenRan() throws SQLException {
		plunitTest.run();
		
		assertTrue("Exceute should be called on PlunitStatement when test is ran", mockPlunitStatement.executeCalled);
		assertEquals("Test name should be passed to statement", plunitTest.getName(), mockPlunitStatement.executeTestNameReceived);
	}
	@Test
	public void plunitTestReturnsResultReturnedFromStatement() throws SQLException {
		plunitTest.run();
		
		assertEquals(plunitTest.getResult(), mockPlunitStatement.executeReturnValue);
	}
	
	@Test
	public void stateIsSetToPassingIfResultFromStatementIsPassing() throws SQLException {
		mockPlunitStatement.executeReturnValue.isPassingReturnValue = true;				
				
		plunitTest.run();
		
		assertEquals(TestState.PASSING, plunitTest.getState());
	}
	@Test
	public void stateIsSetToFailingIfResultFromStatementIsPassing() throws SQLException {
		mockPlunitStatement.executeReturnValue.isPassingReturnValue = false;				
		plunitTest.run();
		
		assertEquals(TestState.FAILING, plunitTest.getState());
	}
	
	@Test
	public void stateIsRunningWhileStatementIsExecuting() throws SQLException {
		plunitTest.run();
		
		assertEquals(TestState.RUNNING, mockPlunitStatement.testStateWhileExecuting);
	}
	@Test
	public void isPassingReturnsFalseIfResultIsNull() {
		assertEquals(null, plunitTest.getResult());
		assertFalse(plunitTest.isPassing());
	}
	@Test
	public void isPassingReturnsValueOfResultIsPassing() {
		mockPlunitStatement.executeReturnValue.isPassingReturnValue = true;
		plunitTest.run();
		assertTrue(plunitTest.isPassing());

		mockPlunitStatement.executeReturnValue.isPassingReturnValue = false;
		plunitTest.run();
		assertFalse(plunitTest.isPassing());
	}
	@Test
	public void stateIsSetToNotRanWhenTestIsReset() {
		plunitTest.run();
		assertTrue(TestState.NOT_RAN != plunitTest.getState());
		
		plunitTest.reset();
		
		assertEquals(TestState.NOT_RAN, plunitTest.getState());
	}
	@Test
	public void resultIsSetToNullWhenTestIsReset() {
		plunitTest.run();
		assertTrue(null != plunitTest.getResult());
		
		plunitTest.reset();
		
		assertEquals(null, plunitTest.getResult());
	}
	
	public class MockPlunitStatement extends PlunitStatement {
		public String executeTestNameReceived;
		public boolean executeCalled;
		public MockTestResult executeReturnValue = new MockTestResult();
		public TestState testStateWhileExecuting;
		
		@Override
		public TestResult execute(String testName) {
			testStateWhileExecuting = plunitTest.getState();
			executeCalled = true;
			executeTestNameReceived = testName;
			return executeReturnValue;
		}
	}
	
	public static class MockTestResult extends TestResult {
		public boolean isPassingReturnValue;
		@Override
		public boolean isPassing() {
			return isPassingReturnValue;
		}
	}
}
