package springbook.user.sqlservice;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class JaxbXmlSqlReader implements SqlReader {

	private String sqlmapFile;
	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

	@Override
	public void read(SqlRegistry sqlRegistry) {
		// JAXB API를 이용해 XML 문서를 오브젝트 트리로 읽어온다!
		String contextPath = Sqlmap.class.getPackage().getName();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);//UserDao와 같은 클래스패스의 sqlmap.xml 파일을 변환한다.
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlmap.getSql()){
				sqlRegistry.registerSql(sql.getKey(), sql.getValue()); //SQL 저장 로직 구현에 독립적인 인터페이스 메소드를 통해 읽어들인 SQL과 Key를 전달!!
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException(e); //복구 불가능한 예외이므로 런타임 예외로 포장해서 던진다.
		}
	}

}
