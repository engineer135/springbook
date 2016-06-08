package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	public Integer calcSum(String filePath) throws IOException {
		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new FileReader(filePath)); //한줄씩 읽기 편하게 BufferedReader로 파일을 가져온다
			Integer sum = 0;
			String line = null;
			while((line = br.readLine()) != null){//마지막 라인까지 한줄씩 읽어가면서 숫자를 더한다.
				sum += Integer.valueOf(line);
			}
			return sum;
		}catch(IOException e){
			System.out.println(e.getMessage());
			throw e;
		}finally{
			if(br != null){//반드시 null체크 해줘야 함!
				try{
					br.close();
				}catch(IOException e){
					System.out.println(e.getMessage());
					throw e;
				}
			}
		}
	}
}
