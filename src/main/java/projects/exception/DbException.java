package projects.exception;

@SuppressWarnings("serial")
public class DbException extends RuntimeException{
	// Extension of RuntimeException(String):
	public DbException(String message) {
		super(message);
	}
	// Extension of RuntimeException(Throwable):
	public DbException(Throwable cause) {
		super(cause);
	}
	// Extension of RuntimeException(String, Throwable):
	public DbException(String message, Throwable cause) {
		super(message,cause);
	}
}
