package springbook.learningtest.jdk;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class UppercaseAdvice implements MethodInterceptor {

	public Object invoke(MethodInvocation invocation) throws Throwable {
		String ret = (String) invocation.proceed();
		return ret.toUpperCase();
	}

}
