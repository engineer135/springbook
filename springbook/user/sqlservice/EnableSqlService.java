package springbook.user.sqlservice;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.context.annotation.Import;

import springbook.SqlServiceContext;

/**
 * @author Engineer135
 *
 * @Import를 메타 애노테이션으로 넣은 애노테이션 정의
 */
@Import(SqlServiceContext.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableSqlService {

}
