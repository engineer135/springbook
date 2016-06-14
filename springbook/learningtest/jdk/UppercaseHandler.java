package springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Engineer135
 * 
 * InvocationHandler�� ���̳��� ���Ͻ� ����
 *
 */
public class UppercaseHandler implements InvocationHandler {
	Hello target;
	
	// ���̳��� ���Ͻ÷κ��� ���޹��� ��û�� �ٽ� Ÿ�� ������Ʈ�� �����ؾ� �ϱ� ������ Ÿ�� ������Ʈ�� ���Թ޾� �д�.
	public UppercaseHandler(Hello target){
		this.target = target;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String ret = (String) method.invoke(target, args);//Ÿ������ ����. �������̽��� �޼ҵ� ȣ�⿡ ��� ����ȴ�.
		return ret.toUpperCase();//�ΰ���� ����
	}
}
