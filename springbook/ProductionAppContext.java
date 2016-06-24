package springbook;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author Engineer135
 *
 * �ȯ�濡���� �ʿ��� ���� ���� �� ���� Ŭ����
 */
@Configuration
public class ProductionAppContext {
	// ��� ���� ������ �����ϴ� MailSender ���� �������� �Ѵ�.
	@Bean
	public MailSender mailSender(){
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("mail.mycompany.com");
		return mailSender;
	}
}
