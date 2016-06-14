package springbook.learningtest.factorybean;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message>{
	String text;
	
	// ������Ʈ�� ������ �� �ʿ��� ������ ���丮 ���� ������Ƽ�� �����ؼ� ��� DI ���� �� �ְ� �Ѵ�.
	// ���Ե� ������ ������Ʈ ���� �߿� ���ȴ�.
	public void setText(String text) {
		this.text = text;
	}

	// ���� ������ ���� ������Ʈ�� ���� �����Ѵ�.
	// �ڵ带 �̿��ϱ� ������ ������ ����� ������Ʈ ������ �ʱ�ȭ �۾��� �����ϴ�.
	public Message getObject() throws Exception {
		return Message.newMessage(this.text);
	}

	public Class<?> getObjectType() {
		return Message.class;
	}

	// getObject() �޼ҵ尡 �����ִ� ������Ʈ�� �̱��������� �˷��ش�. 
	// �� ���丮 ���� �Ź� ��û�� ������ ���ο� ������Ʈ�� ����Ƿ� false�� �����Ѵ�.
	// �̰��� ���丮 ���� ���۹�Ŀ� ���� �����̰� ������� �� ������Ʈ�� �̱������� �������� �������� �� �ִ�.
	public boolean isSingleton() {
		return false;
	}

}
