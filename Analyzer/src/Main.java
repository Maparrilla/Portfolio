//Michael Parrilla
//Xiang Gao
//Salvi Singh

import java.util.Scanner;

public class Main{
	
	public static void main(String[] args){
		
		Scanner reader = new Scanner(System.in);
		System.out.print("Enter file name(Leave out '.txt'): ");
		String fileName = reader.nextLine()+".txt";
		reader.close();
		
		LexicalAnalyzer.Initialize(fileName);
		
		if(SyntaxAnalyzer.program()){
			System.out.println("Program is valid!");
		}else{
			System.out.println("Program is NOT valid!");
		}				
	}
}