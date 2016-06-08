package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author Engineer135
 *
 * UserDao�� �ִ� jdbcContextWithStatementStrategy �޼ҵ带 Ŭ������ ���� �и��Ѵ�.
 * �ٸ� Dao������ �� �� �ֵ���.
 */
public class JdbcContext {
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	// Ŭ���̾�Ʈ
	// ���ؽ�Ʈ
	// ����
	// ���� ����, ����
	
	// Ŭ���̾�Ʈ(���⼱ deleteAll()�޼ҵ�)�� ���ؽ�Ʈ(������ �ڵ�.. jdbcContextWithStatementStrategy) ���� 
	// ����(StatementStrategy�������̽��� ������ Ư�� ������Ʈ)�� �����Ѵ�.
	// Ŭ���̾�Ʈ�� ���� ����(DeleteAllStatement��� StatementStrategy�������̽� ������ ������Ʈ)�� �Ѵ�.
	// �̰��� ���� �����̴�. DI�� �ٽ��� ��ó�� ��3���� ������ ���� �� ������Ʈ ������ ������ ���谡 �����ǵ��� ����ٴ� ��!
	
	// �޼ҵ�� �и��� try/catch/finally ���ؽ�Ʈ(������) �ڵ�
	public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		
		try{
			// ���ܰ� �߻��� ���ɼ��� �ִ� �ڵ带 ��� try ������� �����ش�.
			c = this.dataSource.getConnection();
			
			// �̺κи� ���� �� �������ݾ�
			//ps = c.prepareStatement("delete from users");
			
			// StatementStrategy �������̽��� ���� ������Ʈ�� ���⼭ ���ָ� ���� ���Ͽ� ���� ����!
			//StatementStrategy strategy = new DeleteAllStatement();
			//ps = strategy.makePreparedStatement(c);
			
			// �׷��� �̷��� �и���Ų��. Ŭ���̾�Ʈ(UserDao)�κ��� StatementStrategy Ÿ���� ���� ������Ʈ�� �����ް�, �۾� ����.
			ps = stmt.makePreparedStatement(c);
			
			ps.executeUpdate();
		}catch(SQLException e){
			// �α׸� ����ų�.. �ϴ� ���⼭�� �׳� �����⸸ �Ѵ�.
			throw e;
		}finally{
			if(ps != null){
				try{
					ps.close();
				}catch(SQLException e){// ps.close() �޼ҵ忡���� SQLException�� �߻��� �� �ֱ� ������ ������ �Ѵ�. ��� ������ָ�, ���⼭ ������ ��쿡 �Ʒ��� �ִ� c.close�� Ÿ���� ���ϰ� �޼ҵ带 ����������.
					
				}
			}
			
			if(c != null){
				try{
					c.close();
				}catch(SQLException e){// c.close() �޼ҵ忡���� SQLException�� �߻��� �� �ֱ� ������ ������ �Ѵ�.
					
				}
			}
		}
	}
	
	public void executeSql(final String query) throws SQLException {
		workWithStatementStrategy(
				// ������ �ʴ� �ݹ� Ŭ���� ���ǿ� ������Ʈ ����.. ����Ǵ� �κ��� �и��ؼ� ��Ȱ���� �� �ֵ��� �Ѵ�.
				new StatementStrategy(){
					public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
						PreparedStatement ps = c.prepareStatement(query);
						return ps;
					}
				}
		); // ���ؽ�Ʈ ȣ��. ���� ������Ʈ ����.
	}
	
	public void executeSqlWithParam(final String query, String... strs) throws SQLException {
		// ���� Ŭ�������� �͸� ���� Ŭ������ ��ȯ. Ŭ������ ������ �ʿ䰡 ����, ������ �������̽� Ÿ�����θ� ����� ��쿡 �����ϴ�.
		workWithStatementStrategy(
				new StatementStrategy(){
					public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
						PreparedStatement ps = c.prepareStatement(query);
						
						// user ������ �����ڸ� ���� �����޴´�.
						
						// ���� Ŭ������ �Ǹ�, �ܺ��� �޼ҵ� ���� ������ ���� ������ ����. �����ڸ� ���� ���� �ʿ䰡 ��������. 
						// �ٸ�, �ܺ� ������ �ݵ�� final�� ��������� �Ѵ�.
						for(int i=0; i<strs.length; i++){
							ps.setString(i+1, strs[i]);
						}
						
						return ps;
					}
				}
		); // ���ؽ�Ʈ ȣ��. ���� ������Ʈ ����.
	}
}
