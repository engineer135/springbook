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
		// JAXB API�� �̿��� XML ������ ������Ʈ Ʈ���� �о�´�!
		String contextPath = Sqlmap.class.getPackage().getName();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);//UserDao�� ���� Ŭ�����н��� sqlmap.xml ������ ��ȯ�Ѵ�.
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlmap.getSql()){
				sqlRegistry.registerSql(sql.getKey(), sql.getValue()); //SQL ���� ���� ������ �������� �������̽� �޼ҵ带 ���� �о���� SQL�� Key�� ����!!
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException(e); //���� �Ұ����� �����̹Ƿ� ��Ÿ�� ���ܷ� �����ؼ� ������.
		}
	}

}
