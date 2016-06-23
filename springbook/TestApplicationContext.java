package springbook;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * @author Engineer135
 * 
 * applicationContext.xml을 대체할 DI 설정정보를 담은 클래스
 * @Configuration 어노테이션을 붙여주면 오케이
 *
 */
@Configuration
@ImportResource("/applicationContext.xml") //XML의 DI 정보를 활용한다.
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
}
