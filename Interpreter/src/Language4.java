//Michael Parrilla
//Xiang Gao
//Salvi Singh

import java.util.Scanner;
import java.io.*;
import javax.swing.JOptionPane;

public class Language4{
	
	private static Tokenizer tokenizer = new Tokenizer();
	
	public static void language4(String fileName){		
		try{
			File file = new File(fileName+".txt");
			Scanner reader = new Scanner(file);	
			while(reader.hasNext()){
				String string = reader.next();
				tokenizer.tokenize(string);
				if(S()&&tokenizer.endOfString()){
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
	
	private static boolean S(){
		if(!A()) return false;
		if(tokenizer.getCurrentToken()=='a'){
			tokenizer.getNextToken();
			if(!B()) return false;
			if(tokenizer.getCurrentToken()=='b'){
				tokenizer.getNextToken();
				return true;
			}
		}
		return false;
	}
	
	private static boolean A(){
		if(tokenizer.getCurrentToken()=='b'){
			tokenizer.getNextToken();
			if(A1()) return true;
		}
		return false;
	}
	
	private static boolean A1(){
		if(tokenizer.getCurrentToken()=='b'){
			tokenizer.getNextToken();
			if(A1()) return true;
		}else if(tokenizer.getCurrentToken()=='a'){
			return true;
		}
		return false;
	}
	
	private static boolean B(){
		if(tokenizer.getCurrentToken()=='a'){
			tokenizer.getNextToken();
			if(B1()) return true;
		}
		return false;
	}
	
	private static boolean B1(){
		if(B()) return true;
		else if(tokenizer.getCurrentToken()=='b') return true;
		return false;
	}
}