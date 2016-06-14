package springbook.learningtest.factorybean;

/**
 * @author Engineer135
 *
 * �����ڸ� �������� �ʴ� Ŭ����
 * �����ڸ� private���� ������ٴ� ���� ����ƽ �޼ҵ带 ���� ������Ʈ�� ��������� �ϴ� �߿��� ������ �ֱ� �����̹Ƿ�,
 * �̸� �����ϰ� ������Ʈ�� ������ �����ϸ� �����ϴ�.
 * �׷��Ƿ� ���丮���� ����ؼ� ������Ʈ�� ��������.
 */
public class Message {
	String text;
	
	private Message(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public static Message newMessage(String text){
		return new Message(text);
	}
}
