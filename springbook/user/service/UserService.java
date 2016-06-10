package springbook.user.service;

import java.util.List;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 * 비즈니스 로직을 담당하는 서비스 클래스
 */
public class UserService {
	UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	//사용자 레벨 업그레이드 메소드
	public void upgradeLevels(){
		List<User> users = userDao.getAll();
		for(User user : users){
			Boolean changed = null; //레벨 변화가 있는지 확인하는 플래그
			if(user.getLevel() == Level.BASIC && user.getLogin() >= 50){//Basic 레벨 업그레이드 작업
				user.setLevel(Level.SILVER);
				changed = true;
			}else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30){//Silver 레벨 업그레이드 작업
				user.setLevel(Level.GOLD);
				changed = true;
			}else if(user.getLevel() == Level.GOLD){//Gold 레벨은 변경 없음
				changed = false;
			}else{
				changed = false;
			}
			
			if(changed){//레벨 변경이 있는 경우에만 update() 호출
				userDao.update(user);
			}
		}
	}
}
