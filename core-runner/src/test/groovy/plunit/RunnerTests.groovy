package plunit;
import static org.mockito.Mockito.verify;

import static org.junit.Assert.*
import java.sql.Connection 
import org.junit.Test 
import org.junit.runner.RunWith 
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner 
import plunit.db.PlunitStatement 
import plunit.loaders.PlunitTestLoader
import plunit.runner.Runner;


@RunWith(MockitoJUnitRunner.class)
class RunnerTests {
	@InjectMocks Runner runner = new Runner()
	@Mock PlunitTestLoader testLoader
	@Mock PlunitStatement statement
	@Mock Connection connection
	
	@Test
	void loadGivenTest() {
		runner.run('test package', connection)
		
		verify(testLoader).load('test package', statement, connection)
	}
}
