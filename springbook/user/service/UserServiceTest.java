package springbook.user.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

// 로그인수, 추천수는 상수로 만들어서 쓴다.
//import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
//import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.AppContext;
import springbook.TestAppContext;
import springbook.learningtest.jdk.Hello;
import springbook.learningtest.jdk.HelloTarget;
import springbook.learningtest.jdk.UppercaseAdvice;
import springbook.user.dao.MockUserDao;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations="/applicationContext.xml")
//@ContextConfiguration(classes=TestApplicationContext.class) //테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트의 위치 지정

@ContextConfiguration(classes={TestAppContext.class, AppContext.class})

// 프로파일 적용 필터라고 보면 된다.
@ActiveProfiles("test")
public class UserServiceTest {
	
	public static final Logger logger = LogManager.getLogger();
	
	// 자동 프록시 생성기를 사용하기 때문에
	// @Autowired를 통해 컨텍스트에서 가져오는 UserService 타입 오브젝트는 UserServiceImpl 오브젝트가 아니라 트랜잭션이 적용된 프록시여야 한다!
	@Autowired
	UserService userService;
	
	// 같은 타입의 빈이 두개 존재하기 때문에 필드 이름을 기준으로 주입될 빈이 결정된다.
	// 자동 프록시 생성기에 의해 트랜잭션 부가기능이 testUserService 빈에 적용됐는지를 확인하는 것이 목적이다.
	@Autowired
	UserService testUserService;
	
	//@Autowired
	//UserServiceImpl userServiceImpl;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	DataSource dataSource; //트랜잭션 적용을 위해 DI
	
	//@Autowired
	//PlatformTransactionManager transactionManager;
	
	@Autowired
	MailSender mailSender;
	
	// User 오브젝트는 스프링이 IoC로 관리해주는 오브젝트가 아니기 때문에, 생성자 호출해서 테스트할 User 오브젝트를 만들면 된다.
	User user;
	
