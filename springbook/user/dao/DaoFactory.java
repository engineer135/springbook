package springbook.user.dao;

/**
 * @author Engineer135
 *
 * UserDaoTest에서 담당했던 어떤 ConnctionMaker 구현 클래스를 사용할지 결정하는 기능을 분리하기 위해 만든 Class
 * 
 * UserDao, ConnectionMaker 는 각각 데이터 로직, 기술 로직 담당. 
 * DaoFactory는 어떤 오브젝트가 어떤 오브젝트를 사용하는지를 정의해놓은 설계도라고 보면 된다.
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
	
	// 분리해서 중복을 제거한 ConnectionMaker 타입 오브젝트 생성 코드
	public ConnectionMaker connectionMaker(){
		return new DConnectionMaker();
	}
}
