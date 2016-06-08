package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author Engineer135
 *
 * UserDao에 있던 jdbcContextWithStatementStrategy 메소드를 클래스로 따로 분리한다.
 * 다른 Dao에서도 쓸 수 있도록.
 */
public class JdbcContext {
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	// 클라이언트
	// 컨텍스트
	// 전략
	// 전략 선택, 생성
	
	// 클라이언트(여기선 deleteAll()메소드)가 컨텍스트(공통사용 코드.. jdbcContextWithStatementStrategy) 에게 
	// 전략(StatementStrategy인터페이스를 구현한 특정 오브젝트)을 제공한다.
	// 클라이언트가 전략 선택(DeleteAllStatement라는 StatementStrategy인터페이스 구현한 오브젝트)을 한다.
	// 이것이 전략 패턴이다. DI의 핵심은 이처럼 제3자의 도움을 통해 두 오브젝트 사이의 유연한 관계가 설정되도록 만든다는 것!
	
	// 메소드로 분리한 try/catch/finally 컨텍스트(공통사용) 코드
	public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
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
	
	public void executeSql(final String query) throws SQLException {
		workWithStatementStrategy(
				// 변하지 않는 콜백 클래스 정의와 오브젝트 생성.. 공통되는 부분을 분리해서 재활용할 수 있도록 한다.
				new StatementStrategy(){
					public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
						PreparedStatement ps = c.prepareStatement(query);
						return ps;
					}
				}
		); // 컨텍스트 호출. 전략 오브젝트 전달.
	}
	
	public void executeSqlWithParam(final String query, String... strs) throws SQLException {
		// 로컬 클래스에서 익명 내부 클래스로 전환. 클래스를 재사용할 필요가 없고, 구현한 인터페이스 타입으로만 사용할 경우에 유용하다.
		workWithStatementStrategy(
				new StatementStrategy(){
					public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
						PreparedStatement ps = c.prepareStatement(query);
						
						// user 정보는 생성자를 통해 제공받는다.
						
						// 로컬 클래스가 되면, 외부의 메소드 로컬 변수에 직접 접근이 가능. 생성자를 통해 받을 필요가 없어진다. 
						// 다만, 외부 변수는 반드시 final로 선언해줘야 한다.
						for(int i=0; i<strs.length; i++){
							ps.setString(i+1, strs[i]);
						}
						
						return ps;
					}
				}
		); // 컨텍스트 호출. 전략 오브젝트 전달.
	}
}
