package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService;
	
	@Autowired
	UserDao userDao;
	
	// User 오브젝트는 스프링이 IoC로 관리해주는 오브젝트가 아니기 때문에, 생성자 호출해서 테스트할 User 오브젝트를 만들면 된다.
	User user;
	
	List<User> users;
	@Before
	public void setUp(){
		users = Arrays.asList(//배열을 리스트로 만들어주는 메소드.
				new User("bumjin", "박범진", "p1", Level.BASIC, 49, 0),
				new User("joytouch", "강명성", "p2", Level.BASIC, 50, 0),
				new User("erwins", "신승한", "p3", Level.SILVER, 60, 29),
				new User("madnite1", "이상호", "p4", Level.SILVER, 60, 30),
				new User("green", "오민규", "p5", Level.GOLD, 100, 100)
		);
		
		user = new User();
	}
	
	@Test
	public void upgradeLevels(){
		userDao.deleteAll();
		for(User user : users){
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
		// 각 사용자별 업그레이드 후의 예상 레벨 검증
		checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);
	}
	
	private void checkLevel(User user, Level expectedLevel){
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
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
	
	//userService 빈이 잘 등록됐는지 확인 후 삭제해도 되는 테스트
	@Test
	public void bean(){
		assertThat(this.userService, is(notNullValue()));
	}
	
}
