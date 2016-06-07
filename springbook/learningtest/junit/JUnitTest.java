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

// jUnit은 테스트 메소드를 수행할때마다 새로운 오브젝트를 만든다고 하였음. 이를 테스트하기 위한 테스트 클래스임.
@RunWith(SpringJUnit4ClassRunner.class) // 스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
@ContextConfiguration(locations="/applicationContext.xml") //테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트의 위치 지정
public class JUnitTest {
	//static JUnitTest testObject;
	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	
	@Autowired
	ApplicationContext context; // 테스트 컨텍스트가 매번 주입해주는 애플리케이션 컨텍스트는 항상 같은 오브젝트인지 테스트로 확인해본다.
	
	static ApplicationContext contextObject = null;
	
	static UserDao userDaoObject;
	
	@Autowired
	UserDao userDao1; //스프링이 싱글톤 방식으로 빈의 오브젝트를 만드는지 검증
	
	// UserDao userDao2 = context.getBean("userDao", UserDao.class); // 왜 이건 NullPointerException이 뜰까...?
	// 밑에처럼 Autowired 방식이 아닌 객체 생성 방식으로 어플리케이션 컨텍스트를 가져오면, NullPointerException이 나지 않는다.. 
	// 결국... Autowired 방식은 메소드를 호출하기 직전(?)에 DI를 하는게 아닐까. 
	// 아래와 같은 방법은 컴파일시에 DI가 되는거고? 추측일 뿐...;;
	ApplicationContext context2 = new GenericXmlApplicationContext("applicationContext.xml");
	UserDao dao = context2.getBean("userDao", UserDao.class); // 첫번째 인자는 빈의 이름, 두번째 인자는 리턴 타입
	
	@Test
	public void test1(){
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertThat(contextObject == null || contextObject == this.context, is(true));
		contextObject = this.context;
		
		//스프링이 싱글톤 방식으로 빈의 오브젝트를 만드는지 검증
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
