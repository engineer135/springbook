package springbook.user.service;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

/**
 * @author Engineer135
 *
 * 비즈니스 로직을 담당하는 서비스 클래스
 */
public class UserService {
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	UserLevelUpgradePolicy userLevelUpgradePolicy;
	
	public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
		this.userLevelUpgradePolicy = userLevelUpgradePolicy;
	}
	
	// 트랜잭션 동기화 적용을 위해 DataSource를 DI 받도록 한다.
	/*private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}*/
	
	// 트랜잭션 매니저를 빈으로 분리해서 DI 받도록 처리
	private PlatformTransactionManager transactionManager;
	
	// 프로퍼티 이름은 관례를 따라 transactionManager라고 만드는 것이 편리하다.
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	//사용자 레벨 업그레이드 메소드
	public void upgradeLevels() throws Exception{
		//TransactionSynchronizationManager.initSynchronization();//트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
		//Connection c = DataSourceUtils.getConnection(dataSource); //DB 커넥션을 생성하고 트랜잭션을 시작한다. 이후의 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
		//c.setAutoCommit(false);
		
		// 스프링이 제공하는 트랜잭션 추상화 기술 적용(JDBC, JTA, 하이버네이트 등등에 이거 하나로 적용이 가능!)
		//PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);//JDBC 트랜잭션 추상 오브젝트 생성
		// 직접 객체 생성하지 않고 DI 받기 위해 주석 처리!
		
		//PlatformTransactionManager transactionManager = new JtaTransactionManager(); JTA는 이런식으로 사용.. 하이버네이트도 마찬가지
		// 하지만 어떤 트랜잭션 매니저 구현 클래스를 사용할지 UserService 코드가 알고 있는 것은 DI 원칙에 위배된다!
		// 그러므로, 스프링 빈으로 등록 후 UserService가 DI 방식으로 사용하게 해야 한다.
		// 여기서 중요한 것은, 어떤 클래스든 스프링 빈으로 등록할때 
		// 싱글톤으로 만들어져 여러 스레드에서 동시에 사용해도 괜찮은가!? 하는 것이다.
		// 상태를 갖고 있고, 멀티스레드 환경에서 안전하지 않은 클래스를 빈으로 등록하면 심각한 문제가 발생!
		
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); //DI 받은 트랜잭션 매니저를 공유해서 사용. 멀티스레드 환경에서도 안전.
		
		try{
			List<User> users = userDao.getAll();
			for(User user : users){
				if(canUpgradeLevel(user)){
					upgradeLevel(user);
				}
			}
			
			//c.commit(); //정상적으로 작업을 마치면 트랜잭션 커밋
			
			this.transactionManager.commit(status);
		}catch(Exception e){
			//c.rollback(); //예외가 발생하면 롤백
			
			this.transactionManager.rollback(status);
			throw e;
		}
		
		/*finally{
			DataSourceUtils.releaseConnection(c, dataSource);//스프링 유틸리티 메소드를 이용해 DB 커넥션을 안전하게 닫는다.
			
			// 동기화 작업 종료 및 정리
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}*/
		
		
	}
	
	private boolean canUpgradeLevel(User user) {
		//return userLevelUpgradePolicy.canUpgradeLevel(user);
		
		Level currentLevel = user.getLevel();
		switch(currentLevel){
			case BASIC : return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER : return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD : return false;
			default : throw new IllegalArgumentException("Unknown Leve: "+ currentLevel);
		}
	}
	
	protected void upgradeLevel(User user){// 상속을 통해 트랜젝션 테스트를 진행할 예정. protected 접근권한으로 수정한다.
		//userLevelUpgradePolicy.upgradeLevel(user);
		
		/*if(user.getLevel() == Level.BASIC){
			user.setLevel(Level.SILVER);
		}else if(user.getLevel() == Level.SILVER){
			user.setLevel(Level.GOLD);
		}*/
		user.upgradeLevel(); // 다음 레벨 지정을 user에서 하도록 변경
		userDao.update(user);
		sendUpgradeEMail(user);//이메일 발송 추가
	}

	public void add(User user) {
		if(user.getLevel() == null){
			user.setLevel(Level.BASIC); 
		}
		userDao.add(user);
	}
	
	// 메일 발송
	private void sendUpgradeEMail(User user){
		
		// JavaMail은 확장이나 지원이 불가능하도록 만들어진 가장 악명 높은 API 중 하나!
		// 따라서 실제 메일서버 운영하지 않는다면 어떻게 테스트를 할 수가 없음.
		// 그래서 Spring이 제공하는 추상화 기능을 사용한다.
		/**
		Properties props = new Properties();
		props.put("mail.smtp.host", "mail.ksug.org");
		Session s = Session.getInstance(props, null);
		
		MimeMessage message = new MimeMessage(s);
		try{
			message.setFrom(new InternetAddress("everafterk@naver.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
			message.setSubject("upgrade 안내");
			message.setText("사용자님의 등급이 "+ user.getLevel().name() + " 로 업그레이드되었습니다!");
			Transport.send(message);
		}catch(AddressException e){
			throw new RuntimeException(e);
		}catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		*/
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); //MailSender 구현 클래스의 오브젝트 생성
		mailSender.setHost("mail.server.com");
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("everafterk@naver.com");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 "+ user.getLevel().name() + " 로 업그레이드되었습니다!");
		
		mailSender.send(mailMessage);
	}
}
