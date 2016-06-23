package springbook.user.sqlservice.updatable;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import springbook.user.sqlservice.SqlNotFoundException;
import springbook.user.sqlservice.SqlUpdateFailureException;
import springbook.user.sqlservice.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry {

	JdbcTemplate jdbc;
	
	// DataSource를 DI 받는다.
	public void setDataSource(DataSource dataSource){
		jdbc = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void registerSql(String key, String sql) {
		jdbc.update("insert into sqlmap(key_, sql_) values(?,?)", key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		try{
			return jdbc.queryForObject("select sql_ from sqlmap where key_ = ?", String.class, key);
		}catch(EmptyResultDataAccessException e){
			// queryForObject()는 결과가 없으면 이 예외가 발생한다.
			throw new SqlNotFoundException(key + "에 대한 sql을 찾을 수 없어요.");
		}
	}

	@Override
	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		int affected = jdbc.update("update sqlmap set sql_ = ? where key_ = ?", sql, key);
		
		if(affected == 0){
			throw new SqlUpdateFailureException(key + "에 대한 sql을 찾을 수 없어요. 업데이트 불가!");
		}
	}

	@Override
	public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
		for(Map.Entry<String, String> entry : sqlmap.entrySet()){
			updateSql(entry.getKey(), entry.getValue());
		}
	}

}
