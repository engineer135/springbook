package springbook.learningtest.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

public class CalcSumTest {
	
	@Test
	public void sumOfNumbers() throws IOException {
		Calculator calculator = new Calculator();
		/*������
		this.getClass().getResource("").getPath(); // ���� �ڽ��� ���� ���
		this.getClass().getResource("/").getPath(); // classes ������ �ֻ��� ���
		this.getClass().getResource("/com/test/config/config.properties").getPath(); // classes ������������ �����Ͽ� �ش����ϱ����� ���� ���
		 */		
		int sum = calculator.calcSum(getClass().getResource("numbers.txt").getPath());
		assertThat(sum, is(10));
	}
}
