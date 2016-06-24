package springbook;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springbook.user.dao.UserDao;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.UserServiceTest.TestUserService;
import springbook.user.sqlservice.OxmSqlService;
import springbook.user.sqlservice.SqlRegistry;
import springbook.user.sqlservice.SqlService;
import springbook.user.sqlservice.updatable.EmbeddedDbSqlRegistry;

/**
 * @author Engineer135
 * 
 * applicationContext.xml�� ��ü�� DI ���������� ���� Ŭ����
 * @Configuration ������̼��� �ٿ��ָ� ������
 *
 */
@Configuration
//@ImportResource("/applicationContext.xml") //XML�� DI ������ Ȱ���Ѵ�.

// tx-annotation-driven�� ��ü�� �� �ִ� �ֳ����̼�. ������ 3.1���� ���� ���!!!
@EnableTransactionManagement

// �ڵ� �� ��ĵ ����� ����ϰھ��.
@ComponentScan(basePackages="springbook.user")
public class TestApplicationContext {
	
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
	
	// UserDao ��ü�� �׳� Autowired ó��
	// �׸��� ���� Ŭ������ UserDaoJdbc�� @Component �ֳ����̼��� �ٿ��ָ� �ȴ�.
	// @Component�� ������ ��ϵ� �ĺ� Ŭ������ �ٿ��ִ� ������ ��Ŀ(Marker)��� ���� �ȴ�.
	// �׷��ٰ� �ĺ��� �ڵ� ��������� �ʱ� ������, �� ��ĵ ����� ����ϰڴٴ� @ComponentScan�� this Ŭ������ �ٿ��ָ� ������.
	@Autowired
	UserDao userDao;
	
	/*@Bean
	public UserService userService(){
		UserServiceImpl service = new UserServiceImpl();
		//service.setUserDao(this.userDao);
		//service.setMailSender(this.mailSender());
		return service;
	}*/
	
	// TestUserService ���� ���� Ŭ������ ���� �����ڸ� public���� ����� �Ѵ�.
	// xml�϶��� private�� ���� Ŭ������ ��� ���������� �ڹ��ڵ�� �����Ҷ��� �Ұ����ϹǷ�.
	@Bean
	public UserService testUserService(){
		// UserService ���� ���ϰ� �ٷ� ���� Ŭ���� �����ؼ� ���� ������... static���� �Ǿ��־�� �Ѵ�. http://www.devblog.kr/r/8y0gFPAvJ2j8MWIVVXucyP9uYvQegfSVbY5XNDkHt
		TestUserService testService = new TestUserService();
		//testService.setUserDao(this.userDao);
		//testService.setMailSender(this.mailSender());
		return testService;
	}
	
	@Bean
	public MailSender mailSender(){
		return new DummyMailSender();
	}
	
	@Bean
	public SqlService sqlService(){
		OxmSqlService sqlService = new OxmSqlService();
		sqlService.setSqlmap(new ClassPathResource("springbook/user/dao/sqlmap.xml"));
		sqlService.setUnmarshaller(this.unmarshaller());
		sqlService.setSqlRegistry(this.sqlRegistry());
		return sqlService;
	}
	
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
	@Bean
	public DataSource embeddedDatabase(){
		return new EmbeddedDatabaseBuilder()
				.setName("embeddedDatabase")
				.setType(HSQL) //HSQL, DERBY, H2 �������� �ϳ� ������ �� �ִ�.
				.addScript("classpath:/springbook/user/sqlservice/updatable/sqlRegistrySchema.sql")
				//.addScript("classpath:/springbook/learningtest/spring/embeddeddb/data.sql")
				.build();
	}
	
	@Bean
	public SqlRegistry sqlRegistry(){
		EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
		sqlRegistry.setDataSource(this.embeddedDatabase());
		return sqlRegistry;
	}
	
	@Bean
	public Unmarshaller unmarshaller(){
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("springbook.user.sqlservice.jaxb");
		return marshaller;
	}
}
