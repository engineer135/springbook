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
	
	List<User> users;
	@Before
	public void setUp(){
		users = Arrays.asList(//�迭�� ����Ʈ�� ������ִ� �޼ҵ�.
				new User("bumjin", "�ڹ���", "p1", Level.BASIC, 49, 0),
				new User("joytouch", "������", "p2", Level.BASIC, 50, 0),
				new User("erwins", "�Ž���", "p3", Level.SILVER, 60, 29),
				new User("madnite1", "�̻�ȣ", "p4", Level.SILVER, 60, 30),
				new User("green", "���α�", "p5", Level.GOLD, 100, 100)
		);
	}
	
	@Test
	public void upgradeLevels(){
		userDao.deleteAll();
		for(User user : users){
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
		// �� ����ں� ���׷��̵� ���� ���� ���� ����
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
	
	//userService ���� �� ��ϵƴ��� Ȯ�� �� �����ص� �Ǵ� �׽�Ʈ
	@Test
	public void bean(){
		assertThat(this.userService, is(notNullValue()));
	}
	
}