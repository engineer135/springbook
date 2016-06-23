package springbook.learningtest.spring.oxm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import springbook.user.sqlservice.SqlNotFoundException;
import springbook.user.sqlservice.SqlUpdateFailureException;
import springbook.user.sqlservice.UpdatableSqlRegistry;
import springbook.user.sqlservice.updatable.ConcurrentHashMapSqlRegistry;

public abstract class AbstractUpdatableSqlRegistryTest {
	UpdatableSqlRegistry sqlRegistry;
	
	@Before
	public void setUp(){
		// 오직 이 문장만 특정 클래스에 의존하고 있다. 이것을 abstract 메소드로 분리시킨다.
		sqlRegistry = createUpdatableSqlRegistry();
		sqlRegistry.registerSql("KEY1", "SQL1");
		sqlRegistry.registerSql("KEY2", "SQL2");
		sqlRegistry.registerSql("KEY3", "SQL3");
	}
	
	// 서브클래스에서 이를 구현해야함!
	abstract protected UpdatableSqlRegistry createUpdatableSqlRegistry();
	
	@Test
	public void find(){
		checkFindResult("SQL1","SQL2","SQL3");
	}
	
	// 서브클래스에 테스트를 추가한다면 필요할 수 있으므로 접근이 가능하도록 protected로 변경
	protected void checkFindResult(String expected1, String expected2, String expected3){
		assertThat(sqlRegistry.findSql("KEY1"), is(expected1));
		assertThat(sqlRegistry.findSql("KEY2"), is(expected2));
		assertThat(sqlRegistry.findSql("KEY3"), is(expected3));
	}
	
	@Test(expected=SqlNotFoundException.class)
	public void unknownKey(){
		sqlRegistry.findSql("SQL9999!@#$");
	}
	
	@Test
	public void updateSingle(){
		sqlRegistry.updateSql("KEY2","Modified2");
		checkFindResult("SQL1","Modified2","SQL3");
	}
	
	@Test
	public void updateMulti(){
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY3", "Modified3");
		
		sqlRegistry.updateSql(sqlmap);
		checkFindResult("Modified1","SQL2","Modified3");
	}
	
	@Test(expected=SqlUpdateFailureException.class)
	public void updateWithNotExistingKey(){
		sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
	}
}
