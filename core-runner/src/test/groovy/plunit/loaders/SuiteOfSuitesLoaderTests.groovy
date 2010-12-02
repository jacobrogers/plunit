package plunit.loaders;

import static org.junit.Assert.*
import java.sql.Connection 
import org.junit.Test

import org.junit.runner.RunWith
import org.mockito.InjectMocks 
import org.mockito.Mock 
import org.mockito.runners.MockitoJUnitRunner
import plunit.PlunitTest 
import plunit.PlunitTestSuite 
import plunit.Testable 
import plunit.db.LoadSuiteStatement 
import plunit.db.PlunitStatement
 
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

@RunWith(MockitoJUnitRunner.class)
class SuiteOfSuitesLoaderTests {
	@InjectMocks SuiteOfSuitesLoader loader = new SuiteOfSuitesLoader()
	@Mock SuiteLoader suiteLoader 
	@Mock PlunitStatement plunitStatement
	@Mock Connection connection
	@Mock LoadSuiteStatement loadSuiteStatement
	
	@Test
    void createPlunitTestsFromListOfSuites() {
		def suiteName = 'suite-one,suite-two'
		def suiteOneTests = ['test-one'], suiteTwoTests = ['test-two']
		def testableListOne = [new PlunitTest()], testableListTwo = [new PlunitTest()]
		
		when(loadSuiteStatement.loadTests('suite-one', connection)).thenReturn(suiteOneTests)
		when(loadSuiteStatement.loadTests('suite-two', connection)).thenReturn(suiteTwoTests)
		when(suiteLoader.load(suiteOneTests, plunitStatement, connection)).thenReturn(testableListOne)
		when(suiteLoader.load(suiteTwoTests, plunitStatement, connection)).thenReturn(testableListTwo)
		
		List<Testable> suites = loader.load(suiteName, plunitStatement, connection)
		
		assert 2 == suites.size()
		assert suites[0] instanceof PlunitTestSuite
		assert suites[1] instanceof PlunitTestSuite
		assert 'suite-one' == suites[0].name
		assert testableListOne == suites[0].tests
		assert 'suite-two' == suites[1].name
		assert testableListTwo == suites[1].tests
		
	}	
}
