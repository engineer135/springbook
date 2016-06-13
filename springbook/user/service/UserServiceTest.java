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

// �α��μ�, ��õ���� ����� ���� ����.
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
	DataSource dataSource; //Ʈ����� ������ ���� DI
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	// User ������Ʈ�� �������� IoC�� �������ִ� ������Ʈ�� �ƴϱ� ������, ������ ȣ���ؼ� �׽�Ʈ�� User ������Ʈ�� ����� �ȴ�.
	User user;
	
	List<User> users;
	@Before
	public void setUp(){
		users = Arrays.asList(//�迭�� ����Ʈ�� ������ִ� �޼ҵ�.
				new User("bumjin", "�ڹ���", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
				new User("joytouch", "����", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("erwins", "�Ž���", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1),
				new User("madnite1", "�̻�ȣ", "p4", Level.SILVER, 60, MIN_LOGCOUNT_FOR_SILVER),
				new User("green", "���α�", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
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
		
		// �� ����ں� ���׷��̵� ���� ���� ���� ����
		/*checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);*/
		
		// ���׷��̵� Ȯ�� �׽�Ʈ ����
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
	
	private void checkLevelUpgraded(User user, boolean upgraded){//� ������ �ٲ� ���ΰ��� �ƴ϶�, ���� ������ ���׷��̵�� ���ΰ� �ƴѰ��� �����Ѵ�.
		User userUpdate = userDao.get(user.getId());
		if(upgraded){
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel())); //���׷��̵� �ߴ��� Ȯ��
		}else{
			assertThat(userUpdate.getLevel(), is(user.getLevel()));// ���׷��̵� �� �ߴ��� Ȯ��
		}
	}
	
	@Test
	public void add(){
		userDao.deleteAll();
		
		User userWithLevel = users.get(4); //Gold ����. Gold ������ �̹� ������ User��� ���Խ�(add��) ���� �ʱ�ȭ ����
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null); //������ ����ִ� �����. ������ ���� ����߿� Basic ������ �����Ǿ�� �Ѵ�.
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		// DB�� ����� ����� ������ Ȯ���Ѵ�.
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(userWithoutLevel.getLevel()));
	}
	
	// User�� �߰��� upgradeLevel() �޼ҵ忡 ���� �׽�Ʈ
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
	
	//���� �߻� �� �۾� ��� ���� �׽�Ʈ
	@Test
	public void upgradeAllOrNothing() throws Exception{
		UserService testUserService = new TestUserService(users.get(3).getId());//���ܸ� �߻���ų �׹�° ������� id
		testUserService.setUserDao(this.userDao);//userDao ���� DI
		
		//Ʈ������� ���� �߰�
		//testUserService.setDataSource(this.dataSource);
		
		testUserService.setTransactionManager(transactionManager); //���� DI
		
		userDao.deleteAll();
		for(User user : users){
			userDao.add(user);
		}
		
		try{
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected"); //TestUserService�� ���׷��̵� �۾��߿� ���ܰ� �߻��ؾ� �Ѵ�. ���� ������ ������ ������ ����!
		}catch(TestUserServiceException e){
			//TestUserService�� �����ִ� ���ܸ� ��Ƽ� ��� ����ǵ��� �Ѵ�. �� ���� ���ܶ�� �׽�Ʈ ����!
		}
		
		checkLevelUpgraded(users.get(1), false); //���ܰ� �߻��ϱ� ���� ���� ������ �־��� ������� ������ ó�� ���·� �ٲ���� Ȯ��!
	}
	
	// UserService�� Ʈ������ �׽�Ʈ�� ���� �뿪 Ŭ����. 
	// ����ƽ Ŭ������ �����.
	class TestUserService extends UserService{
		private String id;
		
		private TestUserService(String id){
			this.id = id; //���ܸ� �߻���ų User ������Ʈ�� id�� ������ �� �ְ� �����.
		}
		
		protected void upgradeLevel(User user){// �������̵�
			if(user.getId().equals(this.id)){
				throw new TestUserServiceException();// ������ id�� User ������Ʈ�� �߰ߵǸ� ���ܸ� ������ �۾��� ������ �ߴܽ�Ų��.
			}else{
				super.upgradeLevel(user);
			}
		}
	}
	
	class TestUserServiceException extends RuntimeException{
		
	}
	
	//userService ���� �� ��ϵƴ��� Ȯ�� �� �����ص� �Ǵ� �׽�Ʈ
	@Test
	public void bean(){
		assertThat(this.userService, is(notNullValue()));
	}
	
}
