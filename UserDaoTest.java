import java.sql.SQLException;

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
		
		DaoFactory daoFactory = new DaoFactory();
		UserDao dao = daoFactory.userDao();
		
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
