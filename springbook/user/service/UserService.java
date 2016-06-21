package springbook.user.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 * 비즈니스 로직을 담당하는 서비스 클래스 -> AOP 적용을 위해 인터페이스로 변경
 */
@Transactional
public interface UserService {
	void add(User user);
	
	// Transactional 애노테이션은 적용 순서 
	// 메소드 -> 클래스 -> 인터페이스 메소드 -> 인터페이스
	@Transactional(readOnly=true)
	User get(String id);
	
	@Transactional(readOnly=true)
	List<User> getAll();
	
	void deleteAll();
	void update(User user);
	
	void upgradeLevels();
}
