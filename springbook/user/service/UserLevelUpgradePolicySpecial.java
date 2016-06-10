package springbook.user.service;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserLevelUpgradePolicySpecial implements UserLevelUpgradePolicy {
	public static final int MIN_LOGCOUNT_FOR_SILVER = 30;
	public static final int MIN_RECCOMEND_FOR_GOLD = 10;
	
	UserDao userDao;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch(currentLevel){
			case BASIC : return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER : return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD : return false;
			default : throw new IllegalArgumentException("Unknown Leve: "+ currentLevel);
		}
	}

	@Override
	public void upgradeLevel(User user) {
		user.upgradeLevel(); // 다음 레벨 지정을 user에서 하도록 변경
		userDao.update(user);
	}
}
