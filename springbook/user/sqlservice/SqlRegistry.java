package springbook.user.sqlservice;

/**
 * @author Engineer135
 * 
 * XmlSqlService�� ���ɻ� �и�
 * Sql�� �����޾� ����ص״ٰ� Ű�� �˻��ؼ� �����ִ� ��� ���
 *
 */
public interface SqlRegistry {
	void registerSql(String key, String sql);//sql�� Ű�� �Բ� ���
	String findSql(String key) throws SqlNotFoundException; //Ű�� sql�� �˻��Ѵ�. �˻� �����ϸ� ���� ����
}
