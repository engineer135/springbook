package springbook.user.sqlservice;

/**
 * @author Engineer135
 *
 * XmlSqlService�� ���ɻ� �и�
 * SqlRegistry ������Ʈ�� �޼ҵ� �Ķ���ͷ� DI �޾Ƽ� �������� SQL�� ����ϴ� �� ����ϵ��� ������ ��.
 */
public interface SqlReader {
		void read(SqlRegistry sqlRegistry);
}
