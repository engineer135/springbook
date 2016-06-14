package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 * 트랜잭션 처리를 담당하는 클래스
 * 클라이언트(여기에선 userServiceTest)가 userServiceTx를 호출해서 사용하도록 만들어야 한다. 어플리케이션 컨텍스트도 수정 필요
 * 
 * 이처럼 마치 자신이 클라이언트가 사용하려고 하는 실제 대상인 것처럼 위장해서 클라이언트의 요청을 받아주는 것을
 * 대리자, 대리인과 같은 역할이라고 해서 프록시라고 부른다.
 * 
 * 그리고 프록시를 통해 최족적으로 요청을 위임받아 처리하는 실제 오브젝트를 타깃, 또는 실체(real subject)라고 부른다.
 */
public class UserServiceTx implements UserService {
	
	//UserService를 구현한 다른 오브젝트를 DI 받는다.
	UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	// 트랜잭션 매니저를 빈으로 분리해서 DI 받도록 처리
	private PlatformTransactionManager transactionManager;
	
	// 프로퍼티 이름은 관례를 따라 transactionManager라고 만드는 것이 편리하다.
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	// DI 받은 UserService 오브젝트에 모든 기능을 위임한다.
	public void add(User user) {
		userService.add(user);
	}

	public void upgradeLevels() {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); //DI 받은 트랜잭션 매니저를 공유해서 사용. 멀티스레드 환경에서도 안전.
		
		try{
			userService.upgradeLevels();
			
			this.transactionManager.commit(status);
		}catch(RuntimeException e){
			this.transactionManager.rollback(status);
			throw e;
		}
		
	}

}
