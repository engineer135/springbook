package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	public Integer calcSum(String filePath) throws IOException {
		/*BufferedReaderCallback sumCallback = new BufferedReaderCallback(){
			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
				Integer sum = 0;
				String line = null;
				while((line = br.readLine()) != null){//마지막 라인까지 한줄씩 읽어가면서 숫자를 더한다.
					sum += Integer.valueOf(line);
				}
				return sum;
			}
		};
		
		return fileReadTemplate(filePath, sumCallback);*/
		
		LineCallback sumCallback = new LineCallback(){
			public Integer doSomethingWithLine(String line, Integer value){
				return value + Integer.valueOf(line);
			}
		};
		
		return lineReadTemplate(filePath, sumCallback, 0);
	}
	
	public Integer calcMultiply(String filePath) throws IOException {
		/*BufferedReaderCallback multiplyCallback = new BufferedReaderCallback(){
			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
				Integer multiply = 1;
				String line = null;
				while((line = br.readLine()) != null){//마지막 라인까지 한줄씩 읽어가면서 숫자를 더한다.
					multiply *= Integer.valueOf(line);
				}
				return multiply;
			}
		};
		
		return fileReadTemplate(filePath, multiplyCallback);*/
		
		LineCallback multiplyCallback = new LineCallback(){
			public Integer doSomethingWithLine(String line, Integer value){
				return value * Integer.valueOf(line);
			}
		};
		
		return lineReadTemplate(filePath, multiplyCallback, 1);
	}
	
	public Integer fileReadTemplate(String filePath, BufferedReaderCallback callback) throws IOException {
		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new FileReader(filePath)); //한줄씩 읽기 편하게 BufferedReader로 파일을 가져온다
			
			// 콜백 오브젝트 호출. 템플릿에서 만든 컨텍스트 정보인 BufferedReader를 전달해주고 콜백의 작업 결과를 받아둔다.
			int ret = callback.doSomethingWithReader(br);
			
			return ret;
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
	
	public Integer lineReadTemplate(String filePath, LineCallback callback, int initVal) throws IOException {
		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new FileReader(filePath)); //한줄씩 읽기 편하게 BufferedReader로 파일을 가져온다
			
			Integer res = initVal;
			String line = null;
			
			while((line = br.readLine()) != null){//파일의 각 라인을 루프를 돌면서 가져오는 것도 템플릿이 담당한다.
				res = callback.doSomethingWithLine(line, res);//각 라인의 내용을 가지고 계산하는 작업만 콜백에게 맡긴다.
			}
			
			return res;
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
