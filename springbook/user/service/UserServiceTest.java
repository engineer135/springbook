package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
	
	@Autowired
	MailSender mailSender;
	
	// User ������Ʈ�� �������� IoC�� �������ִ� ������Ʈ�� �ƴϱ� ������, ������ ȣ���ؼ� �׽�Ʈ�� User ������Ʈ�� ����� �ȴ�.
	User user;
	
	List<User> users;
	@Before
	public void setUp(){
		users = Arrays.asList(//�迭�� ����Ʈ�� ������ִ� �޼ҵ�.
				new User("bumjin", "�ڹ���", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "everafterk@naver.com"),
				new User("joytouch", "����", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "everafterk2@naver.com"),
				new User("erwins", "�Ž���", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "everafterk3@naver.com"),
				new User("madnite1", "�̻�ȣ", "p4", Level.SILVER, 60, MIN_LOGCOUNT_FOR_SILVER, "everafterk4@naver.com"),
				new User("green", "���α�", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "everafterk5@naver.com")
		);
		
		user = new User();
	}
	
	
	@Test
	@DirtiesContext // ���� �߼� ��� Ȯ���ϴ� �׽�Ʈ. ���ؽ�Ʈ�� DI ������ �����ϴ� �׽�Ʈ��� ���� �˷��ش�.
	public void upgradeLevels() throws Exception{
		userDao.deleteAll();
		for(User user : users){
			userDao.add(user);
		}
		
		// ���� �߼� ����� �׽�Ʈ�� �� �ֵ��� �� ������Ʈ�� ����� userService�� ���� ������Ʈ�� �����Ѵ�.
		MockMailSender mockMailSender = new MockMailSender();
		userService.setMailSender(mockMailSender);
		
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
		
		// �� ������Ʈ�� ����� ���� ������ ����� ������ ���׷��̵� ���� ��ġ�ϴ��� Ȯ���Ѵ�!
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
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
		
		testUserService.setMailSender(mailSender);
		
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
	// ����ƽ Ŭ������ �����. �� ����ƽ���� �����...!?!?? ���� -> http://secretroute.tistory.com/entry/%EC%9E%90%EB%B0%94%EC%9D%98%E7%A5%9E-Vol1-Nested-Class
	static class TestUserService extends UserService{
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
	
	static class TestUserServiceException extends RuntimeException{
		
	}
	
	// �� ������Ʈ�� ���� ���� ���� Ȯ�ο� Ŭ����
	static class MockMailSender implements MailSender{
		// UserService�κ��� ���� ��û�� ���� ���� �ּҸ� �����صΰ� �̸� ���� �� �ְ� �Ѵ�.
		private List<String> requests = new ArrayList<String>();
		
		public List<String> getRequests(){
			return requests;
		}
		
		public void send(SimpleMailMessage mailMessage) throws MailException {
			requests.add(mailMessage.getTo()[0]); //���� ��û�� ���� �̸��� �ּҸ� �����صд�. �����ϰ� ù��° ������ ���� �ּҸ� �����ߴ�.
		}
		public void send(SimpleMailMessage... mailMessage) throws MailException {
			
		}
		
	}
	
	//userService ���� �� ��ϵƴ��� Ȯ�� �� �����ص� �Ǵ� �׽�Ʈ
	@Test
	public void bean(){
		assertThat(this.userService, is(notNullValue()));
	}
	
}
