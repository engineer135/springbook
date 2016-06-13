package springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummyMailSender implements MailSender {
	private String host = "";
	
	public void setHost(String host) {
		this.host = host;
	}

	public void send(SimpleMailMessage arg0) throws MailException {
		System.out.println("皋老 惯价~");
	}

	public void send(SimpleMailMessage... arg0) throws MailException {
		System.out.println("皋老 惯价~");
	}
}
