package springbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;

import springbook.user.dao.UserDao;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceTest.TestUserService;

@Configuration
public class TestAppContext {
	@Bean
	public UserService testUserService(){
		// UserService 생성 안하고 바로 하위 클래스 생성해서 쓰기 때문에... static으로 되어있어야 한다. http://www.devblog.kr/r/8y0gFPAvJ2j8MWIVVXucyP9uYvQegfSVbY5XNDkHt
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
