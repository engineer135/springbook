package springbook.user.domain;

/**
 * @author Engineer135
 *
 * 사용자 레벨을 저장할 enum 
 */
public enum Level {
	// 이늄 선언에 DB에 저장할 값과 함께 다음 단계의 레벨 정보도 추가한다.
	// 다음 단계의 레벨이 무엇이지 일일이 if 조건식을 만들어서 비즈니스 로직에 담아둘 필요가 없도록!
	//BASIC(1, SILVER), SILVER(2, GOLD), GOLD(3, null); //세개의 이늄 오브젝트 정의
	GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER); //세개의 이늄 오브젝트 정의
	
	private final int value;
	private final Level next;
	
	Level(int value, Level next){//DB에 저장할 값을 넣어줄 생성자를 만들어둔다.
		this.value = value;
		this.next = next;
	}
	
	public int intValue(){//값을 가져오는 메소드
		return value;
	}
	
	public Level nextLevel(){
		return this.next;
	}
	
	public static Level valueOf(int value){//값으로부터 Level 타입 오브젝트를 가져오도록 만든 스태틱 메소드
		switch(value){
			case 1 : return BASIC;
			case 2 : return SILVER;
			case 3 : return GOLD;
			default : throw new AssertionError("Unknown value : "+ value);
		}
	}
}
