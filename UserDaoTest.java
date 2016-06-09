import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

//�׽�Ʈ�� �������� ���������� �Ź� ���ο� ������Ʈ�� ����� ����ϴ� ���� ��Ģ
// ������ ���ø����̼� ���ؽ�Ʈó�� ������ ���� �ð��� �ڿ��� �Ҹ�Ǵ� ��쿡�� �׽�Ʈ ��ü�� �����ϴ� ������Ʈ�� ����⵵ �Ѵ�.
// ������. jUnit�� �Ź� �׽�Ʈ Ŭ������ ������Ʈ�� ���� ����ٴ� ��!
// �׷��� ������Ʈ ������ ���ø����̼� ���ؽ�Ʈ�� �����صθ� ����ϴ�. (���ҽ� ���)
// �׷���.. jUnit�� @beforeClass ����ƽ �޼ҵ带 �����Ѵ�. ������, ���������� ���� �����ϴ� ���ø����̼� ���ؽ�Ʈ �׽�Ʈ ���� ����� ����ϴ� ���� �� ��!
@RunWith(SpringJUnit4ClassRunner.class) // �������� �׽�Ʈ ���ؽ�Ʈ �����ӿ�ũ�� JUnit Ȯ���� ����
@ContextConfiguration(locations="/applicationContext.xml") //�׽�Ʈ ���ؽ�Ʈ�� �ڵ����� ������� ���ø����̼� ���ؽ�Ʈ�� ��ġ ����
public class UserDaoTest {
	
	@Autowired
	private UserDao dao;
	
	private User user1;
	private User user2;
	private User user3;
	
	// ������ �׽�Ʈ ���ؽ�Ʈ ����
	// �׽�Ʈ ������Ʈ�� ��������� ���� ������ �׽�Ʈ ���ؽ�Ʈ�� ���� �ڵ����� �� ����
	@Autowired
	// Autowired�� ���� �ν��Ͻ� ������ ������, �׽�Ʈ ���ؽ�Ʈ �����ӿ�ũ�� ���� Ÿ�԰� ��ġ�ϴ� ���ؽ�Ʈ ���� ���� ã�Ƽ� �������ش�.
	// ApplicationContext Ÿ���� ���� xml ���Ͽ� ���µ� ��� DI�� �Ȱ���?
	// ������ ���ø����̼� ���ؽ�Ʈ�� �ʱ�ȭ�Ҷ� �ڱ� �ڽŵ� ������ ����ϱ� ����!
	// ���� UserDao�� Autowired�� DI ����.
	private ApplicationContext context;
	
