package springbook.user.service;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

/**
 * @author Engineer135
 *
 * Ŭ���� ���Ͱ� ���Ե� ����Ʈ��
 */
public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut {
	public void setMappedClassName(String mappedClassName){
		this.setClassFilter(new SimpleClassFilter(mappedClassName)); //��� Ŭ������ �� ����ϴ� ����Ʈ Ŭ���� ���͸� ������Ƽ�� ���� Ŭ���� �̸��� �̿��ؼ� ���͸� ����� �����
	}
	
	static class SimpleClassFilter implements ClassFilter {
		String mappedName;
		
		private SimpleClassFilter(String mappedName){
			this.mappedName = mappedName;
		}
		
		public boolean matches(Class<?> clazz){
			// ���ϵ�ī��(*)�� �� ���ڿ� �񱳸� �����ϴ� �������� ��ƿ��Ƽ �޼ҵ�.
			// *name, name*, *name* ������ ����� ��� ����
			return PatternMatchUtils.simpleMatch(mappedName, clazz.getSimpleName());
		}
	}
}
