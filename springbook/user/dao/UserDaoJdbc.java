package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserDaoJdbc implements UserDao {
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
	// jdbcTemplate ���鼭 ����� �� ������
	// private DataSource dataSource;
	
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
		//this.dataSource = dataSource;
		
		// JdbcContext ������ ������ ������ ����ϴ� ��� UserDao���� ���� DI�� �����ϴ� ������� ����
		// 2. DAO �ڵ带 �̿��� �������� DI �� ��� 
		// 1. ���� - �ܺο� �� ���谡 �巯���� ����. 
		// 2. ���� - JdbcContext�� ���� ������Ʈ�� ����ϴ��� �̱������� ���� �� ����, DI �۾��� ���� �ΰ����� �ڵ尡 �ʿ�.
		//this.jdbcContext = new JdbcContext();
		//this.jdbcContext.setDataSource(dataSource);
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//private JdbcContext jdbcContext;
	
	// �������� �����ϴ� jdbcTemplate. jdbcContext�� ������ �̰ɷ� ����Ÿ��.
	private JdbcTemplate jdbcTemplate;
	
	// �������̽� ���� DAO�� ������ ���踦 ���� Ŭ������ DI�� �����ϴ� ���� �ΰ���
	// 1. ������ ������ ����ؼ� ��������� 
	// ���� - ������Ʈ ������ ���� �������谡 �������Ͽ� ��Ȯ�ϰ� �巯��
	// ���� - DI�� �ٺ����� ��ġ�� �������� �ʴ� ��ü���� Ŭ�������� ���谡 ������ ���� �����
	/*public void setJdbcContext(JdbcContext jdbcContext) {//JdbcContext�� DI �޿��� �����.
		this.jdbcContext = jdbcContext;
	}*/
	
	// �ߺ� �ڵ� �и��� ����
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
	
	public void add(final User user) {
		//this.jdbcContext.executeSqlWithParam("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
		
		/*try{
			// JDBC�� �̿��� USER ������ DB�� �߰��ϴ� �ڵ� �Ǵ� 
			// �׷� ����� �ִ� �ٸ� SQLException�� ������ �޼ҵ带 ȣ���ϴ� �ڵ�...
			
			this.jdbcContext.executeSqlWithParam("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
		}catch(SQLException e){
			// ErrorCode�� MySql�� Duplicate Entry(1062)�̸� ���� ��ȯ
			if(e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY){
				throw new DuplicateUserIdException(e);
			}else{
				throw new RuntimeException(e); //���� ����
			}
		}*/
		// jdbcTemplate �� ����Ÿ�� 2�ܰ� (���� �ݹ� ���)
		this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend, email) values(?,?,?,?,?,?,?)"
				, user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
	}
	
	public User get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?", new Object[] {id}
			, this.userMapper);
	}
	
	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id", 
				this.userMapper
		);
	}
	
	public void deleteAll() {
		//this.jdbcContext.executeSql("delete from users");
		
		// jdbcTemplate �� ����Ÿ�� 1�ܰ�
		/*this.jdbcTemplate.update(
				new PreparedStatementCreator(){
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						return con.prepareStatement("delete from users");
					}
				}
		);*/
		
		// jdbcTemplate �� ����Ÿ�� 2�ܰ� (���� �ݹ� ���)
		this.jdbcTemplate.update("delete from users");
	}
	
	public int getCount() {
		// jdbcTemplate �� ����Ÿ�� 1�ܰ�
		// ��� �ݹ��� �ΰ���. ���� ���� �����;� �ϱ⿡!
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
		
		// jdbcTemplate �� ����Ÿ�� 2�ܰ�
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}

	public void update(User user) {
		System.out.println("������Ʈ ����!");
		System.out.println(user.getId());
		System.out.println(user.getName());
		int result = this.jdbcTemplate.update("update users set name=?, password=?, level=?, login=?, recommend=?, email=? where id=?"
				, user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId() );
		System.out.println(result);
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
