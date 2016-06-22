package springbook.user.sqlservice;

/**
 * @author Engineer135
 *
 * XmlSqlService의 관심사 분리
 * SqlRegistry 오브젝트를 메소드 파라미터로 DI 받아서 읽으들인 SQL을 등록하는 데 사용하도록 만들어야 함.
 */
public interface SqlReader {
		void read(SqlRegistry sqlRegistry);
}
