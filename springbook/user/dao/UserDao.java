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
	
	public void add(final User user) throws SQLException {
		
		// AddStatement 클래스를 로컬 클래스로 만든다. 클래스 파일이 많아지는 것을 해결.
		class AddStatement implements StatementStrategy {
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
				
				// user 정보는 생성자를 통해 제공받는다.
				
				// 로컬 클래스가 되면, 외부의 메소드 로컬 변수에 직접 접근이 가능. 생성자를 통해 받을 필요가 없어진다. 
				// 다만, 외부 변수는 반드시 final로 선언해줘야 한다.
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				
				return ps;
			}
		}
		
		StatementStrategy st = new AddStatement(); // 선정한 전략 클래스의 오브젝트 생성 - 생성자 파라미터로 user를 전달하지 않아도 된다.
		this.jdbcContextWithStatementStrategy(st); // 컨텍스트 호출. 전략 오브젝트 전달.
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
		StatementStrategy st = new DeleteAllStatement(); // 선정한 전략 클래스의 오브젝트 생성
		this.jdbcContextWithStatementStrategy(st); // 컨텍스트 호출. 전략 오브젝트 전달.
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
	
	
	// 클라이언트
	// 컨텍스트
	// 전략
	// 전략 선택, 생성
	
	// 클라이언트(여기선 deleteAll()메소드)가 컨텍스트(공통사용 코드.. jdbcContextWithStatementStrategy) 에게 
	// 전략(StatementStrategy인터페이스를 구현한 특정 오브젝트)을 제공한다.
	// 클라이언트가 전략 선택(DeleteAllStatement라는 StatementStrategy인터페이스 구현한 오브젝트)을 한다.
	// 이것이 전략 패턴이다. DI의 핵심은 이처럼 제3자의 도움을 통해 두 오브젝트 사이의 유연한 관계가 설정되도록 만든다는 것!
	
	// 메소드로 분리한 try/catch/finally 컨텍스트(공통사용) 코드
	public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		
		try{
			// 예외가 발생할 가능성이 있는 코드를 모두 try 블록으로 묶어준다.
			c = this.dataSource.getConnection();
			
			// 이부분만 빼면 다 공통이잖아
			//ps = c.prepareStatement("delete from users");
			
			// StatementStrategy 인터페이스의 구현 오브젝트를 여기서 써주면 전략 패턴에 맞지 않지!
			//StatementStrategy strategy = new DeleteAllStatement();
			//ps = strategy.makePreparedStatement(c);
			
			// 그래서 이렇게 분리시킨다. 클라이언트(UserDao)로부터 StatementStrategy 타입의 전략 오브젝트를 제공받고, 작업 수행.
			ps = stmt.makePreparedStatement(c);
			
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
	
}
