package plunit.loaders;

import static org.junit.Assert.*;
import java.sql.Connection 
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks 
import org.mockito.Mock 
import org.mockito.runners.MockitoJUnitRunner;
import plunit.db.PlunitStatement 

@RunWith(MockitoJUnitRunner.class)
class SuiteOfSuitesLoaderTests {
	@InjectMocks SuiteOfSuitesLoader loader = new SuiteOfSuitesLoader()
	@Mock SuiteOfSuitesLoader suiteLoader 
	@Mock PlunitStatement plunitStatement
	@Mock Connection connection
	
	@Test
    void createPlunit() {
		def suiteName = 'suite name'
		loader.load suiteName, plunitStatement, connection
	}	
}
