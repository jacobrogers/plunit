package plunit;

import java.sql.Connection 
import java.sql.DriverManager 
import org.junit.Test 

import static org.junit.Assert.*;

class PlunitTestSuiteIntegrationTest {
	@Test
	public void smokeTest() {
		String url = 'jdbc:oracle:thin:@desktop:1521:XE'
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection dbConn = DriverManager.getConnection(url, "jacobrogers", "Intrud3r");

		def testSuite = PlunitTestSuite.build('TEST_CALC_COMM_PERCENT', dbConn)
		testSuite.run()
		
		assert testSuite.tests.size() == 2
		assert testSuite.passingTests.size() == 1
		assert testSuite.failingTests.size() == 1
	}
	
	@Test
	public void smokeTest_suiteOfSuites() {
		String url = 'jdbc:oracle:thin:@desktop:1521:XE'
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection dbConn = DriverManager.getConnection(url, "jacobrogers", "Intrud3r");

		def testSuite = PlunitTestSuite.build('PLUNIT_SUITE_OF_SUITES', dbConn)
		testSuite.run()
		
		assert testSuite.tests.size() == 1
		assert testSuite.passingTests.size() == 0
		assert testSuite.failingTests.size() == 1
		assert testSuite.tests[0] instanceof PlunitTestSuite
	}
}
