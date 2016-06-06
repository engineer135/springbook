package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Engineer135
 *
 * SimpleConnectionMaker �� Ŭ������ �и��߾ ������ ������ �ִ�.
 * 1. this.simpleConnectionMaker.makeNewConnection(); ���⼭ makeNewConnection() �޼ҵ���� �ٲ� ��� ��� ã�Ƽ� ��������� �Ѵٴ� ��
 * 2. DB Ŀ�ؼ��� �����ϴ� Ŭ������ � ������ UserDao�� ��ü������ �˰� �־�� �Ѵٴ� ��!
 * 
 * ���� interface�� ���� �ذ��Ѵ�.
 * �������̽��� ���ǵ� �޼ҵ带 ����ϹǷ� Ŭ������ �ٲ�ٰ� �ص� �޼ҵ� �̸��� ����� ������ ��������.
 * 
 */
public interface ConnectionMaker {
	public Connection makeConnection() throws ClassNotFoundException, SQLException;
}
