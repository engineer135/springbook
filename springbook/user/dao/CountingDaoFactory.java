package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration //애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시
public class CountingDaoFactory {

	@Bean // 오브젝트 생성을 담당하는 IoC용 메소드라는 표시
	public UserDao userDao(){
		//return new UserDao(connectionMaker());
		
		// 생성자가 아닌 수정자 메소드를 이용해 connetionMaker 주입
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
	
	@Bean // 오브젝트 생성을 담당하는 IoC용 메소드라는 표시
	// 분리해서 중복을 제거한 ConnectionMaker 타입 오브젝트 생성 코드
	public ConnectionMaker connectionMaker(){
		//return new DConnectionMaker();
		
		// DB 연결 횟수 카운팅하는 기능 추가!
		return new CountingConnectionMaker(this.realConnectionMaker());
		
		// 이런식으로 로컬 DB, 운영 DB 나눠서 사용도 가능. 이 부분만 수정하면  DB 접속 정보가 바뀌므로. 얼마나 편한가!
		//return new NConnectionMaker();
	}
	
	public ConnectionMaker realConnectionMaker(){
		return new DConnectionMaker();
	}
	
}
