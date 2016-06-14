package springbook.user.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionHandler implements InvocationHandler{
	
	private Object target; //�ΰ������ ������ Ÿ�� ������Ʈ
	private PlatformTransactionManager transactionManager; //Ʈ����� ����� �����ϴ� �� �ʿ��� Ʈ����� �Ŵ���
	private String pattern; //Ʈ������� ������ �޼ҵ� �̸� ����

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(method.getName().startsWith(pattern)){
			return invokeInTransaction(method, args);//Ʈ����� ���� ��� �޼ҵ带 �����ؼ� Ʈ����� ��輳�� ����� �ο����ش�.
		}else{
			return method.invoke(target, args);
		}
	}
	
	private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); //DI ���� Ʈ����� �Ŵ����� �����ؼ� ���. ��Ƽ������ ȯ�濡���� ����.
		
		try{
			Object ret = method.invoke(target, args);
			this.transactionManager.commit(status);
			return ret;
		}catch(InvocationTargetException e){
			this.transactionManager.rollback(status);
			throw e.getTargetException();
		}
	}
}
