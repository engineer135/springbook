package springbook.user.domain;

public class User {
	
	// UserService�� ������ ���� ���׷��̵� �ÿ� User �� � �ʵ带 �����Ѵٴ� �������ٴ�, User���� ���� ���׷��̵带 �ؾ� �ϴ� ������ �����϶�� ��û�ϴ°��� ����.
	public void upgradeLevel(){
		Level nextLevel = this.level.nextLevel();
		if(nextLevel == null){
			throw new IllegalStateException(this.level + "�� ���׷��̵尡 �Ұ����մϴ�.");
		}else{
			this.level = nextLevel;
		}
	}
	
	Level level;//����� ����
	int login;//�α��� Ƚ��
	int recommend;//��õ��
	
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public User(String id, String name, String password){
		this.id = id;
		this.name = name;
		this.password = password;
	}
	
	// ����, �α��� ȸ��, ��õ�� ���� ������ �߰�
	public User(String id, String name, String password, Level level, int login, int recommend){
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}
	
	public User(){
		// Ŭ������ �����ڸ� ��������� �߰����� ���� �Ķ���� ���� ����Ʈ �����ڵ� �Բ� ��������� �Ѵ�.
	}
	
	String id;
	String name;
	String password;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
