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
		/*절대경로
		this.getClass().getResource("").getPath(); // 현재 자신의 절대 경로
		this.getClass().getResource("/").getPath(); // classes 폴더의 최상위 경로
		this.getClass().getResource("/com/test/config/config.properties").getPath(); // classes 폴더에서부터 시작하여 해당파일까지의 절대 경로
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
