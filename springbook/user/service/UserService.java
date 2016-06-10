package springbook.user.service;

import java.util.List;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 * ����Ͻ� ������ ����ϴ� ���� Ŭ����
 */
public class UserService {
	UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	//����� ���� ���׷��̵� �޼ҵ�
	public void upgradeLevels(){
		List<User> users = userDao.getAll();
		for(User user : users){
			Boolean changed = null; //���� ��ȭ�� �ִ��� Ȯ���ϴ� �÷���
			if(user.getLevel() == Level.BASIC && user.getLogin() >= 50){//Basic ���� ���׷��̵� �۾�
				user.setLevel(Level.SILVER);
				changed = true;
			}else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30){//Silver ���� ���׷��̵� �۾�
				user.setLevel(Level.GOLD);
				changed = true;
			}else if(user.getLevel() == Level.GOLD){//Gold ������ ���� ����
				changed = false;
			}else{
				changed = false;
			}
			
			if(changed){//���� ������ �ִ� ��쿡�� update() ȣ��
				userDao.update(user);
			}
		}
	}
}
