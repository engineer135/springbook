package springbook.user.dao;

import java.util.ArrayList;
import java.util.List;

import springbook.user.domain.User;

public class MockUserDao implements UserDao {
	
	//레벨 업그레이드 후보 User 오브젝트 목록
	private List<User> users;
	
	//업그레이드 대상 오브젝트를 저장해둘 목록
	private List<User> updated = new ArrayList();

	public MockUserDao(List<User> users){
		this.users = users;
	}
	
	public List<User> getUpdated() {
		return updated;
	}

	// 고립된 단위 테스트를 upgradeLevels()에 적용중.
	// upgradeLevels()에서 사용하지 않는 메소드는 익셉션 처리를 해놓는다.
	public void add(User user) {
		throw new UnsupportedOperationException();
	}

	public User get(String id) {
		throw new UnsupportedOperationException();
	}

	// 스텁 기능 제공
	public List<User> getAll() {
		return this.users;
	}

	public void deleteAll() {
		throw new UnsupportedOperationException();
	}

	public int getCount() {
		throw new UnsupportedOperationException();
	}

	// 목 오브젝트 기능 제공
	public void update(User user) {
		updated.add(user);
	}

}
