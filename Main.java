import java.sql.SQLException;

import springbook.user.dao.NUserDao;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		UserDao dao = new NUserDao();
		
		User user = new User();
		user.setId("test1");
		user.setName("����");
		user.setPassword("1234");
		
		dao.add(user);
		
		System.out.println(user.getId() + " ��� ����");
		
		User user2= dao.get(user.getId());
		
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		
		System.out.println(user2.getId() + "��ȸ ����");
	}

}
