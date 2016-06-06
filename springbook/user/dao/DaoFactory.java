package springbook.user.dao;

/**
 * @author Engineer135
 *
 * UserDaoTest���� ����ߴ� � ConnctionMaker ���� Ŭ������ ������� �����ϴ� ����� �и��ϱ� ���� ���� Class
 * 
 * UserDao, ConnectionMaker �� ���� ������ ����, ��� ���� ���. 
 * DaoFactory�� � ������Ʈ�� � ������Ʈ�� ����ϴ����� �����س��� ���赵��� ���� �ȴ�.
 */
public class DaoFactory {
	public UserDao userDao(){
		return new UserDao(connectionMaker());
	}
	
	/*public AccountDao accountDao(){
		return new AccountDao(connectionMaker());
	}
	
	public MessageDao messageDao(){
		return new MessageDao(connectionMaker());
	}*/
	
	// �и��ؼ� �ߺ��� ������ ConnectionMaker Ÿ�� ������Ʈ ���� �ڵ�
	public ConnectionMaker connectionMaker(){
		return new DConnectionMaker();
	}
}
