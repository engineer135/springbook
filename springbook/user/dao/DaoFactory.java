package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Engineer135
 *
 * UserDaoTest에서 담당했던 어떤 ConnctionMaker 구현 클래스를 사용할지 결정하는 기능을 분리하기 위해 만든 Class
 * 
 * UserDao, ConnectionMaker 는 각각 데이터 로직, 기술 로직 담당. 
 * DaoFactory는 어떤 오브젝트가 어떤 오브젝트를 사용하는지를 정의해놓은 설계도라고 보면 된다.
 */

// 스프링이 제어권을 가지고 직접 만들고 관계를 부여하는 오브젝트를 빈(Bean)이라고 부른다.
// 빈의 생성과 관계설정 같은 제어를 담당하는 IoC 오브젝트를 빈팩토리 라고 부른다. 이를 확장한 것이 애플리케이션 컨텍스트.
@Configuration //애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시
public class DaoFactory {
	
	@Bean // 오브젝트 생성을 담당하는 IoC용 메소드라는 표시
	public UserDao userDao(){
		//return new UserDao(connectionMaker());
		
		// 생성자가 아닌 수정자 메소드를 이용해 connetionMaker 주입
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
	
	@Bean // 오브젝트 생성을 담당하는 IoC용 메소드라는 표시
	// 분리해서 중복을 제거한 ConnectionMaker 타입 오브젝트 생성 코드
	public ConnectionMaker connectionMaker(){
		return new DConnectionMaker();
		// 이런식으로 로컬 DB, 운영 DB 나눠서 사용도 가능. 이 부분만 수정하면  DB 접속 정보가 바뀌므로. 얼마나 편한가!
		//return new NConnectionMaker();
	}
}
