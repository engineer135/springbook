package springbook.learningtest.spring.oxm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class OxmTest {
	@Autowired
	Unmarshaller unmarshaller; //스프링이 제공하는 언마샬러를 임포트해야 한다. 
	
	@Test
	public void unmarshallSqlMap() throws XmlMappingException, IOException {
		//inputStream을 이용하는 Source 타입의 StreamSource를 만든다.
		Source xmlSource = new StreamSource(getClass().getResourceAsStream("sqlmap.xml"));
		
		// 어떤 OXM 기술이든 언마샬은 이 한줄이면 끝이다! 스프링 서비스 추상화 대박!
		Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource);
		
		List<SqlType> sqlList = sqlmap.getSql();
		
		assertThat(sqlList.size(), is(6));
		assertThat(sqlList.get(0).getKey(), is("userAdd"));
		
	}
}
