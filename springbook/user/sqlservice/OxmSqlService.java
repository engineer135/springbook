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
	
	// 코드 중복을 막기 위해 BaseSqlService로 위임해서 처리한다.
	private final BaseSqlService baseSqlService = new BaseSqlService();
	
	// oxmSqlReader와 달리 단지 디폴트 오브젝트로 만들어진 프로퍼티다.
	// 따라서 필요에 따라 DI를 통해 교체 가능하다.
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	// final이므로 변경이 불가능하다.
	// OxmSqlService와 OxmSqlReader는 강하게 결합돼서 하나의 빈으로 등록되고 한번에 설정할 수 있다.
	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
	
	// 내부 오브젝트의 프로퍼티를 전달해주는 코드
	// OxmSqlService의 공개된 프로퍼티를 통해 DI 받은 것을 그대로 멤버 클래스의 오브젝트에 전달한다.
	// 이 setter들은 단일 빈 설정구조를 위한 창구 역할을 할 뿐이다.
	public void setUnmarshaller(Unmarshaller unmarshaller){
		this.oxmSqlReader.setUnmarshaller(unmarshaller);
	}
	
	/*public void setSqlmapFile(String sqlmapFile){
		this.oxmSqlReader.setSqlmapFile(sqlmapFile);
	}*/
	
	// XML 파일을 클래스패스가 아닌 특정 폴더나 웹상에서 가져올 수 있게 수정!
	public void setSqlmap(Resource sqlmap){
		this.oxmSqlReader.setSqlmap(sqlmap);
	}
	
	// privaet 멤버 클래스로 정의한다. 톱레벨 클래스인 OxmSqlService만이 사용할 수 있다.
	// 두개의 클래스를 강하게 결합하고 더이상의 확장이나 변경을 제한하는 이유? -> OXM을 이용하는 서비스 구조로 최적화하기 위해!
	private class OxmSqlReader implements SqlReader{
		private final static String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
		
		private Unmarshaller unmarshaller;
		//private String sqlmapFile = DEFAULT_SQLMAP_FILE;
		
		// 디폴트 파일은 기존과 같지만 이제는 Resource 구현 클래스인 ClassPathResource를 이용한다.
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
				
				// 리소스의 조율에 상관없이 스트림으로 가져올 수 있다.
				Source source = new StreamSource(sqlmap.getInputStream());
				
				// OxmSqlService를 통해 전달받은 OXM 인터페이스 구현 오브젝트를 가지고 언마샬링 작업 수행
				Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);
				
				for(SqlType sql : sqlmap.getSql()){
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
			}catch(IOException e){
				throw new IllegalArgumentException(this.sqlmap.getFilename() + "을 가져올 수가 없다!", e);
			}
			
		}
	}
	
	
	
	
	@PostConstruct
	public void loadSql(){
		// OxmSqlService의 프로퍼티를 통해서 초기화된 SqlReader와 SqlRegistry를 실제 작업을 위임할 대상인 baseSqlService에 주입한다.
		this.baseSqlService.setSqlReader(this.oxmSqlReader);
		this.baseSqlService.setSqlRegistry(this.sqlRegistry);
		
		this.baseSqlService.loadSql();
		
		//this.oxmSqlReader.read(this.sqlRegistry);
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		// SQL을 찾아오는 작업도 baseSqlService에 위임한다.
		return this.baseSqlService.getSql(key);
		
		/*try{
			return this.sqlRegistry.findSql(key);
		}catch(SqlNotFoundException e){
			throw new SqlRetrievalFailureException(e);
		}*/
	}

}
