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
		// UserDao�� �ƴ� Ŭ���̾�Ʈ�� ����� ������Ʈ�� �����ڸ� ���� �������ش�.
		// �̷��� �����ν� UserDao�� DB Ŀ�ؼǰ��� �Ϻ��� �и�!
		ConnectionMaker connectionMaker = new DConnectionMaker();
		//ConnectionMaker connectionMaker = new NConnectionMaker();
		
		// TODO Auto-generated method stub
		UserDao dao = new UserDao(connectionMaker);
		*/
		
		/*DaoFactory daoFactory = new DaoFactory();
		UserDao dao = daoFactory.userDao();*/
		
		// ���ø����̼� ���ؽ�Ʈ�� ����
		// �̷��� �ϸ� Ŭ���̾�Ʈ�� ��ü���� ���丮 Ŭ������ �� �ʿ䰡 ����. �ϰ��� ������� ���ϴ� ������Ʈ�� ������ �� �ִ�.
		// �������� �˻�(Dependency Lookup) - �������� ����(Dependency Injection)�� �Ұ����� ��� ���
		// �˻��ϴ� ������Ʈ�� �ڽ��� �������� ���� �ʿ䰡 ����.
		// DI�� ���ԵǴ� ������Ʈ�� �ݵ�� �� ������Ʈ���� ��.
		
		//ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		
		// XML�� �̿��ϴ� ���ø����̼� ���ؽ�Ʈ ����
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class); // ù��° ���ڴ� ���� �̸�, �ι�° ���ڴ� ���� Ÿ��
		
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
