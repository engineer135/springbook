import org.springframework.stereotype.Component;

/**
 * @author Engineer135
 *
 * SnsConnector 애노테이션
 * 여기에 @Component 메타 애노테이션을 붙여주면 클래스에 @SnsConnector만 붙여줘도 자동 빈 등록 대상이 된다.
 * 
 */
@Component
public @interface SnsConnector {

}
