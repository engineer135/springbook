package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Engineer135
 *
 * UserDaoTest���� ����ߴ� � ConnctionMaker ���� Ŭ������ ������� �����ϴ� ����� �и��ϱ� ���� ���� Class
 * 
 * UserDao, ConnectionMaker �� ���� ������ ����, ��� ���� ���. 
 * DaoFactory�� � ������Ʈ�� � ������Ʈ�� ����ϴ����� �����س��� ���赵��� ���� �ȴ�.
 */

// �������� ������� ������ ���� ����� ���踦 �ο��ϴ� ������Ʈ�� ��(Bean)�̶�� �θ���.
// ���� ������ ���輳�� ���� ��� ����ϴ� IoC ������Ʈ�� �����丮 ��� �θ���. �̸� Ȯ���� ���� ���ø����̼� ���ؽ�Ʈ.
@Configuration //���ø����̼� ���ؽ�Ʈ �Ǵ� �� ���丮�� ����� ����������� ǥ��
public class DaoFactory {
	
	@Bean // ������Ʈ ������ ����ϴ� IoC�� �޼ҵ��� ǥ��
	public UserDao userDao(){
		//return new UserDao(connectionMaker());
		
		// �����ڰ� �ƴ� ������ �޼ҵ带 �̿��� connetionMaker ����
		UserDao userDao = new UserDao();
		userDao.setConnectionMaker(connectionMaker());
		return userDao;
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
		return new DConnectionMaker();
		// �̷������� ���� DB, � DB ������ ��뵵 ����. �� �κи� �����ϸ�  DB ���� ������ �ٲ�Ƿ�. �󸶳� ���Ѱ�!
		//return new NConnectionMaker();
	}
}
