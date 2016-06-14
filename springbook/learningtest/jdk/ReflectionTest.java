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
		Hello hello = new HelloTarget(); //타겟은 인터페이스를 통해 접근하는 습관을 들이자
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("Toby"), is("Hi Toby"));
		assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));
		
		//프록시를 통해 타깃 오브젝트에 접근하도록 구성한다.
		//Hello proxiedHello = new HelloUppercase(new HelloTarget());
		
		//InvocationHandler를 구현한 다이내믹 프록시 생성
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(
										getClass().getClassLoader(), //동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
										new Class[]{Hello.class}, //구현할 인터페이스
										new UppercaseHandler(new HelloTarget())); //부가기능과 위임 코드를 담은 invocationHandler
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
}
