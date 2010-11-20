package plunit;

public class TestResult {
	private boolean isPassing;
	private String errorMessage = "";
	private String callStack = "";
	private String dbmsOutput = "";
	
	public TestResult() {
		super();
	}

	public boolean isPassing() {
		return isPassing;
	}

	public void setPassing(boolean isPassing) {
		this.isPassing = isPassing;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getCallStack() {
		return callStack;
	}

	public void setCallStack(String callStack) {
		this.callStack = callStack;
	}

	public void setDbmsOutput(String dbmsOutput) {
		this.dbmsOutput = dbmsOutput;
	}

	public String getDbmsOutput() {
		return dbmsOutput;
	}

	@Override
	public String toString() {
		String result = isPassing() ? "Passing!" : getErrorMessage() + "\n" + getCallStack();
				
		if(null != dbmsOutput) {
			result += "\n\n===========\nDBMS Output\n===========\n" + getDbmsOutput();
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((callStack == null) ? 0 : callStack.hashCode());
		result = prime * result
				+ ((dbmsOutput == null) ? 0 : dbmsOutput.hashCode());
		result = prime * result
				+ ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + (isPassing ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TestResult other = (TestResult) obj;
		if (callStack == null) {
			if (other.callStack != null)
				return false;
		} else if (!callStack.equals(other.callStack))
			return false;
		if (dbmsOutput == null) {
			if (other.dbmsOutput != null)
				return false;
		} else if (!dbmsOutput.equals(other.dbmsOutput))
			return false;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (isPassing != other.isPassing)
			return false;
		return true;
	}

}