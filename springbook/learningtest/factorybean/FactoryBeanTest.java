package springbook.learningtest.factorybean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration //�������� �̸��� �������� ������ Ŭ�����̸� + "-context.xml"�� ����Ʈ�� ���ȴ�.
public class FactoryBeanTest {
	@Autowired
	ApplicationContext context;
	
	@Test
	public void getMessageFromFactoryBean(){
		Object message = context.getBean("message");// "message" ��� "&message"�� ���ָ� ���丮 �� ��ü�� �����ش�.
		//assertThat(message, is(Message.class)); //Ÿ��Ȯ��
		assertThat(((Message)message).getText(), is("Factory Bean"));//������ ��� Ȯ��
	}
}
