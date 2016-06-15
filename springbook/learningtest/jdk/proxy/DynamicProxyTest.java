package springbook.learningtest.jdk.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import springbook.learningtest.jdk.UppercaseHandler;

public class DynamicProxyTest {
	@Test
	public void simpleProxy(){
		// JDK ���̳��� ���Ͻ� ����
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(
					getClass().getClassLoader(),
					new Class[] {Hello.class},
					new UppercaseHandler(new HelloTarget())
				);
	}
	
	@Test
	public void proxyFactoryBean(){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());//Ÿ�� ����
		pfBean.addAdvice(new UppercaseAdvice());//�ΰ������ ���� �����̽��� �߰��Ѵ�. �������� �߰��� ���� �ִ�.
		
		Hello proxiedHello = (Hello) pfBean.getObject(); //FactoryBean�̹Ƿ� getObject()�� ������ ���Ͻø� �����´�.
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
	
	static class UppercaseAdvice implements MethodInterceptor {
		//MethodInvocation�� ������ �ݹ� ������Ʈ. �̱������� �ΰ� ������ �����ϴٴ� ����!
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String ret = (String) invocation.proceed();//���÷����� Method�� �޸� �޼ҵ� ���� �� Ÿ�� ������Ʈ�� ������ �ʿ䰡 ����. MethodInvocation�� �޼ҵ� ������ �Բ� Ÿ�� ������Ʈ�� �˰��ֱ� ����
			return ret.toUpperCase();//�ΰ���� ����
		}
	}
	
	// ����Ʈ�Ʊ��� ������ �޼ҵ�
	// �������� �ΰ������ �����ϴ� ������Ʈ�� �����̽���� �θ���
	// �޼ҵ� ���� �˰����� ���� ������Ʈ�� ����Ʈ���̶�� �θ���!
	@Test
	public void pointcutAdvisor(){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());//Ÿ�� ����
		
		// �޼ҵ� �̸��� ���ؼ� ����� �����ϴ� �˰����� �����ϴ� ����Ʈ�� ����
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*"); //sayH�� �����ϴ� ��� �޼ҵ带 �����ϰ� �Ѵ�
		
		// ����Ʈ�ư� �����̽��� Advisor�� ��� �� ���� �߰�
		// �������� �����̽��� ��ϵǴ��� ���� �ٸ� ����Ʈ�ư� ���յ� �� �ֱ� ������ ���� �ٸ� �޼ҵ� ������� ���� ����!
		// �������� = ����Ʈ��(�޼ҵ� ���� �˰���) + �����̽�(�ΰ����)
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice())); 
		//pfBean.addAdvice(new UppercaseAdvice());//�ΰ������ ���� �����̽��� �߰��Ѵ�. �������� �߰��� ���� �ִ�.
		
		Hello proxiedHello = (Hello) pfBean.getObject(); //FactoryBean�̹Ƿ� getObject()�� ������ ���Ͻø� �����´�.
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby")); //�޼ҵ� �̸��� ����Ʈ���� �������ǿ� ���� �����Ƿ�, �ΰ����(�빮�ں�ȯ)�� ������� ����
	}
	
	
	static interface Hello { //Ÿ��� ���Ͻð� ������ �������̽�
		String sayHello(String name);
		String sayHi(String name);
		String sayThankYou(String name);
	}
	
	static class HelloTarget implements Hello {
		public String sayHello(String name) {
			return "Hello "+ name;
		}

		public String sayHi(String name) {
			return "Hi " + name;
		}

		public String sayThankYou(String name) {
			return "Thank You "+ name;
		}
	}
}
