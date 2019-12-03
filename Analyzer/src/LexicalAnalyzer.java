//Michael Parrilla
//Xiang Gao
//Salvi Singh

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LexicalAnalyzer{
	
	
	private static String string;
	
	private static int iterate;
	
	private static Scanner reader;
	
	private static char charclass;
	
	private static int token;
	
	private static char nextchar;
	
	private static String buffer;
	
	private static int length;
		
	private static char Digit='D';
	
	private static char Letter='L';
	
	private static char eof='E';
	
	private final static int If=1;
	private final static int Procedure=2;
    private final static int Begin=3;
    private final static int End=4;
    private final static int Then=5;
    private final static int Else=6;
    private final static int Endif=7;
    private final static int Left_P=8;   
    private final static int right_P=9;   
    private final static int Add=10;     
    private final static int Subtract=11; 
    private final static int Multiply=12; 
    private final static int Divide=13;   
    private final static int Semico=14;   
    private final static int Equality=15; 
    private final static int Assign=16;
    private final static int Notequals=17;
    private final static int Integerliteral=18;
    private final static int Identifier=19;
    private final static int EOF=99;
    
	
	public static void Initialize(String fileName){
		
		File file = new File(fileName);
		try{
			reader = new Scanner(file);	
		
			string = reader.next();
			
			length=string.length();
		
			Getnextchar();
		}catch(FileNotFoundException eRef){
			
		}
		
	}
	
	private static void Getnextchar(){
		
		char result;
		
		if(iterate>=string.length()){
			
			if(reader.hasNext()){ 
				string = reader.next(); 
				length=string.length();
				iterate=0;
				result=string.charAt(iterate);
				nextchar=result;
				iterate++;
				if(Character.isDigit(result)){  charclass=Digit;  } else
					
				if(Character.isLetter(result)){ charclass=Letter;  } else
				
				{ charclass=result;}
				
			}
			
			else{ charclass=eof;}
		}else{
						
			result=string.charAt(iterate);
		
			nextchar=result;
		
			iterate++;
		
			if(Character.isDigit(result)){  charclass=Digit;  } else
		
			if(Character.isLetter(result)){ charclass=Letter;  } else
		
			{ charclass=result;} 
		}
	}
	
	
	public static int lex(){
    	
    	
		switch(charclass){
  
			case 'L':
	  
				Keywordtest();
	  
				break;
	  
			case 'D':
	  
				Getnextchar();
	  
				while(charclass==Digit) {Getnextchar();}
	  
				token=Integerliteral;
	  
				break;
	  
			case '(':   Getnextchar();  token=Left_P;   break;
			case ')':   Getnextchar();  token=right_P;   break;
			case '+':   Getnextchar();  token=Add;      break;
			case '-':   Getnextchar();  token=Subtract; break;
			case '*':   Getnextchar();  token=Multiply; break;
			case '/':   Getnextchar();  token=Divide;   break;
			case ';':   Getnextchar();  token=Semico;   break;
			case '=':   Getnextchar();  token=Equality;   break;
			case ':':   Getnextchar();

				if(charclass=='='){ Getnextchar();token=Assign; }else{token=EOF;}
				break;

			case '!':   Getnextchar(); if(charclass=='='){ Getnextchar();token=Notequals; }else{token=EOF;}
              	break;
			case 'E':   token=EOF;      break;     }
  
		System.out.print(token+" ");
		return token;
    
  }
  
  

	private static void Keywordtest() {
        
        buffer="";
       
        buffer+=nextchar;
       
        Getnextchar();
       
        int i = iterate;
        while((charclass==Letter||charclass==Digit)&&i<=length){
              
               buffer+=nextchar;
              
               Getnextchar();
       
               i++;
        }
     
        if(buffer.equals("if")){token=If;}
        else if(buffer.equals("procedure")){token=Procedure;}
        else if(buffer.equals("begin")){token=Begin;}
        else if(buffer.equals("end")){token=End;}
        else if(buffer.equals("then")){token=Then;}
        else if(buffer.equals("else")){token=Else;}
        else if(buffer.equals("endif")){token=Endif;}
        else token=Identifier;
	}
}

