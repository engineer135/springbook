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
		// UserDao�� �ƴ� Ŭ���̾�Ʈ�� ����� ������Ʈ�� �����ڸ� ���� �������ش�.
		// �̷��� �����ν� UserDao�� DB Ŀ�ؼǰ��� �Ϻ��� �и�!
		ConnectionMaker connectionMaker = new DConnectionMaker();
		//ConnectionMaker connectionMaker = new NConnectionMaker();
		
		// TODO Auto-generated method stub
		UserDao dao = new UserDao(connectionMaker);
		*/
		
		DaoFactory daoFactory = new DaoFactory();
		UserDao dao = daoFactory.userDao();
		
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
