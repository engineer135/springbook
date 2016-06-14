package springbook.learningtest.factorybean;

/**
 * @author Engineer135
 *
 * 생성자를 제공하지 않는 클래스
 * 생성자를 private으로 만들었다는 것은 스태틱 메소드를 통해 오브젝트가 만들어져야 하는 중요한 이유가 있기 때문이므로,
 * 이를 무시하고 오브젝트를 강제로 생성하면 위험하다.
 * 그러므로 팩토리빈을 사용해서 오브젝트를 생성하자.
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