	List<User> users;
	@Before
	public void setUp(){
		users = Arrays.asList(//배열을 리스트로 만들어주는 메소드.
				new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "everafterk@naver.com"),
				new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "everafterk2@naver.com"),
				new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "everafterk3@naver.com"),
				new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_LOGCOUNT_FOR_SILVER, "everafterk4@naver.com"),
				new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "everafterk5@naver.com")
		);
		
		user = new User();
	}
	
	// 팩토리 빈을 가져오려면 애플리케이션 컨텍스트가 필요하다
	@Autowired
	ApplicationContext context;
	
	//@Test
	@DirtiesContext // 메일 발송 대상 확인하는 테스트. 컨텍스트의 DI 설정을 변경하는 테스트라는 것을 알려준다.
	public void upgradeLevels() throws Exception{
		//MockUserDao를 사용해 고립된 테스트를 만든다.
		// 고립된 테스ㅡ에서는 테스트 대상 오브젝트를 직접 생성하면 된다.
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		// 목 오브젝트로 만든 UserDao를 직접 DI해준다.
		MockUserDao mockUserDao = new MockUserDao(this.users);
		//userServiceImpl.setUserDao(mockUserDao);
		
		// 메일 발송 결과를 테스트할 수 있도록 목 오브젝트를 만들어 userService의 의존 오브젝트로 주입한다.
		MockMailSender mockMailSender = new MockMailSender();
		//userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated(); //MockUserDao로부터 업데이트 결과를 가져온다.
		
		//업데이트 횟수와 정보를 확인
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);
		
		// 목 오브젝트에 저장된 메일 수신자 목록을 가져와 업그레이드 대상과 일치하는지 확인한다!
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
	}
	
	// id와 level을 확인하는 간단한 헬퍼 메소드
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel){
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}
	
	private void checkLevel(User user, Level expectedLevel){
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
	}
	
	private void checkLevelUpgraded(User user, boolean upgraded){//어떤 레벨로 바뀔 것인가가 아니라, 다음 레벨로 업그레이드될 것인가 아닌가를 지정한다.
		User userUpdate = userDao.get(user.getId());
		if(upgraded){
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel())); //업그레이드 했는지 확인
		}else{
			assertThat(userUpdate.getLevel(), is(user.getLevel()));// 업그레이드 안 했는지 확인
		}
	}
	
	@Test
	public void add(){
		userDao.deleteAll();
		
		User userWithLevel = users.get(4); //Gold 레벨. Gold 레벨이 이미 지정된 User라면 가입시(add시) 레벨 초기화 안함
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null); //레벨이 비어있는 사용자. 로직에 따라 등록중에 Basic 레벨도 설정되어야 한다.
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		// DB에 저장된 결과를 가져와 확인한다.
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(userWithoutLevel.getLevel()));
	}
	
	// User에 추가한 upgradeLevel() 메소드에 대한 테스트
	@Test
	public void upgradeLevel(){
		Level[] levels = Level.values();
		for(Level level : levels){
			if(level.nextLevel() == null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(level.nextLevel()));
		}
	}
	
	@Test(expected=IllegalStateException.class)
	public void cannotUpgradeLevel(){
		Level[] levels = Level.values();
		for(Level level : levels){
			if(level.nextLevel() != null) continue;
			user.setLevel(level);
			user.upgradeLevel();
		}
	}
	
	//예외 발생 시 작업 취소 여부 테스트
	//@Test
	//@DirtiesContext // 다이내믹 프록시 팩토리 빈을 직접 만들어 사용할때는 없앴다가 다시 등장한 컨텍스트 무효화 애노테이션
	public void upgradeAllOrNothing() throws Exception{
		//TestUserService testUserService = new TestUserService(users.get(3).getId());//예외를 발생시킬 네번째 사용자의 id
		//testUserService.setUserDao(this.userDao);//userDao 수동 DI
		
		//트랜잭션을 위해 추가
		//testUserService.setDataSource(this.dataSource);
		
		//testUserService.setTransactionManager(transactionManager); //수동 DI
		
		//testUserService.setMailSender(mailSender);
		
		//트랜잭션 기능을 분리한 UserServiceTx는 예외 발생용으로 수정할 필요가 없으니 그대로 사용한다.
		//UserServiceTx txUserService = new UserServiceTx();
		//txUserService.setTransactionManager(transactionManager);
		//txUserService.setUserService(testUserService);
		
		//다이내믹 프록시 적용
		/*TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(testUserService);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern("upgradeLevels");
		UserService txUserService = (UserService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {UserService.class}, txHandler);*/
		
		// 팩토리빈을 통한 다이내믹 프록시 DI
		// 팩토리빈 자체를 가져와야 하므로 빈 이름에 &를 반드시 넣어야 한다.
		
		//TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class); //테스트용 타깃 주입
		
		// 스프링 프록시 팩토리 빈으로 변경
		//ProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", ProxyFactoryBean.class); //테스트용 타깃 주입
		//txProxyFactoryBean.setTarget(testUserService);
		//UserService txUserService = (UserService) txProxyFactoryBean.getObject();//변경된 타깃 설정을 이용해서 트랜잭션 다이내믹 프록시 오브젝트를 다시 생성한다!
		
		// TxProxyFactoryBean은 계속 재사용할 수 있다. 트랜잭션 부가기능이 필요한 빈이 추가될 때마다 애플리케이션 컨텍스트에 빈 설정만 추가해주면 된다.
		// 매번 트랜잭션 기능을 담은 UserServiceTx와 같은 프록시 클래스를 작성하는 번거로움을 완벽하게 제거할 수 있게 된 것!!!
		// 자바의 다이내믹 프록시 + 스프링의 팩토리 빈을 함께 적용해서 얻을 수 있는 멋진 결과다!
		
		userDao.deleteAll();
		for(User user : users){
			userDao.add(user);
		}
		
		try{
			this.testUserService.upgradeLevels();// 트랜잭션 기능을 분리한 오브젝트를 통해 예외 발생용 TestUserService가 호출되게 해야 한다.
			fail("TestUserServiceException expected"); //TestUserService는 업그레이드 작업중에 예외가 발생해야 한다. 정상 종료라면 문제가 있으니 실패!
		}catch(TestUserServiceException e){
			//TestUserService가 던져주는 예외를 잡아서 계속 진행되도록 한다. 그 외의 예외라면 테스트 실패!
		}
		
		checkLevelUpgraded(users.get(1), false); //예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었나 확인!
	}
	
	// 확장 포인트컷 테스트
	@Test
	public void classNamePointcutAdvisor(){
		// 포인트컷 준비
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut(){
			public ClassFilter getClassFilter(){//익명 내부 클래스 방식으로 클래스를 정의한다
				return new ClassFilter(){
					public boolean matches(Class<?> clazz){
						return clazz.getSimpleName().startsWith("HelloT"); //클래스 이름이 HelloT로 시작하는 것만 선정한다.
					}
				};
			}
		};
		// sayH로 시작하는 메소드 이름을 가진 메소드만 선정한다.
		classMethodPointcut.setMappedName("sayH*");
		
		// 테스트
		checkAdviced(new HelloTarget(), classMethodPointcut, true); //적용 클래스
		
		class HelloWorld extends HelloTarget{};
		checkAdviced(new HelloWorld(), classMethodPointcut, false); //미적용 클래스
		
		class HelloToby extends HelloTarget{};
		checkAdviced(new HelloToby(), classMethodPointcut, true); //적용 클래스
	}
	
	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		if(adviced){//메소드 선정 방식을 통해 어드바이스 적용
			assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
			assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
		}else{//어드바이스 적용 대상 후보에서 아예 탈락
			assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
			assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
		}
	}
	
	@Test
	public void readOnlyTransactionAttribute(){
		
		logger.info("log4j 테스트!");
		
		testUserService.getAll();
	}
	
	// UserService의 트랜젝션 테스트를 위한 대역 클래스. 
	// 스태틱 클래스로 만든다. 왜 스태틱으로 만들까...!?!?? 참조 -> http://secretroute.tistory.com/entry/%EC%9E%90%EB%B0%94%EC%9D%98%E7%A5%9E-Vol1-Nested-Class
	// http://www.devblog.kr/r/8y0gFPAvJ2j8MWIVVXucyP9uYvQegfSVbY5XNDkHt
	// 자동 프록시 생성기를 테스트하기 위해 이름 변경 TestUserService -> TestUserServiceImpl (어드바이스를 적용해주는 대상 클래스의 이름 패턴을 맞춰줘야함)
	public static class TestUserService extends UserServiceImpl{
		//private String id = "madnite1"; // 테스트 픽스처의 users(3)의 id값을 고정시켜버렸다.
		
		/*protected void upgradeLevel(User user){// 오버라이드
			if(user.getId().equals(this.id)){
				throw new TestUserServiceException();// 지정된 id의 User 오브젝트가 발견되면 예외를 던져서 작업을 강제로 중단시킨다.
			}else{
				super.upgradeLevel(user);
			}
		}*/
		
		// 트랜잭션 부가기능중 읽기전용 테스트를 위해 메소드 오버라이드
		public List<User> getAll(){
			for(User user : super.getAll()){
				System.out.println(user.getId());
				user.setName(user.getName()+"골");
				super.update(user); //강제로 쓰기 시도를 한다. 여기서 읽기전용 속성으로 인한 예외가 발생해야 한다. 근데 예외 발생이 안됨......
			}
			return null;
		}
	}
	
	static class TestUserServiceException extends RuntimeException{
		
	}
	
	/*@Test
	public void transactionRollback(){
		int i=0;
		for(User user : userService.getAll()){
			System.out.println(++i +"::::"+ user.getId());
			userService.update(user); //강제로 쓰기 시도를 한다. 여기서 읽기전용 속성으로 인한 예외가 발생해야 한다.
		}
	}*/
	
	// 자동 프록시 생성기로 프록시 생성이 되었는지 확인하는 테스트
	@Test
	public void advisorAutoProxyCreator(){
		//assertThat(userService, instanceOf(springbook.user.service.UserServiceImpl.class));
		assertThat(userService, instanceOf(java.lang.reflect.Proxy.class));
		assertThat(testUserService, instanceOf(java.lang.reflect.Proxy.class));
	}
	
	// 목 오브젝트로 만든 메일 전송 확인용 클래스
	static class MockMailSender implements MailSender{
		// UserService로부터 전송 요청을 받은 메일 주소를 저장해두고 이를 읽을 수 있게 한다.
		private List<String> requests = new ArrayList<String>();
		
		public List<String> getRequests(){
			return requests;
		}
		
		public void send(SimpleMailMessage mailMessage) throws MailException {
			requests.add(mailMessage.getTo()[0]); //전송 요청을 받은 이메일 주소를 저장해둔다. 간단하게 첫번째 수신자 메일 주소만 저장했다.
		}
		public void send(SimpleMailMessage... mailMessage) throws MailException {
			
		}
		
	}
	
	//userService 빈이 잘 등록됐는지 확인 후 삭제해도 되는 테스트
	//@Test
	public void bean(){
		assertThat(this.userService, is(notNullValue()));
	}
	
	// 등록된 빈 내역을 조회하는 테스트 메소드(스프링 기본 제공. 오토와이어링만 하면 됨)
	@Autowired DefaultListableBeanFactory bf;
	
	@Test
	public void beans(){
		for(String n : bf.getBeanDefinitionNames()){
			System.out.println(n+" \t " +bf.getBean(n).getClass().getName());
		}
	}
	
}
