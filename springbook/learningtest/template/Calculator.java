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
				while((line = br.readLine()) != null){//������ ���α��� ���پ� �о�鼭 ���ڸ� ���Ѵ�.
					sum += Integer.valueOf(line);
				}
				return sum;
			}
		};
		
		return fileReadTemplate(filePath, sumCallback);*/
		
		LineCallback<Integer> sumCallback = new LineCallback<Integer>(){
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
				while((line = br.readLine()) != null){//������ ���α��� ���پ� �о�鼭 ���ڸ� ���Ѵ�.
					multiply *= Integer.valueOf(line);
				}
				return multiply;
			}
		};
		
		return fileReadTemplate(filePath, multiplyCallback);*/
		
		LineCallback<Integer> multiplyCallback = new LineCallback<Integer>(){
			public Integer doSomethingWithLine(String line, Integer value){
				return value * Integer.valueOf(line);
			}
		};
		
		return lineReadTemplate(filePath, multiplyCallback, 1);
	}
	
	public String concatenate(String filePath) throws IOException {
		LineCallback<String> concaenateCallback = new LineCallback<String>(){
			public String doSomethingWithLine(String line, String value){
				return value + line;
			}
		};
		
		return lineReadTemplate(filePath, concaenateCallback, "");
	}
	
	public Integer fileReadTemplate(String filePath, BufferedReaderCallback callback) throws IOException {
		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new FileReader(filePath)); //���پ� �б� ���ϰ� BufferedReader�� ������ �����´�
			
			// �ݹ� ������Ʈ ȣ��. ���ø����� ���� ���ؽ�Ʈ ������ BufferedReader�� �������ְ� �ݹ��� �۾� ����� �޾Ƶд�.
			int ret = callback.doSomethingWithReader(br);
			
			return ret;
		}catch(IOException e){
			System.out.println(e.getMessage());
			throw e;
		}finally{
			if(br != null){//�ݵ�� nullüũ ����� ��!
				try{
					br.close();
				}catch(IOException e){
					System.out.println(e.getMessage());
					throw e;
				}
			}
		}
	}
	
	public <T> T lineReadTemplate(String filePath, LineCallback<T> callback, T initVal) throws IOException {
		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new FileReader(filePath)); //���پ� �б� ���ϰ� BufferedReader�� ������ �����´�
			
			T res = initVal;
			String line = null;
			
			while((line = br.readLine()) != null){//������ �� ������ ������ ���鼭 �������� �͵� ���ø��� ����Ѵ�.
				res = callback.doSomethingWithLine(line, res);//�� ������ ������ ������ ����ϴ� �۾��� �ݹ鿡�� �ñ��.
			}
			
			return res;
		}catch(IOException e){
			System.out.println(e.getMessage());
			throw e;
		}finally{
			if(br != null){//�ݵ�� nullüũ ����� ��!
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
