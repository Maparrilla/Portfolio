//Michael Parrilla
//Xiang Gao
//Salvi Singh

import javax.swing.JOptionPane;

public class Menu{
	
	public static void main(String[] args){
		
		String choice = JOptionPane.showInputDialog(null,"Enter choice:\n1) Language 1\n"+
												"2) Language 2\n3) Language 3\n4) Language 4",
												"Menu",3);
		Integer ch = Integer.parseInt(choice);
		
		String fileName = JOptionPane.showInputDialog(null,"Enter name of file:","Menu",3);
		
		switch(ch){
			case 1:
				Language1.language1(fileName);
				break;
			case 2:
				Language2.language2(fileName);
				break;
			case 3:
				Language3.language3(fileName);
				break;
			case 4:
				Language4.language4(fileName);
				break;
		}
	}
}