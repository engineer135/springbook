package springbook.learningtest.pointcut;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Engineer135
 *
 * aspectJ 포인트컷 표현식에서 기억해야할 점은
 * 클래스 이름 패턴이 아닌 타입 패턴이라는 것이다.
 * 예를 들어 표현식에서 인터페이스를 지정하면,
 * 그 인터페이스를 구현한 클래스의 메소드 중에서, 이 인터페이스를 구현한 메소드에만 포인트컷이 적용된다!
 * 중요하다. 이름이 아닌, 타입 패턴이라는 것!
 */
public class PointcutExpressionTest {
	@Test
	public void methodSignaturePointcut() throws NoSuchMethodException, SecurityException {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(public int "
				+ "springbook.learningtest.pointcut.Target.minus(int, int) "
				+ "throws java.lang.RuntimeException)" 
				); // Target 클래스 minus() 메소드 시그니처'
		
		// Target.minus()
		assertThat(pointcut.getClassFilter().matches(Target.class) // 클래스 필터와 메소드 매처를 가져와 각각 비교한다.
				&& pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null), is(true)); // 포인트컷 조건 통과
		
		// Target.plus()
		assertThat(pointcut.getClassFilter().matches(Target.class) // 클래스 필터와 메소드 매처를 가져와 각각 비교한다.
				&& pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null), is(false)); // 메소드 매처에서 실패
		
		// Bean.method()
		assertThat(pointcut.getClassFilter().matches(Bean.class) // 클래스 필터에서 실패
				&& pointcut.getMethodMatcher().matches(Target.class.getMethod("method"), null), is(false)); // 메소드 매처에서 실패
	}
	
}
