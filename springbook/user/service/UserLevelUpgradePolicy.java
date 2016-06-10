package springbook.user.service;

import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 * 레벨 업그레이드 정책을 다르게 적용할 필요가 있을때 쓰기 위한 인터페이스 ex) 연말 이벤트 등...
 * 이 인터페이스를 구현한 클래스를 두개 만들었다. normal, special...
 * 어플리케이션 컨텍스트의 빈 설정에서 구현 클래스를 바꾸기만 하면 적용 오케이
 * 문제는... UserServiceTest는 소스 수정을 해줘야 한다는거... 상수 import하는 부분... 이거 수정 안해도 되게 하는 법 생각해보자...
 */
public interface UserLevelUpgradePolicy {
	boolean canUpgradeLevel(User user);
	void upgradeLevel(User user);
}
