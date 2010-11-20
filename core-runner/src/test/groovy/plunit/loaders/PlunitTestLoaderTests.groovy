package plunit.loaders;

import plunit.db.PlunitStatement 
import java.sql.Connection 
import org.junit.Test 
import org.junit.runner.RunWith;
import org.mockito.InjectMocks 
import org.mockito.Mock 
import org.mockito.runners.MockitoJUnitRunner;
import plunit.db.LoadSuiteStatement 

import static org.junit.Assert.*;
import static org.mockito.Mockito.when
import static org.mockito.Mockito.then
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.never
import static org.mockito.Matchers.any

@RunWith(MockitoJUnitRunner.class)
class PlunitTestLoaderTests {
	
	@InjectMocks PlunitTestLoader plunitTestLoader = new PlunitTestLoader()
	@Mock LoadSuiteStatement loadSuiteStatement
	@Mock SuiteOfSuitesLoaderTests suiteOfSuitesLoader
	@Mock SuiteLoader suiteLoader
	@Mock Connection connection
	@Mock PlunitStatement plunitStatement
	
	@Test
	void loadSuiteOfSuitesWhenReturningTestNameHasCommas() {
		String suiteName = 'suiteOfSuites'
		when(loadSuiteStatement.loadTests(suiteName, connection)).thenReturn(['suite-one,suite-two'])
	
		plunitTestLoader.load(suiteName, plunitStatement, connection)
		
		verify(suiteOfSuitesLoader).load('suite-one,suite-two'.toUpperCase(), plunitStatement, connection)
		verify(suiteLoader, never()).load(any(), any(), any())
	}
	
	@Test
	void loadSingleSuiteIfReturningTestNameDoesNotContainCommas() {
		def suiteName = 'singleSuite', returnedTests = ['test-one~this is test one', 'test-two~this is test two']
		when(loadSuiteStatement.loadTests(suiteName, connection)).thenReturn(returnedTests)
		
		plunitTestLoader.load(suiteName, plunitStatement, connection)
		
		verify(suiteLoader).load(returnedTests, plunitStatement, connection)
		verify(suiteOfSuitesLoader, never()).load(any(), any(), any())
	} 
}
