package springbook.learningtest.pointcut;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Engineer135
 *
 * aspectJ ����Ʈ�� ǥ���Ŀ��� ����ؾ��� ����
 * Ŭ���� �̸� ������ �ƴ� Ÿ�� �����̶�� ���̴�.
 * ���� ��� ǥ���Ŀ��� �������̽��� �����ϸ�,
 * �� �������̽��� ������ Ŭ������ �޼ҵ� �߿���, �� �������̽��� ������ �޼ҵ忡�� ����Ʈ���� ����ȴ�!
 * �߿��ϴ�. �̸��� �ƴ�, Ÿ�� �����̶�� ��!
 */
public class PointcutExpressionTest {
	@Test
	public void methodSignaturePointcut() throws NoSuchMethodException, SecurityException {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(public int "
				+ "springbook.learningtest.pointcut.Target.minus(int, int) "
				+ "throws java.lang.RuntimeException)" 
				); // Target Ŭ���� minus() �޼ҵ� �ñ״�ó'
		
		// Target.minus()
		assertThat(pointcut.getClassFilter().matches(Target.class) // Ŭ���� ���Ϳ� �޼ҵ� ��ó�� ������ ���� ���Ѵ�.
				&& pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null), is(true)); // ����Ʈ�� ���� ���
		
		// Target.plus()
		assertThat(pointcut.getClassFilter().matches(Target.class) // Ŭ���� ���Ϳ� �޼ҵ� ��ó�� ������ ���� ���Ѵ�.
				&& pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null), is(false)); // �޼ҵ� ��ó���� ����
		
		// Bean.method()
		assertThat(pointcut.getClassFilter().matches(Bean.class) // Ŭ���� ���Ϳ��� ����
				&& pointcut.getMethodMatcher().matches(Target.class.getMethod("method"), null), is(false)); // �޼ҵ� ��ó���� ����
	}
	
}
