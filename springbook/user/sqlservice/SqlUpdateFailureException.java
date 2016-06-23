package springbook.user.sqlservice;

public class SqlUpdateFailureException extends RuntimeException {
	public SqlUpdateFailureException(String message){
		super(message);
	}
}
