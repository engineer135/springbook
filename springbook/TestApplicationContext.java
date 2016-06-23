package springbook;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Engineer135
 * 
 * applicationContext.xml�� ��ü�� DI ���������� ���� Ŭ����
 * @Configuration ������̼��� �ٿ��ָ� ������
 *
 */
@Configuration
@ImportResource("/applicationContext.xml") //XML�� DI ������ Ȱ���Ѵ�.
public class TestApplicationContext {

}
