package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

// jUnit은 테스트 메소드를 수행할때마다 새로운 오브젝트를 만든다고 하였음. 이를 테스트하기 위한 테스트 클래스임.
public class JUnitTest {
	//static JUnitTest testObject;
	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	
	@Test
	public void test1(){
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}
	
	@Test
	public void test2(){
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}
	
	@Test
	public void test3(){
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}
}
