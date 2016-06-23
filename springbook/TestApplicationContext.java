package springbook;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Engineer135
 * 
 * applicationContext.xml을 대체할 DI 설정정보를 담은 클래스
 * @Configuration 어노테이션을 붙여주면 오케이
 *
 */
@Configuration
@ImportResource("/applicationContext.xml") //XML의 DI 정보를 활용한다.
public class TestApplicationContext {

}
