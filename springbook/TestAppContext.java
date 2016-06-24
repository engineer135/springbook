package springbook;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;

import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceTest.TestUserService;

//���� ���ؽ�Ʈ�� ��ø Ŭ������ �����ϸ鼭 �̻��
//@Configuration

// Profile ����
//@Profile("test")
public class TestAppContext {
	@Bean
	public UserService testUserService(){
		// UserService ���� ���ϰ� �ٷ� ���� Ŭ���� �����ؼ� ���� ������... static���� �Ǿ��־�� �Ѵ�. http://www.devblog.kr/r/8y0gFPAvJ2j8MWIVVXucyP9uYvQegfSVbY5XNDkHt
		TestUserService testService = new TestUserService();
		//testService.setUserDao(this.userDao);
		//testService.setMailSender(this.mailSender());
		return testService;
	}
	
	@Bean
	public MailSender mailSender(){
		return new DummyMailSender();
	}
}
