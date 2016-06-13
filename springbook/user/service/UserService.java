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
 * ����Ͻ� ������ ����ϴ� ���� Ŭ����
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
	
	// Ʈ����� ����ȭ ������ ���� DataSource�� DI �޵��� �Ѵ�.
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	//����� ���� ���׷��̵� �޼ҵ�
	public void upgradeLevels() throws Exception{
		TransactionSynchronizationManager.initSynchronization();//Ʈ����� ����ȭ �����ڸ� �̿��� ����ȭ �۾��� �ʱ�ȭ�Ѵ�.
		Connection c = DataSourceUtils.getConnection(dataSource); //DB Ŀ�ؼ��� �����ϰ� Ʈ������� �����Ѵ�. ������ DAO �۾��� ��� ���⼭ ������ Ʈ����� �ȿ��� ����ȴ�.
		c.setAutoCommit(false);
		
		try{
			List<User> users = userDao.getAll();
			for(User user : users){
				if(canUpgradeLevel(user)){
					upgradeLevel(user);
				}
			}
			
			c.commit(); //���������� �۾��� ��ġ�� Ʈ����� Ŀ��
		}catch(Exception e){
			c.rollback(); //���ܰ� �߻��ϸ� �ѹ�
			throw e;
		}finally{
			DataSourceUtils.releaseConnection(c, dataSource);//������ ��ƿ��Ƽ �޼ҵ带 �̿��� DB Ŀ�ؼ��� �����ϰ� �ݴ´�.
			
			// ����ȭ �۾� ���� �� ����
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
	
	protected void upgradeLevel(User user){// ����� ���� Ʈ������ �׽�Ʈ�� ������ ����. protected ���ٱ������� �����Ѵ�.
		//userLevelUpgradePolicy.upgradeLevel(user);
		
		/*if(user.getLevel() == Level.BASIC){
			user.setLevel(Level.SILVER);
		}else if(user.getLevel() == Level.SILVER){
			user.setLevel(Level.GOLD);
		}*/
		user.upgradeLevel(); // ���� ���� ������ user���� �ϵ��� ����
		userDao.update(user);
	}

	public void add(User user) {
		if(user.getLevel() == null){
			user.setLevel(Level.BASIC); 
		}
		userDao.add(user);
	}
}
