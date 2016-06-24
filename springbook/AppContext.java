package springbook;

import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springbook.user.dao.UserDao;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceTest.TestUserService;
import springbook.user.sqlservice.SqlMapConfig;

@Configuration
//@ImportResource("/applicationContext.xml") //XML�� DI ������ Ȱ���Ѵ�.

//tx-annotation-driven�� ��ü�� �� �ִ� �ֳ����̼�. ������ 3.1���� ���� ���!!!
@EnableTransactionManagement

//�ڵ� �� ��ĵ ����� ����ϰھ��.
@ComponentScan(basePackages="springbook.user")

//���� �������� ����Ʈ
//@Import(SqlServiceContext.class)

//���������� �̿�����. ��� �������� Ŭ������ ����Ʈ�Ѵ�.
//@Import({SqlServiceContext.class, TestAppContext.class, ProductionAppContext.class})

// ��ø Ŭ������ ����鼭 ����Ʈ �� ����
@Import({SqlServiceContext.class})

// ������Ƽ �ҽ� ���
@PropertySource("/database.properties")
public class AppContext implements SqlMapConfig {
	
	// sqlMap.xml�� ��ġ�� �������� �������̽��� ���⿡�� ���������ν�
	// ���� ��ġ�� �ٲ� sqlServiceContext ������ �ʿ䰡 ������. ������ ���� �� �� �ִٴ� �̾߱�!
	// SQL ���񽺰� �ʿ��� ���ø����̼��� ���� ���� Ŭ�������� @Import�� SqlServiceContext �� ������ �߰��ϰ�
	// SqlMapConfig�� ������ ���������� ��ġ�� �������ֱ⸸ �ϸ� ������.
	@Override
	public Resource getSqlMapResource() {
		return new ClassPathResource("sqlmap.xml", UserDao.class);
	}
	
	// �������� ������Ƽ ���� �����س��� Enviroment Ÿ���� ȯ�� ������Ʈ
	@Autowired
	Environment env;
	
	// Environment�� ���� ��� ġȯ�ڸ� �̿��ϴ� ����� ����
	@Value("${db.driverClass}") Class<? extends Driver> driverClass;
	@Value("${db.url}") String url;
	@Value("${db.username}") String username;
	@Value("${db.password}") String password;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer(){
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public DataSource dataSource(){
		// �� DataSource�� �ƴ� SimpleDriverDataSource�� �����ϴ°�?
		// DataSource���� setUrl�̳� setUsername ���� ������ �޼ҵ尡 ���ŵ�. �츰 �̰� �ʿ��ѵ�!
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		// 1. ������Ƽ �� ����Ҷ� Environment �̿� ���
		/*try{
			dataSource.setDriverClass((Class<? extends Driver>) Class.forName(env.getProperty("db.driverClass")));
		}catch(ClassNotFoundException e){
			throw new RuntimeException(e);
		}
		dataSource.setUrl(env.getProperty("db.url"));
		dataSource.setUsername(env.getProperty("db.username"));
		dataSource.setPassword(env.getProperty("db.password"));*/
		
		// 2. ġȯ�� �̿� ���(����ϱ� �ϳ� PropertySourcesPlaceholderConfigurer �ʵ带 �� ��������� �Ѵٴ°�.. ���߿� ���Ѱ� ����)
		dataSource.setDriverClass(this.driverClass);
		dataSource.setUrl(this.url);
		dataSource.setUsername(this.username);
		dataSource.setPassword(this.password);
		
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
	
	
	
	// production ���ؽ�Ʈ�� test ���ؽ�Ʈ�� �̳� Ŭ������ �����Ѵ�. ���� ���������� ���� �� �ְ� ����ƽ���� ������ �Ѵ�.
	// �̷��� �ϸ� ����ϴ�. 
	
	@Configuration
	@Profile("production")
	public static class ProductionAppContext {
		// ��� ���� ������ �����ϴ� MailSender ���� �������� �Ѵ�.
		@Bean
		public MailSender mailSender(){
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("mail.mycompany.com");
			return mailSender;
		}
	}
	
	@Configuration
	@Profile("test")
	public static class TestAppContext {
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
	}

}
