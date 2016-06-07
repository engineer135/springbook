package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.either;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;

// jUnit�� �׽�Ʈ �޼ҵ带 �����Ҷ����� ���ο� ������Ʈ�� ����ٰ� �Ͽ���. �̸� �׽�Ʈ�ϱ� ���� �׽�Ʈ Ŭ������.
@RunWith(SpringJUnit4ClassRunner.class) // �������� �׽�Ʈ ���ؽ�Ʈ �����ӿ�ũ�� JUnit Ȯ���� ����
@ContextConfiguration(locations="/applicationContext.xml") //�׽�Ʈ ���ؽ�Ʈ�� �ڵ����� ������� ���ø����̼� ���ؽ�Ʈ�� ��ġ ����
public class JUnitTest {
	//static JUnitTest testObject;
	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	
	@Autowired
	ApplicationContext context; // �׽�Ʈ ���ؽ�Ʈ�� �Ź� �������ִ� ���ø����̼� ���ؽ�Ʈ�� �׻� ���� ������Ʈ���� �׽�Ʈ�� Ȯ���غ���.
	
	static ApplicationContext contextObject = null;
	
	static UserDao userDaoObject;
	
	@Autowired
	UserDao userDao1; //�������� �̱��� ������� ���� ������Ʈ�� ������� ����
	
	// UserDao userDao2 = context.getBean("userDao", UserDao.class); // �� �̰� NullPointerException�� ���...?
	// �ؿ�ó�� Autowired ����� �ƴ� ��ü ���� ������� ���ø����̼� ���ؽ�Ʈ�� ��������, NullPointerException�� ���� �ʴ´�.. 
	// �ᱹ... Autowired ����� �޼ҵ带 ȣ���ϱ� ����(?)�� DI�� �ϴ°� �ƴұ�. 
	// �Ʒ��� ���� ����� �����Ͻÿ� DI�� �Ǵ°Ű�? ������ ��...;;
	ApplicationContext context2 = new GenericXmlApplicationContext("applicationContext.xml");
	UserDao dao = context2.getBean("userDao", UserDao.class); // ù��° ���ڴ� ���� �̸�, �ι�° ���ڴ� ���� Ÿ��
	
	@Test
	public void test1(){
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertThat(contextObject == null || contextObject == this.context, is(true));
		contextObject = this.context;
		
		//�������� �̱��� ������� ���� ������Ʈ�� ������� ����
		UserDao userDao2 = context.getBean("userDao", UserDao.class);
		
		System.out.println(userDao1);
		System.out.println(userDao2);
		
		assertThat(userDao1 == userDao2, is(true));
	}
	
	@Test
	public void test2(){
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertTrue(contextObject == null || contextObject == this.context);
		contextObject = this.context;
		
	}
	
	@Test
	public void test3(){
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertThat(contextObject, either(is(nullValue())).or(is(this.context)));
		contextObject = this.context;
		
	}
}
