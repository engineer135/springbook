package springbook;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * @author Engineer135
 * 
 * applicationContext.xml�� ��ü�� DI ���������� ���� Ŭ����
 * @Configuration ������̼��� �ٿ��ָ� ������
 *
 */
@Configuration
@ImportResource("/applicationContext.xml") //XML�� DI ������ Ȱ���Ѵ�.
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
}
