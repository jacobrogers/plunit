package plunit.observers;

import plunit.PlunitTestSuite;

public interface TestSuiteListener {
   public abstract void notify( PlunitTestSuite testSuite, int testNumber);
   public abstract void notifyStart(int numberOfTests);
   public abstract void notifyFinish();
}
