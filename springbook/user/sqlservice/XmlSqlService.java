package springbook.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class XmlSqlService implements SqlService {
	// �о�� sql�� �����ص� ��
	private Map<String, String> sqlMap = new HashMap<String, String>();
	
	// ������ �ܺο��� ������ �� �ֵ���
	private String sqlmapFile;
	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

	// �������� ������Ʈ�� ����� �������� SQL�� �о������ �����ڸ� �̿�����
	// ���� �޼ҵ�� �и���Ų��!
	/*public XmlSqlService(){
		
	}*/
	
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		if(sql == null){
			throw new SqlRetrievalFailureException(key + "�� ���� sql�� ã�� �� �����!");
		}else{
			return sql;
		}
	}
	
	// �����ڿ��� ���ܰ� �߻��� ���� �ִ� ������ �ʱ�ȭ �۾��� �ϴ°��� ���� �����Ƿ� ���� ����.
	@PostConstruct // �� �ֳ����̼��� ������ ���������� �� ������Ʈ�� �����ϰ� di �۾��� ��ģ �ڿ�.. ������Ƽ���� ��� �غ�� �Ŀ�!! �� �޼ҵ带 �ڵ����� �������ش�! ���!!
	// ���ø����̼� ���ؽ�Ʈ���� context:annotation-config�� �߰��ؾ��Ѵ�!
	public void loadSql(){
		// JAXB API�� �̿��� XML ������ ������Ʈ Ʈ���� �о�´�!
		String contextPath = Sqlmap.class.getPackage().getName();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);//UserDao�� ���� Ŭ�����н��� sqlmap.xml ������ ��ȯ�Ѵ�.
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlmap.getSql()){
				sqlMap.put(sql.getKey(), sql.getValue());
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException(e); //���� �Ұ����� �����̹Ƿ� ��Ÿ�� ���ܷ� �����ؼ� ������.
		}
		
	}

}
