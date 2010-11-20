/**
 * 
 */
package plunit;

public enum TestState { 
	PASSING("Passing"), FAILING("Failing"), NOT_RAN("Not Ran"), RUNNING("Running");
    private String toString;
	TestState(String toString) {
		this.toString = toString;
	}
	public String toString() {
		return toString; 
	}
}