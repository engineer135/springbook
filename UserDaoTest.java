import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import springbook.user.dao.ConnectionMaker;
import springbook.user.dao.DConnectionMaker;
import springbook.user.dao.DaoFactory;
import springbook.user.dao.NConnectionMaker;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class UserDaoTest {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		/*
		// UserDao가 아닌 클라이언트가 사용할 오브젝트를 생성자를 통해 전달해준다.
		// 이렇게 함으로써 UserDao와 DB 커넥션과의 완벽한 분리!
		ConnectionMaker connectionMaker = new DConnectionMaker();
		//ConnectionMaker connectionMaker = new NConnectionMaker();
		
		// TODO Auto-generated method stub
		UserDao dao = new UserDao(connectionMaker);
		*/
		
		/*DaoFactory daoFactory = new DaoFactory();
		UserDao dao = daoFactory.userDao();*/
		
		// 애플리케이션 컨텍스트를 적용
		// 이렇게 하면 클라이언트는 구체적인 팩토리 클래스를 알 필요가 없다. 일관된 방식으로 원하는 오브젝트를 가져올 수 있다.
		// 의존관계 검색(Dependency Lookup) - 의존관계 주입(Dependency Injection)이 불가능한 경우 사용
		// 검색하는 오브젝트는 자신이 스프링의 빈일 필요가 없음.
		// DI는 주입되는 오브젝트도 반드시 빈 오브젝트여야 함.
		
		//ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		
		// XML을 이용하는 애플리케이션 컨텍스트 적용
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class); // 첫번째 인자는 빈의 이름, 두번째 인자는 리턴 타입
		
		User user = new User();
		user.setId("test1");
		user.setName("레몬");
		user.setPassword("1234");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		
		User user2= dao.get(user.getId());
		
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		
		System.out.println(user2.getId() + "조회 성공");
	}
}
