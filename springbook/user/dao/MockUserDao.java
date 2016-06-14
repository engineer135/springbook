package springbook.user.dao;

import java.util.ArrayList;
import java.util.List;

import springbook.user.domain.User;

public class MockUserDao implements UserDao {
	
	//���� ���׷��̵� �ĺ� User ������Ʈ ���
	private List<User> users;
	
	//���׷��̵� ��� ������Ʈ�� �����ص� ���
	private List<User> updated = new ArrayList();

	public MockUserDao(List<User> users){
		this.users = users;
	}
	
	public List<User> getUpdated() {
		return updated;
	}

	// ���� ���� �׽�Ʈ�� upgradeLevels()�� ������.
	// upgradeLevels()���� ������� �ʴ� �޼ҵ�� �ͼ��� ó���� �س��´�.
	public void add(User user) {
		throw new UnsupportedOperationException();
	}

	public User get(String id) {
		throw new UnsupportedOperationException();
	}

	// ���� ��� ����
	public List<User> getAll() {
		return this.users;
	}

	public void deleteAll() {
		throw new UnsupportedOperationException();
	}

	public int getCount() {
		throw new UnsupportedOperationException();
	}

	// �� ������Ʈ ��� ����
	public void update(User user) {
		updated.add(user);
	}

}
