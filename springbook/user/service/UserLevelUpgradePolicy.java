package springbook.user.service;

import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 * ���� ���׷��̵� ��å�� �ٸ��� ������ �ʿ䰡 ������ ���� ���� �������̽� ex) ���� �̺�Ʈ ��...
 * �� �������̽��� ������ Ŭ������ �ΰ� �������. normal, special...
 * ���ø����̼� ���ؽ�Ʈ�� �� �������� ���� Ŭ������ �ٲٱ⸸ �ϸ� ���� ������
 * ������... UserServiceTest�� �ҽ� ������ ����� �Ѵٴ°�... ��� import�ϴ� �κ�... �̰� ���� ���ص� �ǰ� �ϴ� �� �����غ���...
 */
public interface UserLevelUpgradePolicy {
	boolean canUpgradeLevel(User user);
	void upgradeLevel(User user);
}
