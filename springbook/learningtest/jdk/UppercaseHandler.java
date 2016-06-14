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
	//� ������ �������̽��� ������ Ÿ�꿡�� ���� �����ϵ��� Object Ÿ������ ����
	Object target;
	
	// ���̳��� ���Ͻ÷κ��� ���޹��� ��û�� �ٽ� Ÿ�� ������Ʈ�� �����ؾ� �ϱ� ������ Ÿ�� ������Ʈ�� ���Թ޾� �д�.
	public UppercaseHandler(Object target){
		this.target = target;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object ret = method.invoke(target, args);//Ÿ������ ����. �������̽��� �޼ҵ� ȣ�⿡ ��� ����ȴ�.
		
		// ȣ���� �żҵ��� ���� Ÿ���� String�� ��츸 �빮�� ���� ����� �����ϵ��� ����
		if(ret instanceof String && method.getName().startsWith("say")){//���� Ÿ�԰� �޼ҵ� �̸��� ��ġ�ϴ� ��쿡�� �ΰ������ �����Ѵ�.
			return ((String) ret).toUpperCase();//�ΰ���� ����
		}else{
			return ret;
		}
	}
}
