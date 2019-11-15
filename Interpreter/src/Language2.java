//Michael Parrilla
//Xiang Gao
//Salvi Singh

import java.util.Scanner;
import javax.swing.JOptionPane;
import java.io.*;

public class Language2{
	
	private static Tokenizer tokenizer = new Tokenizer();
	
	public static void language2(String fileName){		
		try{
			File file = new File(fileName+".txt");
			Scanner reader = new Scanner(file);
			while(reader.hasNext()){
				String string = reader.nextLine();
				tokenizer.tokenize(string);
				if(ASSGN()&&tokenizer.endOfString()){
					tokenizer.display();
					System.out.println(" is valid");
				}else{
					tokenizer.display();
					System.out.println(" is invalid");
				}
			}		
			reader.close();
		}catch(FileNotFoundException eRef){
			JOptionPane.showMessageDialog(null,"FILE NOT FOUND","ERROR", 2);
		}
	}
	
	private static boolean ASSGN(){
		if(ID()){
			if(tokenizer.getCurrentToken()=='='){
				tokenizer.getNextToken();
			}else{
				return false;
			}
			if(EXPR()) return true;
		}
		return false;
	}
	
	private static boolean EXPR(){
		if(DIGIT()){
			if(tokenizer.getCurrentToken()=='+'){
				tokenizer.getNextToken();
				if(EXPR()) return true;
			}else if(tokenizer.getCurrentToken()=='-'){
				tokenizer.getNextToken();
				if(EXPR()) return true;
			}return true;
		}
		return false;
	}
	
	private static boolean DIGIT(){
		switch(tokenizer.getCurrentToken()){
			case '1':
				tokenizer.getNextToken();
				return true;
			case '2':
				tokenizer.getNextToken();
				return true;
			case '3':
				tokenizer.getNextToken();
				return true;
			case '4':
				tokenizer.getNextToken();
				return true;
			case '5':
				tokenizer.getNextToken();
				return true;
			case '6':
				tokenizer.getNextToken();
				return true;
			case '7':
				tokenizer.getNextToken();
				return true;
			case '8':
				tokenizer.getNextToken();
				return true;
			case '9':
				tokenizer.getNextToken();
				return true;
			default:
				return false;
		}
	}
	
	private static boolean ID(){
		if(tokenizer.getCurrentToken()=='a'){
			tokenizer.getNextToken();
			return true;
		}else if(tokenizer.getCurrentToken()=='b'){
			tokenizer.getNextToken();
			return true;
		}else{
			return false;
		}
	}
}