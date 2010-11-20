package plunit;

public class InvalidPlunitTestException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidPlunitTestException() {}
	
	public InvalidPlunitTestException(String msg) {
		super(msg);
	}
}
