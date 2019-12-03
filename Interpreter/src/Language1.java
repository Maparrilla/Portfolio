//Michael Parrilla
//Xiang Gao
//Salvi Singh

import java.util.Scanner;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileNotFoundException;

public class Language1{
	
	private static Tokenizer tokenizer = new Tokenizer();
	
	public static void language1(String fileName){		
		try{
			File file = new File(fileName+".txt");
			Scanner reader = new Scanner(file);	
			while(reader.hasNext()){
				String string = reader.nextLine();
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
		if(tokenizer.getCurrentToken()=='a'){
			tokenizer.getNextToken();
			if(!S()) return false;
			if(tokenizer.getCurrentToken()=='c'){
				tokenizer.getNextToken();
			}else{return false;}
			if(B()) return true;
		}else if(A()){
			return true;
		}else if(tokenizer.getCurrentToken()=='b'){
			tokenizer.getNextToken();
			return true;
		}
		return false;		
	}
	
	private static boolean A(){
		if(tokenizer.getCurrentToken()=='c'){
			tokenizer.getNextToken();
			if(A()) return true;
		}else if(tokenizer.getCurrentToken()=='d'){
			tokenizer.getNextToken();
			return true;
		}
		return false;
	}
	
	private static boolean B(){
		if(tokenizer.getCurrentToken()=='a'){
			tokenizer.getNextToken();
			if(A()) return true;
		}else if(tokenizer.getCurrentToken()=='d'){
			tokenizer.getNextToken();
			return true;
		}
		return false;
	}
}