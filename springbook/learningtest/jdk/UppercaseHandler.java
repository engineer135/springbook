package springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Engineer135
 * 
 * InvocationHandler로 다이내믹 프록시 적용
 *
 */
public class UppercaseHandler implements InvocationHandler {
	Hello target;
	
	// 다이내믹 프록시로부터 전달받은 요청을 다시 타깃 오브젝트에 위임해야 하기 때문에 타깃 오브젝트를 주입받아 둔다.
	public UppercaseHandler(Hello target){
		this.target = target;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String ret = (String) method.invoke(target, args);//타깃으로 위임. 인터페이스의 메소드 호출에 모두 적용된다.
		return ret.toUpperCase();//부가기능 제공
	}
}
