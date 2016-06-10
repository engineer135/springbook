package springbook.user.domain;

public class User {
	
	// UserService가 일일이 레벨 업그레이드 시에 User 의 어떤 필드를 수정한다는 로직보다는, User에게 레벨 업그레이드를 해야 하니 정보를 변경하라고 요청하는것이 낫다.
	public void upgradeLevel(){
		Level nextLevel = this.level.nextLevel();
		if(nextLevel == null){
			throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
		}else{
			this.level = nextLevel;
		}
	}
	
	Level level;//사용자 레벨
	int login;//로그인 횟수
	int recommend;//추천수
	
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
	
	// 레벨, 로그인 회수, 추천수 관련 생성자 추가
	public User(String id, String name, String password, Level level, int login, int recommend){
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}
	
	public User(){
		// 클래스에 생성자를 명시적으로 추가했을 때는 파라미터 없는 디폴트 생성자도 함께 정의해줘야 한다.
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
