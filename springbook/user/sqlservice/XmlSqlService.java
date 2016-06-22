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

/**
 * @author Engineer135
 *
 * ���ɻ縦 �и����� sqlReader, sqlService, sqlRegistry ������ �������̽��� �����ϵ��� �����.
 */
public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {
	// DI �ޱ� ���� �������̽� Ÿ���� ������Ƽ�� ����
	private SqlReader sqlReader;
	private SqlRegistry sqlRegistry;
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	// �о�� sql�� �����ص� ��
	private Map<String, String> sqlMap = new HashMap<String, String>();// sqlMap�� sqlRegistry ������ �Ϻΰ� �ȴ�. ���� �ܺο��� ���� ���� �Ұ�!
	
	// ������ �ܺο��� ������ �� �ֵ���
	private String sqlmapFile;
	
	// sqlMapFile�� sqlReader ������ �Ϻΰ� �ȴ�. ���� SqlReader ���� �޼ҵ带 ������ �ʰ�� �����ϸ� �ȵſ�!
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

	// �������� ������Ʈ�� ����� �������� SQL�� �о������ �����ڸ� �̿�����
	// ���� �޼ҵ�� �и���Ų��!
	/*public XmlSqlService(){
		
	}*/
	
	public String getSql(String key) throws SqlRetrievalFailureException {
		try{
			return this.sqlRegistry.findSql(key);
		}catch(SqlNotFoundException e){
			throw new SqlRetrievalFailureException(e);
		}
	}
	
	// �����ڿ��� ���ܰ� �߻��� ���� �ִ� ������ �ʱ�ȭ �۾��� �ϴ°��� ���� �����Ƿ� ���� ����.
	@PostConstruct // �� �ֳ����̼��� ������ ���������� �� ������Ʈ�� �����ϰ� di �۾��� ��ģ �ڿ�.. ������Ƽ���� ��� �غ�� �Ŀ�!! �� �޼ҵ带 �ڵ����� �������ش�! ���!!
	// ���ø����̼� ���ؽ�Ʈ���� context:annotation-config�� �߰��ؾ��Ѵ�!
	public void loadSql(){
		this.sqlReader.read(this.sqlRegistry);
	}
	
	
	
	
	
	
	
	
	
	

	@Override
	// HashMap�̶�� ����Ҹ� ����ϴ� ��ü���� ���� ������� ������ �� �ֵ��� �������̽��� �޼ҵ�� �����ϰ� ���ش�.
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if(sql == null){
			throw new SqlRetrievalFailureException(key + "�� ���� sql�� ã�� �� �����! sqlRegistry ����ü�Դϴ�.");
		}else{
			return sql;
		}
	}

	@Override
	// loadSql()�� �ִ� �ڵ带 SqlReader �޼ҵ�� �����´�. �ʱ�ȭ�� ���� ������ �Ұ��ΰ��� SQL�� ��� �д����� �и�!!!
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
