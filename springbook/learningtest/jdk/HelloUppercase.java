package springbook.learningtest.jdk;

public class HelloUppercase implements Hello {

	// ������ Ÿ�� ������Ʈ. ���⼭�� Ÿ�� Ŭ������ ������Ʈ�� ���� ������ �ٸ� ���Ͻø� �߰��� ���� �����Ƿ� �������̽��� �����Ѵ�.
	Hello hello;
	
	public HelloUppercase(Hello hello){
		this.hello = hello;
	}
	
	public String sayHello(String name) {
		return hello.sayHello(name).toUpperCase();//���Ӱ� �ΰ���� ����
	}

	public String sayHi(String name) {
		return hello.sayHi(name).toUpperCase();
	}

	public String sayThankYou(String name) {
		return hello.sayThankYou(name).toUpperCase();
	}

}
