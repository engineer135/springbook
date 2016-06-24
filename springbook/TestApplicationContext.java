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
 * applicationContext.xml을 대체할 DI 설정정보를 담은 클래스
 * @Configuration 어노테이션을 붙여주면 오케이
 *
 */
@Configuration
//@ImportResource("/applicationContext.xml") //XML의 DI 정보를 활용한다.

// tx-annotation-driven을 대체할 수 있는 애노테이션. 스프링 3.1부터 생김 대박!!!
@EnableTransactionManagement

// 자동 빈 스캔 기능을 사용하겠어요.
@ComponentScan(basePackages="springbook.user")
public class TestApplicationContext {
	
	@Bean
	public DataSource dataSource(){
		// 왜 DataSource가 아닌 SimpleDriverDataSource로 선언하는가?
		// DataSource에는 setUrl이나 setUsername 같은 수정자 메소드가 없거든. 우린 이게 필요한데!
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
	
	// UserDao 자체를 그냥 Autowired 처리
	// 그리고 구현 클래스인 UserDaoJdbc에 @Component 애노테이션을 붙여주면 된다.
	// @Component는 빈으로 등록될 후보 클래스에 붙여주는 일종의 마커(Marker)라고 보면 된다.
	// 그렇다고 후보를 자동 등록해주진 않기 때문에, 빈 스캔 기능을 사용하겠다는 @ComponentScan을 this 클래스에 붙여주면 오케이.
	@Autowired
	UserDao userDao;
	
	/*@Bean
	public UserService userService(){
		UserServiceImpl service = new UserServiceImpl();
		//service.setUserDao(this.userDao);
		//service.setMailSender(this.mailSender());
		return service;
	}*/
	
	// TestUserService 같은 내부 클래스는 접근 제어자를 public으로 해줘야 한다.
	// xml일때는 private도 빈의 클래스로 사용 가능하지만 자바코드로 참조할때는 불가능하므로.
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
	
	@Bean
	public SqlService sqlService(){
		OxmSqlService sqlService = new OxmSqlService();
		sqlService.setSqlmap(new ClassPathResource("springbook/user/dao/sqlmap.xml"));
		sqlService.setUnmarshaller(this.unmarshaller());
		sqlService.setSqlRegistry(this.sqlRegistry());
		return sqlService;
	}
	
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
	@Bean
	public DataSource embeddedDatabase(){
		return new EmbeddedDatabaseBuilder()
				.setName("embeddedDatabase")
				.setType(HSQL) //HSQL, DERBY, H2 세가지중 하나 선택할 수 있다.
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
