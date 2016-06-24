package springbook;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
//@ImportResource("/applicationContext.xml") //XML�� DI ������ Ȱ���Ѵ�.

//tx-annotation-driven�� ��ü�� �� �ִ� �ֳ����̼�. ������ 3.1���� ���� ���!!!
@EnableTransactionManagement

//�ڵ� �� ��ĵ ����� ����ϰھ��.
@ComponentScan(basePackages="springbook.user")

//���� �������� ����Ʈ
//@Import(SqlServiceContext.class)

//���������� �̿�����. ��� �������� Ŭ������ ����Ʈ�Ѵ�.
@Import({SqlServiceContext.class, TestAppContext.class, ProductionAppContext.class})
public class AppContext {

	@Bean
	public DataSource dataSource(){
		// �� DataSource�� �ƴ� SimpleDriverDataSource�� �����ϴ°�?
		// DataSource���� setUrl�̳� setUsername ���� ������ �޼ҵ尡 ���ŵ�. �츰 �̰� �ʿ��ѵ�!
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		dataSource.setDriverClass(org.mariadb.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mariadb://localhost/springbook?characterEncoding=utf-8");
		dataSource.setUsername("spring");
		dataSource.setPassword("book");
		
		return dataSource;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(){
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(this.dataSource()); // �ٸ� �� ������Ʈ�� ������Ƽ�� ��������� �ϴ� ��� �̷� ������ ó��!
		return tm;
	}
	
	// Autowired ����ϸ� XML�� ���ǵ� �� �����ͼ� ����!
	/*@Autowired
	SqlService sqlService;*/
	
	//@Bean
	/*public UserDao userDao(){
		UserDaoJdbc userDao = new UserDaoJdbc();
		
		// �̰� �ڵ� ���̾ �ϰ� �;�? �׷� UserDaoJdbc �� setDataSource �޼ҵ� or DataSource �ʵ忡 @Autowired �ٿ��ָ� ��
		// But DastaSource�� ������ �޼ҵ尡 �ƴ� �ʵ忡 �ٿ��� ��
		// �ֳĸ� ������ �޼ҵ尡 �ܼ��� �־��� ������Ʈ�� �ʵ忡 �����ϴ� ���� �ƴ϶� JdbcTemplate�� �����ؼ� �������ֱ� �����̴�.
		//userDao.setDataSource(this.dataSource());
		
		// �̰͵� ���������� �ڵ� ���̾~
		// userDao.setSqlService(this.sqlService());
		
		return userDao;
	}*/
	
	/*@Bean
	public UserService userService(){
		UserServiceImpl service = new UserServiceImpl();
		//service.setUserDao(this.userDao);
		//service.setMailSender(this.mailSender());
		return service;
	}*/
	
	//@Resource�� @Autowired �� �������� 
	// @Resource�� �ʵ� �̸� �������� ���� ã��(�ʵ��̸� = �� ���̵�)
	// @Autowired�� �ʵ� Ÿ���� �������� ã�´ٴ� ��! ���⼱ DataSource Ÿ���� dataSource ���� �����ϹǷ� Ÿ�� �������� ���Թ����� ȥ�� �߻�.
	// EmbeddedDatabase�� DataSource�� ��ӹ޾ұ� �����̴�!
	
	//@Resource
	//EmbeddedDatabase embeddedDatabase;
	
	// �׷���... Resource�� �ϴ� �ȵȴ�. 
	// Bean named 'embeddedDatabase' must be of type [org.springframework.jdbc.datasource.embedded.EmbeddedDatabase], 
	// but was actually of type [org.springframework.jdbc.datasource.SimpleDriverDataSource]
	// �̷� ������ �������� ��... ����� ���� �׳� ��ŵ�ϰ� �ڹ��ڵ�� �����.
}
