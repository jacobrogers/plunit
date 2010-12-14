package plunit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import plunit.db.LoadSuiteStatement;
import plunit.db.PlunitStatement;
import plunit.loaders.PlunitTestLoader;
import plunit.observers.DefaultPopUp;
import plunit.observers.PopUp;
import plunit.observers.TestSuiteListener;

public class PlunitTestSuite implements Testable, TestSuiteListener {
	private String packageName;
	private Connection dbConnection;
	private List<Testable> testList = new ArrayList<Testable>();
	private List<Testable> testListBackup;
	private List<TestSuiteListener> observers = new ArrayList<TestSuiteListener>();
	private TestState state;
	private int testNumber = 0;
	private PlunitStatement plunitStatement;
	private LoadSuiteStatement loadSuiteStatement;
	private PopUp popup = new DefaultPopUp();
	
	public static PlunitTestSuite build(String packageName, Connection dbConnection) throws SQLException {
		PlunitTestSuite testSuite = new PlunitTestSuite(packageName, dbConnection);
		testSuite.loadPlunitTestList();
		return testSuite;
	}
	
	public PlunitTestSuite(String packageName, List<Testable> tests, Connection dbConnection) {
		this.testList = tests;
		this.packageName = packageName;
		this.dbConnection = dbConnection;
	}
	private PlunitTestSuite(String packageName, Connection dbConnection) throws SQLException {
		super();
		this.packageName = packageName;
		this.dbConnection = dbConnection;
		this.plunitStatement = new PlunitStatement(this.getName(), dbConnection);
		this.loadSuiteStatement = new LoadSuiteStatement(dbConnection);
		this.state = TestState.NOT_RAN;
	}
	
	public void addListener(TestSuiteListener observer) {
		popup.show("adding listener");
		this.observers.add(observer);
	}

	public void run(Testable test) throws InvalidPlunitTestException, SQLException {
		List<Testable> tests = new ArrayList<Testable>();
		tests.add(test);

		if (null == testListBackup) {
			testListBackup = testList;
		}
		testList = tests;
		run();
	}
	
	public void run() throws SQLException {
		try {
			checkIfSuiteIsValid();
			reset();
			notifyObserversTestsAreStarting();
			runTests();
			notifyObserversTestsHaveFinished();
			setState();
		} catch(InvalidPlunitTestException e) {
			popup.show(e);
		} catch(SQLException e) {
			popup.show(e);
		}
	}

	private void setState() {
		state = isPassing() ? TestState.PASSING : TestState.FAILING;
	}

	public boolean isPassing() {
		for (Testable test : testList) {
			if (!test.isPassing())
				return false;
		}
		return true;
	}

	public TestResult getResult() {
		return new TestResult();
	}

	public List<Testable> getTests() {
		return testList;
	}

	public TestState getState() {
		return state;
	}

	public List<Testable> getPassingTests() {
		List<Testable> passingPlunitTests = new ArrayList<Testable>();
		for (Testable plunitTest : testList) {
			if (plunitTest.isPassing()) {
				passingPlunitTests.add(plunitTest);
			}
		}
		return passingPlunitTests;
	}

	public List<Testable> getFailingTests() {
		List<Testable> failingPlunitTests = new ArrayList<Testable>();
		for (Testable plunitTest : testList) {
			if (!plunitTest.isPassing()) {
				failingPlunitTests.add(plunitTest);
			}
		}
		return failingPlunitTests;
	}

	public String getName() {
		return packageName;
	}

	private void loadPlunitTestList() throws SQLException {
		popup.show("loading tests");
		PlunitTestSuite testSuite = new PlunitTestLoader(loadSuiteStatement).load(getName(), plunitStatement, dbConnection);
		for(PlunitTestSuite suite : testSuite.getSuites()) {
			suite.addListener(this);
			suite.setNotifier(popup);
		}
		testList.addAll(testSuite.getTests());
	}
	
	public List<PlunitTestSuite> getSuites() {
		List<PlunitTestSuite> suites = new ArrayList<PlunitTestSuite>();
		for(Testable test : testList) {
			if(test instanceof PlunitTestSuite) suites.add((PlunitTestSuite) test);
		}
		return suites;
	}
	
	public void reset() throws SQLException {
		testNumber = 0;
		this.state = TestState.NOT_RAN;
		plunitStatement = new PlunitStatement(this.getName(), dbConnection);
		for (Testable test : testList) {
			test.reset();
			test.setPlunitStatement(plunitStatement);
		}
	}

	private void runTests() throws InvalidPlunitTestException, SQLException {
		popup.show("suite is running tests");
		state = TestState.RUNNING;
		int testCount = 0;
		for (final Testable test : testList) {
			test.run();
			++testCount;
			popup.show("notifying...");
			if(!(test instanceof PlunitTestSuite))  
				for (TestSuiteListener listener : observers) 
					listener.notify(this, testCount);
		}
		plunitStatement.close();
	}

	private void notifyObserversTestsAreStarting() {
		for (TestSuiteListener listener : observers) {
			listener.notifyStart(getNumberOfTests());
		}
	}
	
	private void notifyObserversTestsHaveFinished() {
		for (TestSuiteListener listener : observers) {
			listener.notifyFinish();
		}
	}

	private int getNumberOfTests() {
		int size = 0;
		for(Testable test : testList) {
			if(test instanceof PlunitTestSuite)
				size += ((PlunitTestSuite)test).getTests().size();
			else
				size++;
		}
		return size;
	}

	private void checkIfSuiteIsValid() throws InvalidPlunitTestException {
		if (0 == testList.size()) {
			throw new InvalidPlunitTestException(this.getName() + " is not a valid Plunit Test");
		}
	}

	public void runSuite() throws InvalidPlunitTestException, SQLException {
		if (null != testListBackup) {
			testList = testListBackup;
		}
		run();
	}
	
	public void notify(PlunitTestSuite testSuite, int n) {
		++testNumber;
		for(TestSuiteListener observer : observers) { 
			observer.notify(this, testNumber);
		}
		popup.show("testNumber: " + testNumber);
	}

	public void notifyFinish() {}
	public void notifyStart(int numberOfTests) {}

	public Connection getConnection() {
		return dbConnection;
	}

	public void setNotifier(PopUp notifier) {
		notifier.show("setting popup on suite");
		this.popup = notifier;
	}

	@Override
	public String toString() {
		if(state == TestState.RUNNING) {
			return state.toString();
		} else {
			return getTests().size() + " tests, " + getPassingTests().size() + " passing, " + getFailingTests().size() + " failing";
		}
	}

	public void setPlunitStatement(PlunitStatement plunitStatement) {
		this.plunitStatement = plunitStatement;
	}

	public String getDescription() {
		return getName();
	}
}