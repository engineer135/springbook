package springbook.user.domain;

/**
 * @author Engineer135
 *
 * ����� ������ ������ enum 
 */
public enum Level {
	BASIC(1), SILVER(2), GOLD(3); //������ �̴� ������Ʈ ����
	
	private final int value;
	
	Level(int value){//DB�� ������ ���� �־��� �����ڸ� �����д�.
		this.value = value;
	}
	
	public int intValue(){//���� �������� �޼ҵ�
		return value;
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
