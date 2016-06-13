package springbook.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

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

	//사용자 레벨 업그레이드 메소드
	public void upgradeLevels(){
		List<User> users = userDao.getAll();
		for(User user : users){
			if(canUpgradeLevel(user)){
				upgradeLevel(user);
			}
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
