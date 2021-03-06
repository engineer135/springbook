package springbook;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author Engineer135
 *
 * 운영환경에서만 필요한 빈을 담은 빈 설정 클래스
 */
// 메인 컨텍스트의 중첩 클래스로 변경하면서 미사용
//@Configuration
//@Profile("production")
public class ProductionAppContext {
	// 운영용 메일 서버를 지원하는 MailSender 빈을 등록해줘야 한다.
	@Bean
	public MailSender mailSender(){
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("mail.mycompany.com");
		return mailSender;
	}
}
