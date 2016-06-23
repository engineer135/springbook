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

/**
 * @author Engineer135
 *
 * 쿼리 수정 기능 테스트
 */
public class ConcurrentHashMapSqlRegistryTest {
	UpdatableSqlRegistry sqlRegistry;
	
	@Before
	public void setUp(){
		sqlRegistry = new ConcurrentHashMapSqlRegistry();
		sqlRegistry.registerSql("KEY1", "SQL1");
		sqlRegistry.registerSql("KEY2", "SQL2");
		sqlRegistry.registerSql("KEY3", "SQL3");
	}
	
	@Test
	public void find(){
		checkFindResult("SQL1","SQL2","SQL3");
	}
	
	private void checkFindResult(String expected1, String expected2, String expected3){
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
