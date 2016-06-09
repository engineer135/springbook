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

//테스트는 가능한한 독립적으로 매번 새로운 오브젝트를 만들어 사용하는 것이 원칙
// 하지만 애플리케이션 컨텍스트처럼 생성에 많은 시간과 자원이 소모되는 경우에는 테스트 전체가 공유하는 오브젝트를 만들기도 한다.
// 문제는. jUnit이 매번 테스트 클래스의 오브젝트를 새로 만든다는 점!
// 그래서 오브젝트 레벨에 애플리케이션 컨텍스트를 저장해두면 곤란하다. (리소스 등등)
// 그래서.. jUnit이 @beforeClass 스태틱 메소드를 지원한다. 하지만, 스프링에서 직접 제공하는 애플리케이션 컨텍스트 테스트 지원 기능을 사용하는 것이 더 편리!
@RunWith(SpringJUnit4ClassRunner.class) // 스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
@ContextConfiguration(locations="/applicationContext.xml") //테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트의 위치 지정
public class UserDaoTest {
	
	@Autowired
	private UserDao dao;
	
	private User user1;
	private User user2;
	private User user3;
	
	// 스프링 테스트 컨텍스트 적용
	// 테스트 오브젝트가 만들어지고 나면 스프링 테스트 컨텍스트에 의해 자동으로 값 주입
	@Autowired
	// Autowired가 붙은 인스턴스 변수가 있으면, 테스트 컨텍스트 프레임워크는 변수 타입과 일치하는 컨텍스트 내의 빈을 찾아서 주입해준다.
	// ApplicationContext 타입의 빈은 xml 파일에 없는데 어떻게 DI가 된건지?
	// 이유는 애플리케이션 컨텍스트가 초기화할때 자기 자신도 빈으로 등록하기 때문!
	// 따라서 UserDao도 Autowired로 DI 가능.
	private ApplicationContext context;
	
	// @Before 어노테이션이 붙으면 테스트 메소드 실행시 먼저 실행한다.
	// 반복 작업을 여기 넣어두면 좋다.
	// 테스트를 수행하는 데 필요한 정보나 오브젝트를 픽스처(fixture)라고 하는데, 여기선 dao가 대표적인 픽스처이다. 
	@Before
	public void setUp() {
		// 컨텍스트 공유가 어떻게 되는것인지 확인하기 위한 소스
		// 컨텍스트는 모두 동일해야한다. 그게 스프링 테스트 컨텍스트를 적용한 이유!
		System.out.println(this.context);
		System.out.println(this);
		
		//ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml"); 스프링 테스트 컨텍스트 프레임워크 적용을 위해 주석 처리
		//this.dao = context.getBean("userDao", UserDao.class); // 첫번째 인자는 빈의 이름, 두번째 인자는 리턴 타입

		user1 = new User("gymee","자몽1","1234", Level.BASIC, 1, 0); //추가된 필드를 위한 초기값 세팅
		user2 = new User("leegw700","자몽2","1234" , Level.SILVER, 55, 10); //추가된 필드를 위한 초기값 세팅
		user3 = new User("bumjin","자몽3","1234", Level.GOLD, 100, 40); //추가된 필드를 위한 초기값 세팅
		
		System.out.println(user1);
		System.out.println(user2);
		System.out.println(user3);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		/**
		// UserDao가 아닌 클라이언트가 사용할 오브젝트를 생성자를 통해 전달해준다.
		// 이렇게 함으로써 UserDao와 DB 커넥션과의 완벽한 분리!
		//ConnectionMaker connectionMaker = new DConnectionMaker();
		//ConnectionMaker connectionMaker = new NConnectionMaker();
		
		// TODO Auto-generated method stub
		//UserDao dao = new UserDao(connectionMaker);
		
		
		//DaoFactory daoFactory = new DaoFactory();
		//UserDao dao = daoFactory.userDao();
		
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
		
		if(!user.getName().equals(user2.getName())){
			System.out.println("테스트 실패 (name)");
		}else if(!user.getPassword().equals(user2.getPassword())){
			System.out.println("테스트 실패 (password)");
		}else{
			System.out.println("조회 테스트 성공");
		}
		*/
		
		
		
		//jUnit 테스트로 변경해준다
		//JUnitCore.main("UserDaoTest");
		
		//IDE의 jUnit 테스트 기능을 이용하면 main 메소드 호출도 필요 없다.
		//@Test 있는 클래스 선택한 뒤에 Run As -> JUnit Test 선택하면 자동 실행
	}
	
	
	
	
	// jUnit 프레임워크를 사용한 테스트
	@Test
	public void addAndGet() throws SQLException {
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
		// setUp()으로 이동
		/*ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		dao = context.getBean("userDao", UserDao.class); // 첫번째 인자는 빈의 이름, 두번째 인자는 리턴 타입
*/		
		// deleteAll 추가
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		//setUp()으로 이동
		// 새로 만든 생성자로 user 간편 생성
		/*User user1 = new User("test1","자몽","1234");
		User user2 = new User("test2","자몽2","1234");*/
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		/*if(!user.getName().equals(user2.getName())){
			System.out.println("테스트 실패 (name)");
		}else if(!user.getPassword().equals(user2.getPassword())){
			System.out.println("테스트 실패 (password)");
		}else{
			System.out.println("조회 테스트 성공");
		}*/
		
		User userget1 = dao.get(user1.getId());
		this.checkSameUser(userget1, user1);
		
		User userget2 = dao.get(user2.getId());
		this.checkSameUser(userget2, user2);
	}
	
	@Test
	public void count() throws SQLException {
		// setUp()으로 이동
		// XML을 이용하는 애플리케이션 컨텍스트 적용
		/*ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class); // 첫번째 인자는 빈의 이름, 두번째 인자는 리턴 타입
		
		User user1 = new User("test2","자몽","1234");
		User user2 = new User("test3","자몽3","1234");
		User user3 = new User("test4","자몽4","1234");
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
	
	//테스트 중에 발생할 것으로 기대하는 예외 클래스를 지정해준다.
	//예외가 반드시 발생해야 하는 경우를 테스트할때 사용. 예외 발생 = 테스트 성공. 그 외는 실패.
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
		// setUp()으로 이동
		// XML을 이용하는 애플리케이션 컨텍스트 적용
		/*ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class); // 첫번째 인자는 빈의 이름, 두번째 인자는 리턴 타입
*/		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");//이 메소드 실행 중에 예외가 발생해야 한다. 예외가 발생하지 않으면 테스트가 실패한다. 해당 id의 정보가 없으니까.. rs.next 에서 로우가 없으므로 에러!
	}
	
	@Test
	public void getAll() throws SQLException{
		dao.deleteAll();
		
		// 데이터가 없을때 어떻게 처리할지!?
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));//데이터가 없을때 크기가 0인 리스트 오브젝트가 리턴되어야 한다.
		
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
		checkSameUser(user3, users3.get(0));//user3의 id값이 알파벳순으로 가장 빠르므로 getAll()의 첫번째 엘리먼트여야 한다.
		checkSameUser(user1, users3.get(1));
		checkSameUser(user2, users3.get(2));
	}
	
	@Test
	public void update(){
		dao.deleteAll();
		
		dao.add(user1);	//수정할 사용자
		dao.add(user2);	//수정하지 않을 사용자
		
		user1.setName("오민규");
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
