package springbook.user.service;

import java.lang.reflect.InvocationTargetException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {

	PlatformTransactionManager transactionManager;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	// Ÿ���� ȣ���ϴ� ����� ���� �ݹ� ������Ʈ�� ���Ͻ÷κ��� �޴´�.
	// ���п� �����̽��� Ư�� Ÿ�꿡 �������� �ʰ� ���� �����ϴ�.
	public Object invoke(MethodInvocation invocation) throws Throwable {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); //DI ���� Ʈ����� �Ŵ����� �����ؼ� ���. ��Ƽ������ ȯ�濡���� ����.
		
		try{
			// �ݹ��� ȣ���ؼ� Ÿ���� �޼ҵ带 �����Ѵ�.
			// Ÿ�� �޼ҵ� ȣ�� ���ķ� �ʿ��� �ΰ������ ���� �� �ִ�.
			// ��쿡 ���� Ÿ���� �ƿ� ȣ����� �ʰ� �ϰų� ��õ��� ���� �ݺ����� ȣ�⵵ �����ϴ�.
			Object ret = invocation.proceed();
			this.transactionManager.commit(status);
			return ret;
		}catch(RuntimeException e){// JDK ���̳��� ���Ͻð� �����ϴ� Method�ʹ� �޸� �������� MethodInvocation�� ���� Ÿ��ȣ���� ���ܰ� ������� �ʰ� Ÿ�꿡�� ���� �״�� ���޵ȴ�.
			this.transactionManager.rollback(status);
			throw e;
		}
	}

}
