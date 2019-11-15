//Michael Parrilla
//Xiang Gao
//Salvi Singh

import java.util.ArrayList;

public class Tokenizer{
	
	private static ArrayList<Character> word = new ArrayList<Character>();
	private int currentToken;
	
	private final char EOS = 'z';
	
	public Tokenizer(){
		currentToken = 0;
		word.clear();
	}
	
	public void tokenize(String string){
		word.clear();
		int i = 0;
		while(i<string.length()){
			if(string.charAt(i)!='\0'&&string.charAt(i)!='\t'){
				word.add(string.charAt(i));
			}
			i++;
		}
		word.add(EOS);
		currentToken = 0;
	}
	
	public char getCurrentToken(){
		return word.get(currentToken);
	}
	
	public void getNextToken(){
		currentToken++;
	}
	
	public boolean endOfString(){
		if(word.get(currentToken)==EOS){
			return true;
		}else{
			return false;
		}
	}
	
	public void display(){
		int i = 0;
		while(i < (word.size()-1)){
			System.out.print(word.get(i));
			i++;
		}
	}
}