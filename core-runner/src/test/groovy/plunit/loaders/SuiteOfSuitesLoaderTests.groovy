package plunit.loaders;

import static org.junit.Assert.*;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks 
import org.mockito.Mock 
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
class SuiteOfSuitesLoaderTests {
	@InjectMocks SuiteOfSuitesLoader loader = SuiteOfSuitesLoader()
	@Mock SuiteLoader suiteLoader 
	
	@Test
    void createPlunit() {
		loader.load suiteName, plunitStatement, connection
	}	
}
