package springbook.learningtest.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

public class CalcSumTest {
	
	@Test
	public void sumOfNumbers() throws IOException {
		Calculator calculator = new Calculator();
		/*절대경로
		this.getClass().getResource("").getPath(); // 현재 자신의 절대 경로
		this.getClass().getResource("/").getPath(); // classes 폴더의 최상위 경로
		this.getClass().getResource("/com/test/config/config.properties").getPath(); // classes 폴더에서부터 시작하여 해당파일까지의 절대 경로
		 */		
		int sum = calculator.calcSum(getClass().getResource("numbers.txt").getPath());
		assertThat(sum, is(10));
	}
}
