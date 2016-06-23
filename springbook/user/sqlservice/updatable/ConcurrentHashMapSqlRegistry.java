package springbook.user.sqlservice.updatable;

import java.util.HashMap;
import java.util.Map;

import springbook.user.sqlservice.SqlNotFoundException;
import springbook.user.sqlservice.SqlRetrievalFailureException;
import springbook.user.sqlservice.SqlUpdateFailureException;
import springbook.user.sqlservice.UpdatableSqlRegistry;

public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {

	private Map<String, String> sqlMap = new HashMap<String, String>();
	
	@Override
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if(sql == null){
			throw new SqlNotFoundException(key + "�� ���� sql�� ã�� �� �����.");
		}else{
			return sql;
		}
	}

	@Override
	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		if(sqlMap.get(key) == null){
			throw new SqlUpdateFailureException(key + "�� ���� sql�� ã�� �� �����. ������Ʈ �Ұ�!");
		}
		
		sqlMap.put(key, sql);
	}

	@Override
	public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
		for(Map.Entry<String, String> entry : sqlmap.entrySet()){
			updateSql(entry.getKey(), entry.getValue());
		}
	}

}
