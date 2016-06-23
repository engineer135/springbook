package springbook.user.sqlservice;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class OxmSqlService implements SqlService {
	
	// �ڵ� �ߺ��� ���� ���� BaseSqlService�� �����ؼ� ó���Ѵ�.
	private final BaseSqlService baseSqlService = new BaseSqlService();
	
	// oxmSqlReader�� �޸� ���� ����Ʈ ������Ʈ�� ������� ������Ƽ��.
	// ���� �ʿ信 ���� DI�� ���� ��ü �����ϴ�.
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	// final�̹Ƿ� ������ �Ұ����ϴ�.
	// OxmSqlService�� OxmSqlReader�� ���ϰ� ���յż� �ϳ��� ������ ��ϵǰ� �ѹ��� ������ �� �ִ�.
	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
	
	// ���� ������Ʈ�� ������Ƽ�� �������ִ� �ڵ�
	// OxmSqlService�� ������ ������Ƽ�� ���� DI ���� ���� �״�� ��� Ŭ������ ������Ʈ�� �����Ѵ�.
	// �� setter���� ���� �� ���������� ���� â�� ������ �� ���̴�.
	public void setUnmarshaller(Unmarshaller unmarshaller){
		this.oxmSqlReader.setUnmarshaller(unmarshaller);
	}
	
	/*public void setSqlmapFile(String sqlmapFile){
		this.oxmSqlReader.setSqlmapFile(sqlmapFile);
	}*/
	
	// XML ������ Ŭ�����н��� �ƴ� Ư�� ������ ���󿡼� ������ �� �ְ� ����!
	public void setSqlmap(Resource sqlmap){
		this.oxmSqlReader.setSqlmap(sqlmap);
	}
	
	// privaet ��� Ŭ������ �����Ѵ�. �鷹�� Ŭ������ OxmSqlService���� ����� �� �ִ�.
	// �ΰ��� Ŭ������ ���ϰ� �����ϰ� ���̻��� Ȯ���̳� ������ �����ϴ� ����? -> OXM�� �̿��ϴ� ���� ������ ����ȭ�ϱ� ����!
	private class OxmSqlReader implements SqlReader{
		private final static String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
		
		private Unmarshaller unmarshaller;
		//private String sqlmapFile = DEFAULT_SQLMAP_FILE;
		
		// ����Ʈ ������ ������ ������ ������ Resource ���� Ŭ������ ClassPathResource�� �̿��Ѵ�.
		private Resource sqlmap = new ClassPathResource("sqlmap.xml");
		
		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}
		public void setSqlmap(Resource sqlmap) {
			this.sqlmap = sqlmap;
		}
		
		@Override
		public void read(SqlRegistry sqlRegistry) {
			try{
				//Source source = new StreamSource(UserDao.class.getResourceAsStream(this.sqlmapFile));
				
				// ���ҽ��� ������ ������� ��Ʈ������ ������ �� �ִ�.
				Source source = new StreamSource(sqlmap.getInputStream());
				
				// OxmSqlService�� ���� ���޹��� OXM �������̽� ���� ������Ʈ�� ������ �𸶼��� �۾� ����
				Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);
				
				for(SqlType sql : sqlmap.getSql()){
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
			}catch(IOException e){
				throw new IllegalArgumentException(this.sqlmap.getFilename() + "�� ������ ���� ����!", e);
			}
			
		}
	}
	
	
	
	
	@PostConstruct
	public void loadSql(){
		// OxmSqlService�� ������Ƽ�� ���ؼ� �ʱ�ȭ�� SqlReader�� SqlRegistry�� ���� �۾��� ������ ����� baseSqlService�� �����Ѵ�.
		this.baseSqlService.setSqlReader(this.oxmSqlReader);
		this.baseSqlService.setSqlRegistry(this.sqlRegistry);
		
		this.baseSqlService.loadSql();
		
		//this.oxmSqlReader.read(this.sqlRegistry);
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		// SQL�� ã�ƿ��� �۾��� baseSqlService�� �����Ѵ�.
		return this.baseSqlService.getSql(key);
		
		/*try{
			return this.sqlRegistry.findSql(key);
		}catch(SqlNotFoundException e){
			throw new SqlRetrievalFailureException(e);
		}*/
	}

}
