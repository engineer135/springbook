package springbook.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class XmlSqlService implements SqlService {
	// 읽어온 sql을 저장해둘 맵
	private Map<String, String> sqlMap = new HashMap<String, String>();

	// 스프링이 오브젝트를 만드는 시점에서 SQL을 읽어오도록 생성자를 이용하자
	public XmlSqlService(){
		// JAXB API를 이용해 XML 문서를 오브젝트 트리로 읽어온다!
		String contextPath = Sqlmap.class.getPackage().getName();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml");//UserDao와 같은 클래스패스의 sqlmap.xml 파일을 변환한다.
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlmap.getSql()){
				sqlMap.put(sql.getKey(), sql.getValue());
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException(e); //복구 불가능한 예외이므로 런타임 예외로 포장해서 던진다.
		}
	}
	
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		if(sql == null){
			throw new SqlRetrievalFailureException(key + "에 대한 sql을 찾을 수 없어요!");
		}else{
			return sql;
		}
	}

}
