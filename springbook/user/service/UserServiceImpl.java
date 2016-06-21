package springbook.user.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserServiceImpl implements UserService {
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	//UserLevelUpgradePolicy userLevelUpgradePolicy;
	
	/*public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
		this.userLevelUpgradePolicy = userLevelUpgradePolicy;
	}*/
	
	// Ʈ����� ����ȭ ������ ���� DataSource�� DI �޵��� �Ѵ�.
	/*private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}*/
	
	// Ʈ����� �Ŵ����� ������ �и��ؼ� DI �޵��� ó��
	//private PlatformTransactionManager transactionManager;
	
	// ������Ƽ �̸��� ���ʸ� ���� transactionManager��� ����� ���� ���ϴ�.
	/*public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}*/

	//����� ���� ���׷��̵� �޼ҵ�
	public void upgradeLevels() {
		//TransactionSynchronizationManager.initSynchronization();//Ʈ����� ����ȭ �����ڸ� �̿��� ����ȭ �۾��� �ʱ�ȭ�Ѵ�.
		//Connection c = DataSourceUtils.getConnection(dataSource); //DB Ŀ�ؼ��� �����ϰ� Ʈ������� �����Ѵ�. ������ DAO �۾��� ��� ���⼭ ������ Ʈ����� �ȿ��� ����ȴ�.
		//c.setAutoCommit(false);
		
		// �������� �����ϴ� Ʈ����� �߻�ȭ ��� ����(JDBC, JTA, ���̹�����Ʈ �� �̰� �ϳ��� ������ ����!)
		//PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);//JDBC Ʈ����� �߻� ������Ʈ ����
		// ���� ��ü �������� �ʰ� DI �ޱ� ���� �ּ� ó��!
		
		//PlatformTransactionManager transactionManager = new JtaTransactionManager(); JTA�� �̷������� ���.. ���̹�����Ʈ�� ��������
		// ������ � Ʈ����� �Ŵ��� ���� Ŭ������ ������� UserService �ڵ尡 �˰� �ִ� ���� DI ��Ģ�� ����ȴ�!
		// �׷��Ƿ�, ������ ������ ��� �� UserService�� DI ������� ����ϰ� �ؾ� �Ѵ�.
		// ���⼭ �߿��� ����, � Ŭ������ ������ ������ ����Ҷ� 
		// �̱������� ������� ���� �����忡�� ���ÿ� ����ص� ��������!? �ϴ� ���̴�.
		// ���¸� ���� �ְ�, ��Ƽ������ ȯ�濡�� �������� ���� Ŭ������ ������ ����ϸ� �ɰ��� ������ �߻�!
		
		// Ʈ����� �ڵ�� ���� �и��ϱ� ���� �ּ� ó��
		//TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); //DI ���� Ʈ����� �Ŵ����� �����ؼ� ���. ��Ƽ������ ȯ�濡���� ����.
		
		try{
			List<User> users = userDao.getAll();
			for(User user : users){
				if(canUpgradeLevel(user)){
					upgradeLevel(user);
				}
			}
			
			//c.commit(); //���������� �۾��� ��ġ�� Ʈ����� Ŀ��
			
			//this.transactionManager.commit(status);
		}catch(Exception e){
			//c.rollback(); //���ܰ� �߻��ϸ� �ѹ�
			
			//this.transactionManager.rollback(status);
			throw e;
		}
		
		/*finally{
			DataSourceUtils.releaseConnection(c, dataSource);//������ ��ƿ��Ƽ �޼ҵ带 �̿��� DB Ŀ�ؼ��� �����ϰ� �ݴ´�.
			
			// ����ȭ �۾� ���� �� ����
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
	
	protected void upgradeLevel(User user){// ����� ���� Ʈ������ �׽�Ʈ�� ������ ����. protected ���ٱ������� �����Ѵ�.
		//userLevelUpgradePolicy.upgradeLevel(user);
		
		/*if(user.getLevel() == Level.BASIC){
			user.setLevel(Level.SILVER);
		}else if(user.getLevel() == Level.SILVER){
			user.setLevel(Level.GOLD);
		}*/
		user.upgradeLevel(); // ���� ���� ������ user���� �ϵ��� ����
		userDao.update(user);
		sendUpgradeEMail(user);//�̸��� �߼� �߰�
	}

	public void add(User user) {
		if(user.getLevel() == null){
			user.setLevel(Level.BASIC); 
		}
		userDao.add(user);
	}
	
	// DI �ޱ� ���� �߰�
	private MailSender mailSender;
	
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	// ���� �߼�
	private void sendUpgradeEMail(User user){
		
		// JavaMail�� Ȯ���̳� ������ �Ұ����ϵ��� ������� ���� �Ǹ� ���� API �� �ϳ�!
		// ���� ���� ���ϼ��� ����� �ʴ´ٸ� ��� �׽�Ʈ�� �� ���� ����.
		// �׷��� Spring�� �����ϴ� �߻�ȭ ����� ����Ѵ�.
		/**
		Properties props = new Properties();
		props.put("mail.smtp.host", "mail.ksug.org");
		Session s = Session.getInstance(props, null);
		
		MimeMessage message = new MimeMessage(s);
		try{
			message.setFrom(new InternetAddress("everafterk@naver.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
			message.setSubject("upgrade �ȳ�");
			message.setText("����ڴ��� ����� "+ user.getLevel().name() + " �� ���׷��̵�Ǿ����ϴ�!");
			Transport.send(message);
		}catch(AddressException e){
			throw new RuntimeException(e);
		}catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		*/
		
		// DI �ޱ� ���� �ּ� ó��
		//JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); //MailSender ���� Ŭ������ ������Ʈ ����
		//mailSender.setHost("mail.server.com");
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("everafterk@naver.com");
		mailMessage.setSubject("Upgrade �ȳ�");
		mailMessage.setText("����ڴ��� ����� "+ user.getLevel().name() + " �� ���׷��̵�Ǿ����ϴ�!");
		
		this.mailSender.send(mailMessage);
	}

	@Override
	public User get(String id) {
		userDao.update(userDao.get(id));
		return userDao.get(id);
	}

	@Override
	public List<User> getAll() {
		return userDao.getAll();
	}

	@Override
	public void deleteAll() {
		userDao.deleteAll();
	}

	@Override
	public void update(User user) {
		if(user.getId().equals("green")){
			throw new RuntimeException();
		}else{
			userDao.update(user);
		}
	}
}
