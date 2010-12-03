package plunit.loaders;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.InjectMocks;
import java.sql.Connection;

import plunit.db.LoadSuiteStatement;
import plunit.db.PlunitStatement 
import plunit.loaders.SuiteLoader;
import plunit.PlunitTest;
import plunit.Testable 

@RunWith(MockitoJUnitRunner.class)
class SuiteLoaderTests {
	@InjectMocks SuiteLoader testLoader = new SuiteLoader()
	@Mock LoadSuiteStatement loadTestStatement
	@Mock Connection connection
	@Mock PlunitStatement plunitStatement
	
	@Test
	void loadASingleSuiteOfPlunitTest() {
		def testNames =  ['test_one~test one description', 'test_two~test two description']
		
		List<Testable> actualTests = testLoader.load(testNames, plunitStatement, connection)
		
		assert 2 == actualTests.size()
		assert [plunitTest('test_one', 'test one description'), plunitTest('test_two', 'test two description')] == actualTests
	}
	@Test
	public void noDescriptionForTest() {
		def testNames =  ['suite_one~null']
		
		List<Testable> actualTests = testLoader.load(testNames, plunitStatement, connection)
		
		assert 1 == actualTests.size()
		assert 'suite_one' == actualTests[0].name
		assert null == actualTests[0].description
	}
	@Test
	void testWithNullDescription() {
		def testNames =  ['name~null']

		List<Testable> actualTests = testLoader.load(testNames, plunitStatement, connection)
		
		assert 1 == actualTests.size()
		assert 'name' == actualTests[0].getName()
		assert null == actualTests[0].getDescription()
	}
	private PlunitTest plunitTest(String name, String description) {
		new PlunitTest(name, description, plunitStatement)
	}
}
