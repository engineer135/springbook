package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration //���ø����̼� ���ؽ�Ʈ �Ǵ� �� ���丮�� ����� ����������� ǥ��
public class CountingDaoFactory {

	@Bean // ������Ʈ ������ ����ϴ� IoC�� �޼ҵ��� ǥ��
	public UserDao userDao(){
		//return new UserDao(connectionMaker());
		
		// �����ڰ� �ƴ� ������ �޼ҵ带 �̿��� connetionMaker ����
		//UserDao userDao = new UserDao();
		//userDao.setConnectionMaker(connectionMaker());
		//return userDao;
		
		return null;
	}
	
	/*public AccountDao accountDao(){
		return new AccountDao(connectionMaker());
	}
	
	public MessageDao messageDao(){
		return new MessageDao(connectionMaker());
	}*/
	
	@Bean // ������Ʈ ������ ����ϴ� IoC�� �޼ҵ��� ǥ��
	// �и��ؼ� �ߺ��� ������ ConnectionMaker Ÿ�� ������Ʈ ���� �ڵ�
	public ConnectionMaker connectionMaker(){
		//return new DConnectionMaker();
		
		// DB ���� Ƚ�� ī�����ϴ� ��� �߰�!
		return new CountingConnectionMaker(this.realConnectionMaker());
		
		// �̷������� ���� DB, � DB ������ ��뵵 ����. �� �κи� �����ϸ�  DB ���� ������ �ٲ�Ƿ�. �󸶳� ���Ѱ�!
		//return new NConnectionMaker();
	}
	
	public ConnectionMaker realConnectionMaker(){
		return new DConnectionMaker();
	}
	
}
