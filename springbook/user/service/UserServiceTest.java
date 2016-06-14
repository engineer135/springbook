package springbook.user.service;

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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.MockUserDao;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	DataSource dataSource; //트랜잭션 적용을 위해 DI
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
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
	
	
	@Test
	@DirtiesContext // 메일 발송 대상 확인하는 테스트. 컨텍스트의 DI 설정을 변경하는 테스트라는 것을 알려준다.
	public void upgradeLevels() throws Exception{
		//MockUserDao를 사용해 고립된 테스트를 만든다.
		// 고립된 테스ㅡ에서는 테스트 대상 오브젝트를 직접 생성하면 된다.
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		// 목 오브젝트로 만든 UserDao를 직접 DI해준다.
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		// 메일 발송 결과를 테스트할 수 있도록 목 오브젝트를 만들어 userService의 의존 오브젝트로 주입한다.
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
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
	@Test
	public void upgradeAllOrNothing() throws Exception{
		TestUserService testUserService = new TestUserService(users.get(3).getId());//예외를 발생시킬 네번째 사용자의 id
		testUserService.setUserDao(this.userDao);//userDao 수동 DI
		
		//트랜잭션을 위해 추가
		//testUserService.setDataSource(this.dataSource);
		
		//testUserService.setTransactionManager(transactionManager); //수동 DI
		
		testUserService.setMailSender(mailSender);
		
		//트랜잭션 기능을 분리한 UserServiceTx는 예외 발생용으로 수정할 필요가 없으니 그대로 사용한다.
		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);
		
		userDao.deleteAll();
		for(User user : users){
			userDao.add(user);
		}
		
		try{
			txUserService.upgradeLevels();// 트랜잭션 기능을 분리한 오브젝트를 통해 예외 발생용 TestUserService가 호출되게 해야 한다.
			fail("TestUserServiceException expected"); //TestUserService는 업그레이드 작업중에 예외가 발생해야 한다. 정상 종료라면 문제가 있으니 실패!
		}catch(TestUserServiceException e){
			//TestUserService가 던져주는 예외를 잡아서 계속 진행되도록 한다. 그 외의 예외라면 테스트 실패!
		}
		
		checkLevelUpgraded(users.get(1), false); //예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었나 확인!
	}
	
	// UserService의 트랜젝션 테스트를 위한 대역 클래스. 
	// 스태틱 클래스로 만든다. 왜 스태틱으로 만들까...!?!?? 참조 -> http://secretroute.tistory.com/entry/%EC%9E%90%EB%B0%94%EC%9D%98%E7%A5%9E-Vol1-Nested-Class
	static class TestUserService extends UserServiceImpl{
		private String id;
		
		private TestUserService(String id){
			this.id = id; //예외를 발생시킬 User 오브젝트의 id를 지정할 수 있게 만든다.
		}
		
		protected void upgradeLevel(User user){// 오버라이드
			if(user.getId().equals(this.id)){
				throw new TestUserServiceException();// 지정된 id의 User 오브젝트가 발견되면 예외를 던져서 작업을 강제로 중단시킨다.
			}else{
				super.upgradeLevel(user);
			}
		}
	}
	
	static class TestUserServiceException extends RuntimeException{
		
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
	@Test
	public void bean(){
		assertThat(this.userService, is(notNullValue()));
	}
	
}
