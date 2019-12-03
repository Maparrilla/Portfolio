//Michael Parrilla
//Xiang Gao
//Salvi Singh

import java.util.Scanner;
import javax.swing.JOptionPane;
import java.io.*;

public class Language3{
	
	private static Tokenizer tokenizer = new Tokenizer();
	
	public static void language3(String fileName){
		String string;
		try{
			File file = new File(fileName+".txt");
			Scanner reader = new Scanner(file);
			while(reader.hasNext()){
				string = reader.nextLine();
				tokenizer.tokenize(string);
				if(A2()&&tokenizer.endOfString()){
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
	
	private static boolean A2(){
		if(tokenizer.getCurrentToken()=='a'){
			tokenizer.getNextToken();
			if(!A2()) return false;
			if(tokenizer.getCurrentToken()=='c'){
				tokenizer.getNextToken();
				return true;
			}
		}else if(B2()){
			return true;
		}
		return false;
	}
	
	private static boolean B2(){
		if(tokenizer.getCurrentToken()=='b'){
			tokenizer.getNextToken();
			if(!B2()) return false;
			if(tokenizer.getCurrentToken()=='c'){
				tokenizer.getNextToken();
				return true;
			}else{
				return false;
			}
		}
		return true;
	}
}