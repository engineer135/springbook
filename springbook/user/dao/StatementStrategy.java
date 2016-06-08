package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Engineer135
 *
 * DAO 공통으로 쓰는 부분(DB 커넥션 가져오기, 닫기 등등)을 줄이기 위해.. 전략 패턴 적용을 위한 inserface
 */
public interface StatementStrategy {
	PreparedStatement makePreparedStatement(Connection c) throws SQLException;
}
