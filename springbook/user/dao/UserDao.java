package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	
	private ConnectionMaker connectionMaker;
	
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
	public UserDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException {
		// �������̽��� ���ǵ� �޼ҵ带 ����ϹǷ�, �޼ҵ� �̸� ������ ���� ����. 
		// �ٽ��� UserDao�� ����ġ�� �ʾƵ� �ȴٴ� ��.
		Connection c = this.connectionMaker.makeConnection();
		
		PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}
	
	public User get(String id) throws SQLException, ClassNotFoundException {
		Connection c = this.connectionMaker.makeConnection();
		
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
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
