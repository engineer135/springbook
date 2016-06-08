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
	// 상속의 단점을 피하기 위해 DB 연결을 별도의 클래스로 생성
	private SimpleConnectionMaker simpleConnectionMaker;
	
	public UserDao() {
		// 한번만 만들어서 인스턴스 변수에 저장해두고 메소드에서 사용!
		simpleConnectionMaker = new SimpleConnectionMaker();
	}
	*/
	
	//private ConnectionMaker connectionMaker;
	
	// DataSource 인터페이스로 변환
	private DataSource dataSource;
	
	/*public UserDao() {
		// N사와 D사 각각 맞춰서 준비시켜준다.
		// 여전히 UserDao의 생성자 메소드를 수정해줘야 하는 문제가 있다!
		// UserDao와 UserDao가 사용할 ConnectionMaker의 특정 구현 클래스 사이의 관걔를 설정해주는 것에 관한 관심! 이것을 분리해야 한다.
		connectionMaker = new DConnectionMaker();
		//connectionMaker = new NConnectionMaker();
	}*/
	
	// 생성자에서 관계를 만들지 않고, 클라이언트가 전달한 오브젝트를 사용하도록 변경
	// UserDao가 ConnectionMaker라는 인터페이스에만 의존하고 있다는 것. 이게 DI의 핵심인듯하다.
	// 이 얘기는 ConnectionMaker를 구현하기만 하고 있다면 어떤 오브젝트든지 사용할 수 있다는 뜻.
	/*public UserDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}*/
	
	// 생성자로 의존관계 주입을 하지 않고, 수정자 메소드를 이용해 주입한다.
	/*public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}*/
	
	// DataSource 인터페이스로 변환
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void add(User user) throws SQLException {
		// 인터페이스에 정의된 메소드를 사용하므로, 메소드 이름 변경할 걱정 없다. 
		// 핵심은 UserDao를 뜯어고치지 않아도 된다는 것.
		//Connection c = this.connectionMaker.makeConnection();
		
		// DataSource 인터페이스로 변환
		Connection c = this.dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}
	
	public User get(String id) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		User user = null;
		
		try{
			//Connection c = this.connectionMaker.makeConnection();
			
			// DataSource 인터페이스로 변환
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
				}catch(SQLException e){// rs.close() 메소드에서도 SQLException이 발생할 수 있기 때문에 잡아줘야 한다. 요거 안잡아주면, 여기서 에러난 경우에 아래에 있는 c.close는 타지도 못하고 메소드를 빠져나간다.
					
				}
			}
			
			if(ps != null){
				try{
					ps.close();
				}catch(SQLException e){// ps.close() 메소드에서도 SQLException이 발생할 수 있기 때문에 잡아줘야 한다. 요거 안잡아주면, 여기서 에러난 경우에 아래에 있는 c.close는 타지도 못하고 메소드를 빠져나간다.
					
				}
			}
			
			if(c != null){
				try{
					c.close();
				}catch(SQLException e){// c.close() 메소드에서도 SQLException이 발생할 수 있기 때문에 잡아줘야 한다.
					
				}
			}
		}
		
		if(user == null){
			throw new EmptyResultDataAccessException(1);
		}
		
		return user;
	}
	
	public void deleteAll() throws SQLException{
		Connection c = null;
		PreparedStatement ps = null;
		
		try{
			// 예외가 발생할 가능성이 있는 코드를 모두 try 블록으로 묶어준다.
			c = this.dataSource.getConnection();
			
			ps = c.prepareStatement("delete from users");
			ps.executeUpdate();
		}catch(SQLException e){
			// 로그를 남기거나.. 일단 여기서는 그냥 던지기만 한다.
			throw e;
		}finally{
			if(ps != null){
				try{
					ps.close();
				}catch(SQLException e){// ps.close() 메소드에서도 SQLException이 발생할 수 있기 때문에 잡아줘야 한다. 요거 안잡아주면, 여기서 에러난 경우에 아래에 있는 c.close는 타지도 못하고 메소드를 빠져나간다.
					
				}
			}
			
			if(c != null){
				try{
					c.close();
				}catch(SQLException e){// c.close() 메소드에서도 SQLException이 발생할 수 있기 때문에 잡아줘야 한다.
					
				}
			}
		}
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
				}catch(SQLException e){// rs.close() 메소드에서도 SQLException이 발생할 수 있기 때문에 잡아줘야 한다. 요거 안잡아주면, 여기서 에러난 경우에 아래에 있는 c.close는 타지도 못하고 메소드를 빠져나간다.
					
				}
			}
			
			if(ps != null){
				try{
					ps.close();
				}catch(SQLException e){// ps.close() 메소드에서도 SQLException이 발생할 수 있기 때문에 잡아줘야 한다. 요거 안잡아주면, 여기서 에러난 경우에 아래에 있는 c.close는 타지도 못하고 메소드를 빠져나간다.
					
				}
			}
			
			if(c != null){
				try{
					c.close();
				}catch(SQLException e){// c.close() 메소드에서도 SQLException이 발생할 수 있기 때문에 잡아줘야 한다.
					
				}
			}
		}
		
		return count;
	}
	
	/**
	 * 
	 * * UserDao의 getConnection 메소드를 abstract로 처리
	 * NUserDao, DUserDao 가 UserDao를 상속 후 필요에 맞게 구현하도록 사용 (Template Method Pattern or Factory Method Pattern)
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	//public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
	
}