	// @Before ������̼��� ������ �׽�Ʈ �޼ҵ� ����� ���� �����Ѵ�.
	// �ݺ� �۾��� ���� �־�θ� ����.
	// �׽�Ʈ�� �����ϴ� �� �ʿ��� ������ ������Ʈ�� �Ƚ�ó(fixture)��� �ϴµ�, ���⼱ dao�� ��ǥ���� �Ƚ�ó�̴�. 
	@Before
	public void setUp() {
		// ���ؽ�Ʈ ������ ��� �Ǵ°����� Ȯ���ϱ� ���� �ҽ�
		// ���ؽ�Ʈ�� ��� �����ؾ��Ѵ�. �װ� ������ �׽�Ʈ ���ؽ�Ʈ�� ������ ����!
		System.out.println(this.context);
		System.out.println(this);
		
		//ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml"); ������ �׽�Ʈ ���ؽ�Ʈ �����ӿ�ũ ������ ���� �ּ� ó��
		//this.dao = context.getBean("userDao", UserDao.class); // ù��° ���ڴ� ���� �̸�, �ι�° ���ڴ� ���� Ÿ��

		user1 = new User("gymee","�ڸ�1","1234", Level.BASIC, 1, 0); //�߰��� �ʵ带 ���� �ʱⰪ ����
		user2 = new User("leegw700","�ڸ�2","1234" , Level.SILVER, 55, 10); //�߰��� �ʵ带 ���� �ʱⰪ ����
		user3 = new User("bumjin","�ڸ�3","1234", Level.GOLD, 100, 40); //�߰��� �ʵ带 ���� �ʱⰪ ����
		
		System.out.println(user1);
		System.out.println(user2);
		System.out.println(user3);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		/**
		// UserDao�� �ƴ� Ŭ���̾�Ʈ�� ����� ������Ʈ�� �����ڸ� ���� �������ش�.
		// �̷��� �����ν� UserDao�� DB Ŀ�ؼǰ��� �Ϻ��� �и�!
		//ConnectionMaker connectionMaker = new DConnectionMaker();
		//ConnectionMaker connectionMaker = new NConnectionMaker();
		
		// TODO Auto-generated method stub
		//UserDao dao = new UserDao(connectionMaker);
		
		
		//DaoFactory daoFactory = new DaoFactory();
		//UserDao dao = daoFactory.userDao();
		
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
		
		if(!user.getName().equals(user2.getName())){
			System.out.println("�׽�Ʈ ���� (name)");
		}else if(!user.getPassword().equals(user2.getPassword())){
			System.out.println("�׽�Ʈ ���� (password)");
		}else{
			System.out.println("��ȸ �׽�Ʈ ����");
		}
		*/
		
		
		
		//jUnit �׽�Ʈ�� �������ش�
		//JUnitCore.main("UserDaoTest");
		
		//IDE�� jUnit �׽�Ʈ ����� �̿��ϸ� main �޼ҵ� ȣ�⵵ �ʿ� ����.
		//@Test �ִ� Ŭ���� ������ �ڿ� Run As -> JUnit Test �����ϸ� �ڵ� ����
	}
	
	
	
	
	// jUnit �����ӿ�ũ�� ����� �׽�Ʈ
	@Test
	public void addAndGet() throws SQLException {
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
		// setUp()���� �̵�
		/*ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		dao = context.getBean("userDao", UserDao.class); // ù��° ���ڴ� ���� �̸�, �ι�° ���ڴ� ���� Ÿ��
*/		
		// deleteAll �߰�
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		//setUp()���� �̵�
		// ���� ���� �����ڷ� user ���� ����
		/*User user1 = new User("test1","�ڸ�","1234");
		User user2 = new User("test2","�ڸ�2","1234");*/
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		/*if(!user.getName().equals(user2.getName())){
			System.out.println("�׽�Ʈ ���� (name)");
		}else if(!user.getPassword().equals(user2.getPassword())){
			System.out.println("�׽�Ʈ ���� (password)");
		}else{
			System.out.println("��ȸ �׽�Ʈ ����");
		}*/
		
		User userget1 = dao.get(user1.getId());
		this.checkSameUser(userget1, user1);
		
		User userget2 = dao.get(user2.getId());
		this.checkSameUser(userget2, user2);
	}
	
	@Test
	public void count() throws SQLException {
		// setUp()���� �̵�
		// XML�� �̿��ϴ� ���ø����̼� ���ؽ�Ʈ ����
		/*ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class); // ù��° ���ڴ� ���� �̸�, �ι�° ���ڴ� ���� Ÿ��
		
		User user1 = new User("test2","�ڸ�","1234");
		User user2 = new User("test3","�ڸ�3","1234");
		User user3 = new User("test4","�ڸ�4","1234");
*/
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}
	
	//�׽�Ʈ �߿� �߻��� ������ ����ϴ� ���� Ŭ������ �������ش�.
	//���ܰ� �ݵ�� �߻��ؾ� �ϴ� ��츦 �׽�Ʈ�Ҷ� ���. ���� �߻� = �׽�Ʈ ����. �� �ܴ� ����.
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
		// setUp()���� �̵�
		// XML�� �̿��ϴ� ���ø����̼� ���ؽ�Ʈ ����
		/*ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class); // ù��° ���ڴ� ���� �̸�, �ι�° ���ڴ� ���� Ÿ��
*/		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");//�� �޼ҵ� ���� �߿� ���ܰ� �߻��ؾ� �Ѵ�. ���ܰ� �߻����� ������ �׽�Ʈ�� �����Ѵ�. �ش� id�� ������ �����ϱ�.. rs.next ���� �ο찡 �����Ƿ� ����!
	}
	
	@Test
	public void getAll() throws SQLException{
		dao.deleteAll();
		
		// �����Ͱ� ������ ��� ó������!?
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));//�����Ͱ� ������ ũ�Ⱑ 0�� ����Ʈ ������Ʈ�� ���ϵǾ�� �Ѵ�.
		
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user3, users3.get(0));//user3�� id���� ���ĺ������� ���� �����Ƿ� getAll()�� ù��° ������Ʈ���� �Ѵ�.
		checkSameUser(user1, users3.get(1));
		checkSameUser(user2, users3.get(2));
	}
	
	@Test
	public void update(){
		dao.deleteAll();
		
		dao.add(user1);	//������ �����
		dao.add(user2);	//�������� ���� �����
		
		user1.setName("���α�");
		user1.setPassword("12345");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1,user1update);
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
	}
	
	private void checkSameUser(User user1, User user2){
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
	}
}
