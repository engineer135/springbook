package springbook;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import springbook.user.sqlservice.OxmSqlService;
import springbook.user.sqlservice.SqlRegistry;
import springbook.user.sqlservice.SqlService;
import springbook.user.sqlservice.updatable.EmbeddedDbSqlRegistry;

@Configuration
public class SqlServiceContext {
	
	@Bean
	public SqlService sqlService(){
		OxmSqlService sqlService = new OxmSqlService();
		sqlService.setSqlmap(new ClassPathResource("springbook/user/dao/sqlmap.xml"));
		sqlService.setUnmarshaller(this.unmarshaller());
		sqlService.setSqlRegistry(this.sqlRegistry());
		return sqlService;
	}
	
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
