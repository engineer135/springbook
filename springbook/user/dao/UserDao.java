package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 */
public class UserDao {
	/*
	// ����� ������ ���ϱ� ���� DB ������ ������ Ŭ������ ����
	private SimpleConnectionMaker simpleConnectionMaker;
	
	public UserDao() {
		// �ѹ��� ���� �ν��Ͻ� ������ �����صΰ� �޼ҵ忡�� ���!
		simpleConnectionMaker = new SimpleConnectionMaker();
	}
	*/
	
	//private ConnectionMaker connectionMaker;
	
	// DataSource �������̽��� ��ȯ
	private DataSource dataSource;
	
	/*public UserDao() {
		// N��� D�� ���� ���缭 �غ�����ش�.
		// ������ UserDao�� ������ �޼ҵ带 ��������� �ϴ� ������ �ִ�!
		// UserDao�� UserDao�� ����� ConnectionMaker�� Ư�� ���� Ŭ���� ������ ���¸� �������ִ� �Ϳ� ���� ����! �̰��� �и��ؾ� �Ѵ�.
		connectionMaker = new DConnectionMaker();
		//connectionMaker = new NConnectionMaker();
	}*/
	
	// �����ڿ��� ���踦 ������ �ʰ�, Ŭ���̾�Ʈ�� ������ ������Ʈ�� ����ϵ��� ����
	// UserDao�� ConnectionMaker��� �������̽����� �����ϰ� �ִٴ� ��. �̰� DI�� �ٽ��ε��ϴ�.
	// �� ���� ConnectionMaker�� �����ϱ⸸ �ϰ� �ִٸ� � ������Ʈ���� ����� �� �ִٴ� ��.
	/*public UserDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}*/
	
	// �����ڷ� �������� ������ ���� �ʰ�, ������ �޼ҵ带 �̿��� �����Ѵ�.
	/*public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}*/
	
	// DataSource �������̽��� ��ȯ
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		
		// JdbcContext ������ ������ ������ ����ϴ� ��� UserDao���� ���� DI�� �����ϴ� ������� ����
		// 2. DAO �ڵ带 �̿��� �������� DI �� ��� 
		// 1. ���� - �ܺο� �� ���谡 �巯���� ����. 
		// 2. ���� - JdbcContext�� ���� ������Ʈ�� ����ϴ��� �̱������� ���� �� ����, DI �۾��� ���� �ΰ����� �ڵ尡 �ʿ�.
		this.jdbcContext = new JdbcContext();
		this.jdbcContext.setDataSource(dataSource);
	}
	
	private JdbcContext jdbcContext;
	
	// �������̽� ���� DAO�� ������ ���踦 ���� Ŭ������ DI�� �����ϴ� ���� �ΰ���
	// 1. ������ ������ ����ؼ� ��������� 
	// ���� - ������Ʈ ������ ���� �������谡 �������Ͽ� ��Ȯ�ϰ� �巯��
	// ���� - DI�� �ٺ����� ��ġ�� �������� �ʴ� ��ü���� Ŭ�������� ���谡 ������ ���� �����
	/*public void setJdbcContext(JdbcContext jdbcContext) {//JdbcContext�� DI �޿��� �����.
		this.jdbcContext = jdbcContext;
	}*/
	
	public void add(final User user) throws SQLException {
		this.jdbcContext.executeSqlWithParam("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
	}
	
	public User get(String id) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		User user = null;
		
		try{
			//Connection c = this.connectionMaker.makeConnection();
			
			// DataSource �������̽��� ��ȯ
			c = this.dataSource.getConnection();
			
			ps = c.prepareStatement("select * from users where id = ?");
			ps.setString(1, id);
			
			rs = ps.executeQuery();
			
			if(rs.next()){
				user = new User();
				user.setId(rs.getString("id"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
			}
		}catch(SQLException e){
			throw e;
		}finally{
			if(rs != null){
				try{
					rs.close();
				}catch(SQLException e){// rs.close() �޼ҵ忡���� SQLException�� �߻��� �� �ֱ� ������ ������ �Ѵ�. ��� ������ָ�, ���⼭ ������ ��쿡 �Ʒ��� �ִ� c.close�� Ÿ���� ���ϰ� �޼ҵ带 ����������.
					
				}
			}
			
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
		
		if(user == null){
			throw new EmptyResultDataAccessException(1);
		}
		
		return user;
	}
	
	public void deleteAll() throws SQLException{
		this.jdbcContext.executeSql("delete from users");
	}
	
	public int getCount() throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		
		try{
			c = this.dataSource.getConnection();
			
			ps = c.prepareStatement("select count(1) from users");
			
			rs = ps.executeQuery();
			rs.next();
			
			count = rs.getInt(1);
		}catch(SQLException e){
			throw e;
		}finally{
			if(rs != null){
				try{
					rs.close();
				}catch(SQLException e){// rs.close() �޼ҵ忡���� SQLException�� �߻��� �� �ֱ� ������ ������ �Ѵ�. ��� ������ָ�, ���⼭ ������ ��쿡 �Ʒ��� �ִ� c.close�� Ÿ���� ���ϰ� �޼ҵ带 ����������.
					
				}
			}
			
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
		
		return count;
	}
	
	/**
	 * 
	 * * UserDao�� getConnection �޼ҵ带 abstract�� ó��
	 * NUserDao, DUserDao �� UserDao�� ��� �� �ʿ信 �°� �����ϵ��� ��� (Template Method Pattern or Factory Method Pattern)
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	//public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
}
