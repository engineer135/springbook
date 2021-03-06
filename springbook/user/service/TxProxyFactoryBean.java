package springbook.user.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Engineer135
 *
 * 프록시 팩토리 빈의 한계
 * 타깃에 부가기능을 제공하는 것은 메소드 단위로 일어남.
 * 한번에 여러 개의 클래스에 공통적인 부가기능 제공하는 것이 불가능(거의 비슷한 프록시 팩토리 빈의 설정이 중복되는 것을 막을 수 없음)
 * 하나의 타깃에 여러개의 부가기능을 적용하려고 할 때도 문제. (트랜잭션 + 보안 기능 + 기능 검사 등등)
 * xml 설정에 부가기능의 개수만큼 추가되어야 함.
 * 또 한가지 문제점은 TransactionHandler 오브젝트가 프록시 팩토리 빈 개수만큼 만들어진다는 점!
 * 
 */
public class TxProxyFactoryBean implements FactoryBean<Object>{ //생성할 오브젝트 타입을 지정할 수도 있지만 범용적으로 사용하기 위해 Object로 했다
	
	//TransactionHandler를 생성할 때 필요
	Object target;
	PlatformTransactionManager transactionManager;
	String pattern;
	
	//다이내믹 프록시를 생성할때 필요. UserService 외의 인터페이스를 가진 타깃에도 적용할 수 있다.
	Class<?> serviceInterface;

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	// FactoryBean 인터페이스 구현 메소드
	// 이렇게 하면 다이내믹 프록시도 빈 생성이 가능하게 되는 거구나! 
	// 그래서 팩토리빈 테스트를 했구나 이걸 위해서!!
	// UppercaseHandler를 먼저 작업했고, 그 후 FactoryBeanTest를 한 후 여기에 적용을 한 것임!
	public Object getObject() throws Exception {//DI 받은 정보를 이용해서 TransactionHandler를 사용하는 다이내믹 프록시를 생성한다.
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(target);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern(pattern);
		return Proxy.newProxyInstance(
				getClass().getClassLoader(), //동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
				new Class[] {serviceInterface}, //구현할 인터페이스
				txHandler); //부가기능과 위임 코드를 담은 invocationHandler
	}

	public Class<?> getObjectType() {
		return serviceInterface; // 팩토리 빈이 생성하는 오브젝트의 타입은 DI 받은 인터페이스 타입에 따라 달라진다. 따라서 다양한 타입의 프록시 오브젝트 생성에 재사용 가능.
	}

	public boolean isSingleton() {
		return false; // 싱글톤 빈이 아니라는 뜻이 아니라 getObject()가 매번 같은 오브젝트를 리턴하지 않는다는 의미이다.
	}
}
