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
 * 관심사를 분리시켜 sqlReader, sqlService, sqlRegistry 세개의 인터페이스를 구현하도록 만든다.
 */
public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {
	// DI 받기 위해 인터페이스 타입의 프로퍼티로 선언
	private SqlReader sqlReader;
	private SqlRegistry sqlRegistry;
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	// 읽어온 sql을 저장해둘 맵
	private Map<String, String> sqlMap = new HashMap<String, String>();// sqlMap은 sqlRegistry 구현의 일부가 된다. 따라서 외부에서 직접 접근 불가!
	
	// 파일을 외부에서 지정할 수 있도록
	private String sqlmapFile;
	
	// sqlMapFile은 sqlReader 구현의 일부가 된다. 따라서 SqlReader 구현 메소드를 통하지 않고는 접근하면 안돼요!
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

	// 스프링이 오브젝트를 만드는 시점에서 SQL을 읽어오도록 생성자를 이용하자
	// 따로 메소드로 분리시킨다!
	/*public XmlSqlService(){
		
	}*/
	
	public String getSql(String key) throws SqlRetrievalFailureException {
		try{
			return this.sqlRegistry.findSql(key);
		}catch(SqlNotFoundException e){
			throw new SqlRetrievalFailureException(e);
		}
	}
	
	// 생성자에서 예외가 발생할 수도 있는 복잡한 초기화 작업을 하는것은 좋지 않으므로 따로 뺀다.
	@PostConstruct // 이 애노테이션이 붙으면 스프링에서 빈 오브젝트를 생성하고 di 작업을 마친 뒤에.. 프로퍼티까지 모두 준비된 후에!! 이 메소드를 자동으로 실행해준다! 대박!!
	// 애플리케이션 컨텍스트에는 context:annotation-config를 추가해야한다!
	public void loadSql(){
		this.sqlReader.read(this.sqlRegistry);
	}
	
	
	
	
	
	
	
	
	
	

	@Override
	// HashMap이라는 저장소를 사용하는 구체적인 구현 방법에서 독립될 수 있도록 인터페이스의 메소드로 접근하게 해준다.
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if(sql == null){
			throw new SqlRetrievalFailureException(key + "에 대한 sql을 찾을 수 없어요! sqlRegistry 구현체입니다.");
		}else{
			return sql;
		}
	}

	@Override
	// loadSql()에 있던 코드를 SqlReader 메소드로 가져온다. 초기화를 위해 무엇을 할것인가와 SQL을 어떻게 읽는지를 분리!!!
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
