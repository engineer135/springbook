package springbook.user.dao;

import java.util.List;

import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 * 기술에 독립적인 UserDao를 만들기 위해 인터페이스 생성 ex) JDBC, JPA, 하이버네이트 구현할때마다 UserDaoJdbc, UserDaoJpa, UserDaoHibernate 라고 구현해서 사용하기 위해
 * 
 */
public interface UserDao {
	void add(User user);
	User get(String id);
	List<User> getAll();
	void deleteAll();
	int getCount();
}
