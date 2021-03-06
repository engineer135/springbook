package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.sqlservice.SqlService;

// 빈 아이디는 클래스 이름을 따라간다.
// 만약 아이디를 지정해주고 싶다면 @Component("id")로 하면 된다.
//@Component

// 데이터 액세스 서비스 제공하는 빈은 Component 대신 Repository를 쓴다
@Repository
public class UserDaoJdbc implements UserDao {
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
	// jdbcTemplate 쓰면서 사용할 일 없어짐
	// private DataSource dataSource;
	
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
	@Autowired
	public void setDataSource(DataSource dataSource) {
		//this.dataSource = dataSource;
		
		// JdbcContext 생성을 스프링 빈으로 등록하는 대신 UserDao에서 직접 DI를 적용하는 방법으로 수정
		// 2. DAO 코드를 이용해 수동으로 DI 한 경우 
		// 1. 장점 - 외부에 그 관계가 드러나지 않음. 
		// 2. 단점 - JdbcContext를 여러 오브젝트가 사용하더라도 싱글톤으로 만들 수 없고, DI 작업을 위한 부가적인 코드가 필요.
		//this.jdbcContext = new JdbcContext();
		//this.jdbcContext.setDataSource(dataSource);
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//private JdbcContext jdbcContext;
	
	// 스프링이 제공하는 jdbcTemplate. jdbcContext를 버리고 이걸로 갈아타자.
	private JdbcTemplate jdbcTemplate;
	
	// 인터페이스 없이 DAO와 밀접한 관계를 갖는 클래스를 DI에 적용하는 법은 두가지
	// 1. 스프링 빈으로 등록해서 사용했을때 
	// 장점 - 오브젝트 사이의 실제 의존관계가 설정파일에 명확하게 드러남
	// 단점 - DI의 근본적인 원치게 부합하지 않는 구체적인 클래스와의 관계가 설정에 직접 노출됨
	/*public void setJdbcContext(JdbcContext jdbcContext) {//JdbcContext를 DI 받오록 만든다.
		this.jdbcContext = jdbcContext;
	}*/
	
	// 중복 코드 분리해 재사용
	private RowMapper<User> userMapper =
			new RowMapper<User>(){
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					User user = new User();
					user.setId(rs.getString("id"));
					user.setName(rs.getString("name"));
					user.setPassword(rs.getString("password"));
					
					user.setLevel(Level.valueOf(rs.getInt("level")));
					user.setLogin(rs.getInt("login"));
					user.setRecommend(rs.getInt("recommend"));
					
					user.setEmail(rs.getString("email"));
					return user;
				}
	};
	
	// SQL을 XML로 빼기 위해 추가
	private String sqlAdd;
	
	public void setSqlAdd(String sqlAdd) {
		this.sqlAdd = sqlAdd;
	}
	
	// sqlService 프로퍼티 추가
	@Autowired
	private SqlService sqlService;
	
	/*public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
	}*/

	public void add(final User user) {
		//this.jdbcContext.executeSqlWithParam("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
		
		/*try{
			// JDBC를 이용해 USER 정보를 DB에 추가하는 코드 또는 
			// 그런 기능이 있는 다른 SQLException을 던지는 메소드를 호출하는 코드...
			
			this.jdbcContext.executeSqlWithParam("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
		}catch(SQLException e){
			// ErrorCode가 MySql의 Duplicate Entry(1062)이면 예외 전환
			if(e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY){
				throw new DuplicateUserIdException(e);
			}else{
				throw new RuntimeException(e); //예외 포장
			}
		}*/
		// jdbcTemplate 로 갈아타기 2단계 (내장 콜백 사용)
		this.jdbcTemplate.update(this.sqlService.getSql("userAdd")
				, user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
	}
	
	public User get(String id) {
		return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGet"), new Object[] {id}
			, this.userMapper);
	}
	
	public List<User> getAll() {
		System.out.println("getAll 실행");
		return this.jdbcTemplate.query(this.sqlService.getSql("userGetAll"), 
				this.userMapper
		);
	}
	
	public void deleteAll() {
		//this.jdbcContext.executeSql("delete from users");
		
		// jdbcTemplate 로 갈아타기 1단계
		/*this.jdbcTemplate.update(
				new PreparedStatementCreator(){
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						return con.prepareStatement("delete from users");
					}
				}
		);*/
		
		// jdbcTemplate 로 갈아타기 2단계 (내장 콜백 사용)
		this.jdbcTemplate.update(this.sqlService.getSql("userDeleteAll"));
	}
	
	public int getCount() {
		// jdbcTemplate 로 갈아타기 1단계
		// 얘는 콜백이 두개다. 리턴 값을 가져와야 하기에!
		/*return this.jdbcTemplate.query(
				new PreparedStatementCreator(){
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						return con.prepareStatement("select count(*) from users");
					}}
				, new ResultSetExtractor<Integer>(){
					public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
						rs.next();
						return rs.getInt(1);
					}
				}
		);*/
		
		// jdbcTemplate 로 갈아타기 2단계
		return this.jdbcTemplate.queryForInt(this.sqlService.getSql("userGetCount"));
	}

	public void update(User user) {
		System.out.println("업데이트 실행!");
		System.out.println(user.getId());
		System.out.println(user.getName());
		int result = this.jdbcTemplate.update(this.sqlService.getSql("userUpdate")
				, user.getName(), user.getPassword(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId() );
		System.out.println(result);
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
