package springbook.user.domain;

/**
 * @author Engineer135
 *
 * ����� ������ ������ enum 
 */
public enum Level {
	// �̴� ���� DB�� ������ ���� �Բ� ���� �ܰ��� ���� ������ �߰��Ѵ�.
	// ���� �ܰ��� ������ �������� ������ if ���ǽ��� ���� ����Ͻ� ������ ��Ƶ� �ʿ䰡 ������!
	//BASIC(1, SILVER), SILVER(2, GOLD), GOLD(3, null); //������ �̴� ������Ʈ ����
	GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER); //������ �̴� ������Ʈ ����
	
	private final int value;
	private final Level next;
	
	Level(int value, Level next){//DB�� ������ ���� �־��� �����ڸ� �����д�.
		this.value = value;
		this.next = next;
	}
	
	public int intValue(){//���� �������� �޼ҵ�
		return value;
	}
	
	public Level nextLevel(){
		return this.next;
	}
	
	public static Level valueOf(int value){//�����κ��� Level Ÿ�� ������Ʈ�� ���������� ���� ����ƽ �޼ҵ�
		switch(value){
			case 1 : return BASIC;
			case 2 : return SILVER;
			case 3 : return GOLD;
			default : throw new AssertionError("Unknown value : "+ value);
		}
	}
}
