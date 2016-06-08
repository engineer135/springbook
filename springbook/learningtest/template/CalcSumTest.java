package springbook.learningtest.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class CalcSumTest {
	
	Calculator calculator;
	String numFilePath;
	
	@Before
	public void setUp(){
		this.calculator =  new Calculator();
		/*������
		this.getClass().getResource("").getPath(); // ���� �ڽ��� ���� ���
		this.getClass().getResource("/").getPath(); // classes ������ �ֻ��� ���
		this.getClass().getResource("/com/test/config/config.properties").getPath(); // classes ������������ �����Ͽ� �ش����ϱ����� ���� ���
		 */		
		this.numFilePath = getClass().getResource("numbers.txt").getPath();
	}
	
	@Test
	public void sumOfNumbers() throws IOException {
		assertThat(calculator.calcSum(this.numFilePath), is(10));
	}
	
	@Test
	public void multiplyOfNumbers() throws IOException {
		assertThat(calculator.calcMultiply(this.numFilePath), is(24));
	}
}
