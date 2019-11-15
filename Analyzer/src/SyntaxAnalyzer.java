//Michael Parrilla
//Xiang Gao
//Salvi Singh

public class SyntaxAnalyzer{

	private static final int If=1;
	private static final int Procedure=2;
	private static final int Begin=3;
	private static final int End=4;
	private static final int Then=5;
	private static final int Else=6;
	private static final int EndIf=7;
	private static final int Left_P=8;   
	private static final int Right_P=9;   
	private static final int Add=10;     
	private static final int Subtract=11; 
	private static final int Multiply=12; 
	private static final int Divide=13;   
	private static final int Semico=14;   
	private static final int Equality=15; 
	private static final int Assign=16;
	private static final int NotEquals=17;
	private static final int IntegerLiteral=18;
	private static final int Identifier=19;
	private static final int EOF=99;
	
    private static int newtoken;
    
	
	public static boolean program(){
		if(LexicalAnalyzer.lex()!=Procedure){
			return false;
		}else{
			if(LexicalAnalyzer.lex()!=Identifier){
				return false;
			}else{
				if(LexicalAnalyzer.lex()!=Begin){
					return false;
				}else{
					
					newtoken=LexicalAnalyzer.lex();
					
					if(!stmtList()){
						return false;
					}else{
						if(newtoken!=End){
							return false;
						}else{
							if(LexicalAnalyzer.lex()!=Semico){
								return false;
							}else{
								return true;
							}
						}
					}
				}
			}
		}
	}
	
	private static boolean stmtList(){
		if(!stmt()){
			return false;}
		if(newtoken==Identifier||newtoken==If){
			
			if(!stmtList()){return false;}}
		
		newtoken=LexicalAnalyzer.lex();
		
			return true;
		
	}
	
	private static boolean stmt(){
		if(assign()){newtoken=LexicalAnalyzer.lex();
			return true;
		}else if(if_keyword()){
			
			newtoken=LexicalAnalyzer.lex();
			return true;
		}else{
			return false;
		}
	}
	
	private static boolean if_keyword(){
		if(newtoken!=If){
			return false;}
		newtoken=LexicalAnalyzer.lex();
		
		if(newtoken!=Left_P){
				return false;}
		newtoken=LexicalAnalyzer.lex();
		
		if(!bool()){
					return false;
				}
	    if(newtoken!=Right_P){
						return false;
					}
	    newtoken=LexicalAnalyzer.lex();
	    
		if(newtoken!=Then){
							return false;
						}else{
							if(!stmtList()){
								return false;}}
		if(newtoken==Else){
									if(!stmtList()){return false;}}
		
		if(newtoken!=EndIf){return false;}
		newtoken=LexicalAnalyzer.lex();
		if(newtoken==Semico){newtoken=LexicalAnalyzer.lex();
												return true;
											}
		return false;}
	
	
	private static boolean assign(){
		if(!var()){
			return false;}
	
		if(newtoken!=Assign){
				return false;}
		newtoken=LexicalAnalyzer.lex();
		if(!expr()){
					return false;
				}
		if(newtoken==Semico){
			
			newtoken=LexicalAnalyzer.lex();
						return true;}
		return false;
					
					}
	
	private static boolean bool(){
		if(!var()){
			return false;}
		
			
	    if(newtoken==Equality||newtoken==NotEquals){
				
			if(int_method()){newtoken=LexicalAnalyzer.lex();
					return true;
					
				}}
					return false;
		
	}
	
	private static boolean expr(){
		if(term()){			
			
			if(newtoken==Add||newtoken==Subtract||newtoken==Multiply||newtoken==Divide){
					if(term()){newtoken=LexicalAnalyzer.lex();
						return true;
					}else{
						return false;
					}
				}
			newtoken=LexicalAnalyzer.lex();
			
			return true;
			
	
		}
		return false;
	}
		
	private static boolean term(){
		if(var()){newtoken=LexicalAnalyzer.lex();
			return true;
		}else if(int_method()){newtoken=LexicalAnalyzer.lex();
			return true;
		}else{
			return false;
		}
	}
	
	private static boolean var(){
		if(newtoken==Identifier){newtoken=LexicalAnalyzer.lex();
			return true;
		}else{
			return false;
		}
	}
	
	private static boolean int_method(){
		if(newtoken==IntegerLiteral){newtoken=LexicalAnalyzer.lex();
			return true;
		}else{
			return false;
		}
	}
	
}