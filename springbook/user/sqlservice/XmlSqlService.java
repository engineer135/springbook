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
	// �о�� sql�� �����ص� ��
	private Map<String, String> sqlMap = new HashMap<String, String>();

	// �������� ������Ʈ�� ����� �������� SQL�� �о������ �����ڸ� �̿�����
	public XmlSqlService(){
		// JAXB API�� �̿��� XML ������ ������Ʈ Ʈ���� �о�´�!
		String contextPath = Sqlmap.class.getPackage().getName();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml");//UserDao�� ���� Ŭ�����н��� sqlmap.xml ������ ��ȯ�Ѵ�.
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlmap.getSql()){
				sqlMap.put(sql.getKey(), sql.getValue());
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException(e); //���� �Ұ����� �����̹Ƿ� ��Ÿ�� ���ܷ� �����ؼ� ������.
		}
	}
	
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		if(sql == null){
			throw new SqlRetrievalFailureException(key + "�� ���� sql�� ã�� �� �����!");
		}else{
			return sql;
		}
	}

}
