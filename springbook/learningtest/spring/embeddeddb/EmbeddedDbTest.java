package springbook.learningtest.spring.embeddeddb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 * @author Engineer135
 *
 * ������ DB �׽�Ʈ
 */
public class EmbeddedDbTest {
	EmbeddedDatabase db;
	JdbcTemplate  template;
	
	@Before
	public void setUp(){
		db = new EmbeddedDatabaseBuilder()
				.setType(HSQL) //HSQL, DERBY, H2 �������� �ϳ� ������ �� �ִ�.
				.addScript("classpath:/springbook/learningtest/spring/embeddeddb/schema.sql")
				.addScript("classpath:/springbook/learningtest/spring/embeddeddb/data.sql")
				.build();
		
		template = new JdbcTemplate(db);
	}
	
	@After
	public void tearDown(){
		// �� �׽�Ʈ�� ������ �ڿ� DB�� �����Ѵ�. ������ �޸� DB�� ���� �������� �ʴ� �� ���ø����̼ǰ� �Բ� �Ź� ���Ӱ�  DB�� ���������
		// ���ŵǴ� �����ֱ⸦ ���´�.
		db.shutdown(); 
	}
	
	@Test
	public void initData(){
		assertThat(template.queryForInt("SELECT COUNT(1) FROM SQLMAP"), is(2));
		
		List<Map<String, Object>> list = template.queryForList("SELECT * FROM SQLMAP ORDER BY KEY_");
		assertThat((String)list.get(0).get("key_"), is("KEY1"));
		assertThat((String)list.get(0).get("sql_"), is("SQL1"));
		assertThat((String)list.get(1).get("key_"), is("KEY2"));
		assertThat((String)list.get(1).get("sql_"), is("SQL2"));
	}
	
	@Test
	public void insert(){
		template.update("insert into sqlmap(key_, sql_) values(?,?)", "KEY3", "SQL3");
		assertThat(template.queryForInt("SELECT COUNT(1) FROM SQLMAP"), is(3));
	}
}
