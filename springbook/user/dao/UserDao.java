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
		
		// JdbcContext 생성을 스프링 빈으로 등록하는 대신 UserDao에서 직접 DI를 적용하는 방법으로 수정
		// 2. DAO 코드를 이용해 수동으로 DI 한 경우 
		// 1. 장점 - 외부에 그 관계가 드러나지 않음. 
		// 2. 단점 - JdbcContext를 여러 오브젝트가 사용하더라도 싱글톤으로 만들 수 없고, DI 작업을 위한 부가적인 코드가 필요.
		this.jdbcContext = new JdbcContext();
		this.jdbcContext.setDataSource(dataSource);
	}
	
	private JdbcContext jdbcContext;
	
	// 인터페이스 없이 DAO와 밀접한 관계를 갖는 클래스를 DI에 적용하는 법은 두가지
	// 1. 스프링 빈으로 등록해서 사용했을때 
	// 장점 - 오브젝트 사이의 실제 의존관계가 설정파일에 명확하게 드러남
	// 단점 - DI의 근본적인 원치게 부합하지 않는 구체적인 클래스와의 관계가 설정에 직접 노출됨
	/*public void setJdbcContext(JdbcContext jdbcContext) {//JdbcContext를 DI 받오록 만든다.
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
