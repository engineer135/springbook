package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Engineer135
 *
 * SimpleConnectionMaker 로 클래스를 분리했어도 여전히 문제가 있다.
 * 1. this.simpleConnectionMaker.makeNewConnection(); 여기서 makeNewConnection() 메소드명이 바뀔 경우 모두 찾아서 수정해줘야 한다는 것
 * 2. DB 커넥션을 제공하는 클래스가 어떤 것인지 UserDao가 구체적으로 알고 있어야 한다는 것!
 * 
 * 따라서 interface를 만들어서 해결한다.
 * 인터페이스에 정의된 메소드를 사용하므로 클래스가 바뀐다고 해도 메소드 이름이 변경될 걱정은 없어진다.
 * 
 */
public interface ConnectionMaker {
	public Connection makeConnection() throws ClassNotFoundException, SQLException;
}
