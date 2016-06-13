package springbook.user.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 * 비즈니스 로직을 담당하는 서비스 클래스
 */
public class UserService {
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	UserLevelUpgradePolicy userLevelUpgradePolicy;
	
	public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
		this.userLevelUpgradePolicy = userLevelUpgradePolicy;
	}
	
	// 트랜잭션 동기화 적용을 위해 DataSource를 DI 받도록 한다.
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	//사용자 레벨 업그레이드 메소드
	public void upgradeLevels() throws Exception{
		TransactionSynchronizationManager.initSynchronization();//트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
		Connection c = DataSourceUtils.getConnection(dataSource); //DB 커넥션을 생성하고 트랜잭션을 시작한다. 이후의 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
		c.setAutoCommit(false);
		
		try{
			List<User> users = userDao.getAll();
			for(User user : users){
				if(canUpgradeLevel(user)){
					upgradeLevel(user);
				}
			}
			
			c.commit(); //정상적으로 작업을 마치면 트랜잭션 커밋
		}catch(Exception e){
			c.rollback(); //예외가 발생하면 롤백
			throw e;
		}finally{
			DataSourceUtils.releaseConnection(c, dataSource);//스프링 유틸리티 메소드를 이용해 DB 커넥션을 안전하게 닫는다.
			
			// 동기화 작업 종료 및 정리
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}
		
		
	}
	
	private boolean canUpgradeLevel(User user) {
		//return userLevelUpgradePolicy.canUpgradeLevel(user);
		
		Level currentLevel = user.getLevel();
		switch(currentLevel){
			case BASIC : return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER : return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD : return false;
			default : throw new IllegalArgumentException("Unknown Leve: "+ currentLevel);
		}
	}
	
	protected void upgradeLevel(User user){// 상속을 통해 트랜젝션 테스트를 진행할 예정. protected 접근권한으로 수정한다.
		//userLevelUpgradePolicy.upgradeLevel(user);
		
		/*if(user.getLevel() == Level.BASIC){
			user.setLevel(Level.SILVER);
		}else if(user.getLevel() == Level.SILVER){
			user.setLevel(Level.GOLD);
		}*/
		user.upgradeLevel(); // 다음 레벨 지정을 user에서 하도록 변경
		userDao.update(user);
	}

	public void add(User user) {
		if(user.getLevel() == null){
			user.setLevel(Level.BASIC); 
		}
		userDao.add(user);
	}
}
