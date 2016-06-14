package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 * Ʈ����� ó���� ����ϴ� Ŭ����
 * Ŭ���̾�Ʈ(���⿡�� userServiceTest)�� userServiceTx�� ȣ���ؼ� ����ϵ��� ������ �Ѵ�. ���ø����̼� ���ؽ�Ʈ�� ���� �ʿ�
 * 
 * ��ó�� ��ġ �ڽ��� Ŭ���̾�Ʈ�� ����Ϸ��� �ϴ� ���� ����� ��ó�� �����ؼ� Ŭ���̾�Ʈ�� ��û�� �޾��ִ� ����
 * �븮��, �븮�ΰ� ���� �����̶�� �ؼ� ���Ͻö�� �θ���.
 * 
 * �׸��� ���Ͻø� ���� ���������� ��û�� ���ӹ޾� ó���ϴ� ���� ������Ʈ�� Ÿ��, �Ǵ� ��ü(real subject)��� �θ���.
 */
public class UserServiceTx implements UserService {
	
	//UserService�� ������ �ٸ� ������Ʈ�� DI �޴´�.
	UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	// Ʈ����� �Ŵ����� ������ �и��ؼ� DI �޵��� ó��
	private PlatformTransactionManager transactionManager;
	
	// ������Ƽ �̸��� ���ʸ� ���� transactionManager��� ����� ���� ���ϴ�.
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	// DI ���� UserService ������Ʈ�� ��� ����� �����Ѵ�.
	public void add(User user) {
		userService.add(user);
	}

	public void upgradeLevels() {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); //DI ���� Ʈ����� �Ŵ����� �����ؼ� ���. ��Ƽ������ ȯ�濡���� ����.
		
		try{
			userService.upgradeLevels();
			
			this.transactionManager.commit(status);
		}catch(RuntimeException e){
			this.transactionManager.rollback(status);
			throw e;
		}
		
	}

}
