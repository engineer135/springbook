package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

// 로그인수, 추천수는 상수로 만들어서 쓴다.
//import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
//import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	DataSource dataSource; //트랜잭션 적용을 위해 DI
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	// User 오브젝트는 스프링이 IoC로 관리해주는 오브젝트가 아니기 때문에, 생성자 호출해서 테스트할 User 오브젝트를 만들면 된다.
	User user;
	
	List<User> users;
	@Before
	public void setUp(){
		users = Arrays.asList(//배열을 리스트로 만들어주는 메소드.
				new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
				new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1),
				new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_LOGCOUNT_FOR_SILVER),
				new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
		);
		
		user = new User();
	}
	
	@Test
	public void upgradeLevels() throws Exception{
		userDao.deleteAll();
		for(User user : users){
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
		// 각 사용자별 업그레이드 후의 예상 레벨 검증
		/*checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);*/
		
		// 업그레이드 확인 테스트 개선
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
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
		UserService testUserService = new TestUserService(users.get(3).getId());//예외를 발생시킬 네번째 사용자의 id
		testUserService.setUserDao(this.userDao);//userDao 수동 DI
		
		//트랜잭션을 위해 추가
		//testUserService.setDataSource(this.dataSource);
		
		testUserService.setTransactionManager(transactionManager); //수동 DI
		
		userDao.deleteAll();
		for(User user : users){
			userDao.add(user);
		}
		
		try{
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected"); //TestUserService는 업그레이드 작업중에 예외가 발생해야 한다. 정상 종료라면 문제가 있으니 실패!
		}catch(TestUserServiceException e){
			//TestUserService가 던져주는 예외를 잡아서 계속 진행되도록 한다. 그 외의 예외라면 테스트 실패!
		}
		
		checkLevelUpgraded(users.get(1), false); //예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었나 확인!
	}
	
	// UserService의 트랜젝션 테스트를 위한 대역 클래스. 
	// 스태틱 클래스로 만든다.
	class TestUserService extends UserService{
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
	
	class TestUserServiceException extends RuntimeException{
		
	}
	
	//userService 빈이 잘 등록됐는지 확인 후 삭제해도 되는 테스트
	@Test
	public void bean(){
		assertThat(this.userService, is(notNullValue()));
	}
	
}
