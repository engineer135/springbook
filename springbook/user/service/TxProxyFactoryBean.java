package springbook.user.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public class TxProxyFactoryBean implements FactoryBean<Object>{ //������ ������Ʈ Ÿ���� ������ ���� ������ ���������� ����ϱ� ���� Object�� �ߴ�
	
	//TransactionHandler�� ������ �� �ʿ�
	Object target;
	PlatformTransactionManager transactionManager;
	String pattern;
	
	//���̳��� ���Ͻø� �����Ҷ� �ʿ�. UserService ���� �������̽��� ���� Ÿ�꿡�� ������ �� �ִ�.
	Class<?> serviceInterface;

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	// FactoryBean �������̽� ���� �޼ҵ�
	// �̷��� �ϸ� ���̳��� ���Ͻõ� �� ������ �����ϰ� �Ǵ� �ű���! 
	// �׷��� ���丮�� �׽�Ʈ�� �߱��� �̰� ���ؼ�!!
	// UppercaseHandler�� ���� �۾��߰�, �� �� FactoryBeanTest�� �� �� ���⿡ ������ �� ����!
	public Object getObject() throws Exception {//DI ���� ������ �̿��ؼ� TransactionHandler�� ����ϴ� ���̳��� ���Ͻø� �����Ѵ�.
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(target);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern(pattern);
		return Proxy.newProxyInstance(
				getClass().getClassLoader(), //�������� �����Ǵ� ���̳��� ���Ͻ� Ŭ������ �ε��� ����� Ŭ���� �δ�
				new Class[] {serviceInterface}, //������ �������̽�
				txHandler); //�ΰ���ɰ� ���� �ڵ带 ���� invocationHandler
	}

	public Class<?> getObjectType() {
		return serviceInterface; // ���丮 ���� �����ϴ� ������Ʈ�� Ÿ���� DI ���� �������̽� Ÿ�Կ� ���� �޶�����. ���� �پ��� Ÿ���� ���Ͻ� ������Ʈ ������ ���� ����.
	}

	public boolean isSingleton() {
		return false; // �̱��� ���� �ƴ϶�� ���� �ƴ϶� getObject()�� �Ź� ���� ������Ʈ�� �������� �ʴ´ٴ� �ǹ��̴�.
	}
}
