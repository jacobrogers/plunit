package plunit;

import plunit.db.PlunitStatement;

public class PlunitTest implements Testable {
    private String name;
	private String description;
	private TestResult result;
	private PlunitStatement plunitStatement;
	private TestState state;
	
	public PlunitTest() {}
	
	public PlunitTest(String name, String description, PlunitStatement plunitStatement) {
		this.name = name;
		this.description = description;
		this.plunitStatement = plunitStatement;
		this.state = TestState.NOT_RAN;
	}
	public boolean isPassing() {
		return null != result && result.isPassing();
	}
	public void run() {
		state = TestState.RUNNING;
		result = plunitStatement.execute(getName());
		state = result.isPassing() ? TestState.PASSING : TestState.FAILING;
	}
	public void reset() {
		state = TestState.NOT_RAN;
		result = null;
	}
	public TestState getState() {
	   return state;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public TestResult getResult() {
		return result;
	}
	public void setPlunitStatement(PlunitStatement plunitStatement) {
		this.plunitStatement = plunitStatement;
	}
	@Override
	public String toString() {
		return state == TestState.FAILING ? getResult().toString() : state.toString();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PlunitTest other = (PlunitTest) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		return result;
	}
}
