package plunit.runner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement 
import java.sql.Connection;

import oracle.jdbc.OracleTypes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import plunit.PlunitTestSuite 
import plunit.Testable 
import plunit.loaders.PlunitTestLoader 

@RunWith(MockitoJUnitRunner.class)
class RunnerFactoryTests {
	@InjectMocks RunnerFactory factory = new RunnerFactory()
	@Mock Connection connection
	@Mock CallableStatement callableStatement
	@Mock PlunitTestLoader testLoader
	
	@Test
	void prepareCallableStatementForRunner() {
		when(connection.prepareCall('begin plunit_wrapper.run_test(?,?,?,?,?,?); end;')).thenReturn(callableStatement)
		
		factory.build('suite name', connection)
		
		verify(connection).prepareCall('begin plunit_wrapper.run_test(?,?,?,?,?,?); end;')
		verify(callableStatement).setString(1, 'suite name')
		verify(callableStatement).registerOutParameter(3, OracleTypes.INTEGER)
		verify(callableStatement).registerOutParameter(4, OracleTypes.VARCHAR)
		verify(callableStatement).registerOutParameter(5, OracleTypes.VARCHAR)
		verify(callableStatement).registerOutParameter(6, OracleTypes.VARCHAR)
	}
	@Test
	void returnRunnerWithCorrectDependencies() {
		when(connection.prepareCall('begin plunit_wrapper.run_test(?,?,?,?,?,?); end;')).thenReturn(callableStatement)
		
		Runner runner = factory.build('suite name', connection)
		
		assert 'suite name' == runner.suiteName
		assert connection == runner.connection
		assert callableStatement == runner.callableStatement
	}
	@Test
	void loadTestsForSuite() {
		PlunitTestSuite test = new PlunitTestSuite()
		when(testLoader.load('suite name',connection)).thenReturn(test)
		when(connection.prepareCall('begin plunit_wrapper.run_test(?,?,?,?,?,?); end;')).thenReturn(callableStatement)
		
		Runner runner = factory.build('suite name', connection)
		
		assert test == runner.getTests()
	}
}
