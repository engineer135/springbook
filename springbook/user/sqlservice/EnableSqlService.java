package springbook.user.sqlservice;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.context.annotation.Import;

import springbook.SqlServiceContext;

/**
 * @author Engineer135
 *
 * @Import�� ��Ÿ �ֳ����̼����� ���� �ֳ����̼� ����
 */
@Import(SqlServiceContext.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableSqlService {

}
