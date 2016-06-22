package springbook.user.sqlservice;

public class SqlRetrievalFailureException extends RuntimeException {
	public SqlRetrievalFailureException(String message){
		super(message);
	}
	
	// SQL�� �������� �� ������ �ٺ� ������ ���� �� �ֵ��� ��ø ���ܸ� ������ �� �ִ� ������
	public SqlRetrievalFailureException(String message, Throwable cause){
		super(message, cause);
	}
	
	public SqlRetrievalFailureException(Throwable cause){
		super(cause);
	}
}
