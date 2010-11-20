package plunit.observers;

public interface PopUp {
	public abstract void show(String message);
	public abstract void show(Exception e);
	public abstract void show(RuntimeException e);
}
