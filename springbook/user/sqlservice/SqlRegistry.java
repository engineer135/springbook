package springbook.user.sqlservice;

/**
 * @author Engineer135
 * 
 * XmlSqlService의 관심사 분리
 * Sql을 제공받아 등록해뒀다가 키로 검색해서 돌려주는 기능 담당
 *
 */
public interface SqlRegistry {
	void registerSql(String key, String sql);//sql을 키와 함께 등록
	String findSql(String key) throws SqlNotFoundException; //키로 sql을 검색한다. 검색 실패하면 예외 던짐
}
