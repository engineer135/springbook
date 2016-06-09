package springbook.user.dao;

import java.util.List;

import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 * ����� �������� UserDao�� ����� ���� �������̽� ���� ex) JDBC, JPA, ���̹�����Ʈ �����Ҷ����� UserDaoJdbc, UserDaoJpa, UserDaoHibernate ��� �����ؼ� ����ϱ� ����
 * 
 */
public interface UserDao {
	void add(User user);
	User get(String id);
	List<User> getAll();
	void deleteAll();
	int getCount();
}
