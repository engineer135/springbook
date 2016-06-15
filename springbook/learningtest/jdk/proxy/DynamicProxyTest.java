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
		// JDK 다이내믹 프록시 생성
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(
					getClass().getClassLoader(),
					new Class[] {Hello.class},
					new UppercaseHandler(new HelloTarget())
				);
	}
	
	@Test
	public void proxyFactoryBean(){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());//타깃 설정
		pfBean.addAdvice(new UppercaseAdvice());//부가기능을 담은 어드바이스를 추가한다. 여러개를 추가할 수도 있다.
		
		Hello proxiedHello = (Hello) pfBean.getObject(); //FactoryBean이므로 getObject()로 생성된 프록시를 가져온다.
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
	
	static class UppercaseAdvice implements MethodInterceptor {
		//MethodInvocation은 일종의 콜백 오브젝트. 싱글톤으로 두고 공유가 가능하다는 장점!
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String ret = (String) invocation.proceed();//리플렉션의 Method와 달리 메소드 실행 시 타깃 오브젝트를 전달할 필요가 없다. MethodInvocation은 메소드 정보와 함께 타깃 오브젝트를 알고있기 때문
			return ret.toUpperCase();//부가기능 적용
		}
	}
	
	// 포인트컷까지 적용한 메소드
	// 스프링은 부가기능을 제공하는 오브젝트를 어드바이스라고 부르고
	// 메소드 선정 알고리즘을 담은 오브젝트를 포인트컷이라고 부른다!
	@Test
	public void pointcutAdvisor(){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());//타깃 설정
		
		// 메소드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트컷 생성
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*"); //sayH로 시작하는 모든 메소드를 선택하게 한다
		
		// 포인트컷과 어드바이스를 Advisor로 묶어서 한 번에 추가
		// 여러개의 어드바이스가 등록되더라도 각각 다른 포인트컷과 조합될 수 있기 때문에 각기 다른 메소드 선정방식 적용 가능!
		// 어드바이저 = 포인트컷(메소드 선정 알고리즘) + 어드바이스(부가기능)
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice())); 
		//pfBean.addAdvice(new UppercaseAdvice());//부가기능을 담은 어드바이스를 추가한다. 여러개를 추가할 수도 있다.
		
		Hello proxiedHello = (Hello) pfBean.getObject(); //FactoryBean이므로 getObject()로 생성된 프록시를 가져온다.
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby")); //메소드 이름이 포인트컷의 선정조건에 맞지 않으므로, 부가기능(대문자변환)이 적용되지 않음
	}
	
	
	static interface Hello { //타깃과 프록시가 구현할 인터페이스
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
