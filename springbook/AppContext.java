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
//@ImportResource("/applicationContext.xml") //XML의 DI 정보를 활용한다.

//tx-annotation-driven을 대체할 수 있는 애노테이션. 스프링 3.1부터 생김 대박!!!
@EnableTransactionManagement

//자동 빈 스캔 기능을 사용하겠어요.
@ComponentScan(basePackages="springbook.user")

//보조 설정정보 임포트
//@Import(SqlServiceContext.class)

//프로파일을 이용하자. 모든 설정정보 클래스를 임포트한다.
//@Import({SqlServiceContext.class, TestAppContext.class, ProductionAppContext.class})

// 중첩 클래스로 만들면서 임포트 문 수정
@Import({SqlServiceContext.class})

// 프로퍼티 소스 등록
@PropertySource("/database.properties")
public class AppContext implements SqlMapConfig {
	
	// sqlMap.xml의 위치를 가져오는 인터페이스를 여기에서 구현함으로써
	// 파일 위치가 바뀌어도 sqlServiceContext 수정할 필요가 없어짐. 독립된 모듈로 쓸 수 있다는 이야기!
	// SQL 서비스가 필요한 애플리케이션은 메인 설정 클래스에서 @Import로 SqlServiceContext 빈 설정을 추가하고
	// SqlMapConfig를 구현해 매핑파일의 위치를 지정해주기만 하면 오케이.
	@Override
	public Resource getSqlMapResource() {
		return new ClassPathResource("sqlmap.xml", UserDao.class);
	}
	
	// 스프링이 프로퍼티 값을 저장해놓는 Enviroment 타입의 환경 오브젝트
	@Autowired
	Environment env;
	
	// Environment를 쓰는 대신 치환자를 이용하는 방법도 있음
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
		// 왜 DataSource가 아닌 SimpleDriverDataSource로 선언하는가?
		// DataSource에는 setUrl이나 setUsername 같은 수정자 메소드가 없거든. 우린 이게 필요한데!
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		// 1. 프로퍼티 값 사용할때 Environment 이용 방식
		/*try{
			dataSource.setDriverClass((Class<? extends Driver>) Class.forName(env.getProperty("db.driverClass")));
		}catch(ClassNotFoundException e){
			throw new RuntimeException(e);
		}
		dataSource.setUrl(env.getProperty("db.url"));
		dataSource.setUsername(env.getProperty("db.username"));
		dataSource.setPassword(env.getProperty("db.password"));*/
		
		// 2. 치환자 이용 방식(깔끔하긴 하나 PropertySourcesPlaceholderConfigurer 필드를 또 선언해줘야 한다는게.. 둘중에 편한거 쓸것)
		dataSource.setDriverClass(this.driverClass);
		dataSource.setUrl(this.url);
		dataSource.setUsername(this.username);
		dataSource.setPassword(this.password);
		
		return dataSource;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(){
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(this.dataSource()); // 다른 빈 오브젝트를 프로퍼티에 주입해줘야 하는 경우 이런 식으로 처리!
		return tm;
	}
	
	// Autowired 사용하면 XML에 정의된 빈 가져와서 주입!
	/*@Autowired
	SqlService sqlService;*/
	
	//@Bean
	/*public UserDao userDao(){
		UserDaoJdbc userDao = new UserDaoJdbc();
		
		// 이걸 자동 와이어링 하고 싶어? 그럼 UserDaoJdbc 의 setDataSource 메소드 or DataSource 필드에 @Autowired 붙여주면 됨
		// But DastaSource는 수정자 메소드가 아닌 필드에 붙여야 함
		// 왜냐면 수정자 메소드가 단순히 주어진 오브젝트를 필드에 저장하는 것이 아니라 JdbcTemplate를 생성해서 저장해주기 때문이다.
		//userDao.setDataSource(this.dataSource());
		
		// 이것도 마찬가지로 자동 와이어링~
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
	
	//@Resource와 @Autowired 의 차이점은 
	// @Resource는 필드 이름 기준으로 빈을 찾고(필드이름 = 빈 아이디)
	// @Autowired는 필드 타입을 기준으로 찾는다는 점! 여기선 DataSource 타입의 dataSource 빈이 존재하므로 타입 기준으로 주입받으면 혼란 발생.
	// EmbeddedDatabase는 DataSource를 상속받았기 때문이다!
	
	//@Resource
	//EmbeddedDatabase embeddedDatabase;
	
	// 그러나... Resource로 하니 안된다. 
	// Bean named 'embeddedDatabase' must be of type [org.springframework.jdbc.datasource.embedded.EmbeddedDatabase], 
	// but was actually of type [org.springframework.jdbc.datasource.SimpleDriverDataSource]
	// 이런 에러가 떠버리니 뭐... 방법이 없네 그냥 스킵하고 자바코드로 만든다.
	
	
	
	// production 컨텍스트와 test 컨텍스트를 이너 클래스로 변경한다. 각각 독립적으로 사용될 수 있게 스태틱으로 만들어야 한다.
	// 이렇게 하면 깔끔하다. 
	
	@Configuration
	@Profile("production")
	public static class ProductionAppContext {
		// 운영용 메일 서버를 지원하는 MailSender 빈을 등록해줘야 한다.
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
			// UserService 생성 안하고 바로 하위 클래스 생성해서 쓰기 때문에... static으로 되어있어야 한다. http://www.devblog.kr/r/8y0gFPAvJ2j8MWIVVXucyP9uYvQegfSVbY5XNDkHt
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
