package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Engineer135
 *
 * DAO �������� ���� �κ�(DB Ŀ�ؼ� ��������, �ݱ� ���)�� ���̱� ����.. ���� ���� ������ ���� inserface
 */
public interface StatementStrategy {
	PreparedStatement makePreparedStatement(Connection c) throws SQLException;
}
