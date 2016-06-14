package springbook.learningtest.jdk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;

public class ReflectionTest {
	@Test
	public void invokeMethod() throws Exception {
		String name = "Spring";
		
		//length()
		assertThat(name.length(), is(6));
		
		Method lengthMethod = String.class.getMethod("length");
		assertThat(lengthMethod.invoke(name), is(6));
		
		//chartAt()
		assertThat(name.charAt(0), is('S'));
		
		Method charAtMethod = String.class.getMethod("charAt", int.class);
		assertThat(charAtMethod.invoke(name, 0), is('S'));
		
	}
	
	@Test
	public void simpleProxy(){
		Hello hello = new HelloTarget(); //Ÿ���� �������̽��� ���� �����ϴ� ������ ������
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("Toby"), is("Hi Toby"));
		assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));
		
		//���Ͻø� ���� Ÿ�� ������Ʈ�� �����ϵ��� �����Ѵ�.
		//Hello proxiedHello = new HelloUppercase(new HelloTarget());
		
		//InvocationHandler�� ������ ���̳��� ���Ͻ� ����
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(
										getClass().getClassLoader(), //�������� �����Ǵ� ���̳��� ���Ͻ� Ŭ������ �ε��� ����� Ŭ���� �δ�
										new Class[]{Hello.class}, //������ �������̽�
										new UppercaseHandler(new HelloTarget())); //�ΰ���ɰ� ���� �ڵ带 ���� invocationHandler
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
}
