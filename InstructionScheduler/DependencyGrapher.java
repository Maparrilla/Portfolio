//Michael Parrilla

import java.util.ArrayList;

public class DependencyGrapher{

	public static void CalculateGraph(ArrayList<Instruction> instructions){
		
		for(int i = 0; i < instructions.size(); i++){
			if(instructions.get(i).get_ID().equals("LOADI")){
				CalculateLOADI(instructions, i);
			}else if(instructions.get(i).get_ID().equals("ADD") ||
				instructions.get(i).get_ID().equals("SUB") ||
				instructions.get(i).get_ID().equals("DIV") ||
				instructions.get(i).get_ID().equals("MULT")){
				CalculateASDM(instructions, i);
			}else if(instructions.get(i).get_ID().equals("STORE") ||
						instructions.get(i).get_ID().equals("STOREAI") ||
						instructions.get(i).get_ID().equals("STOREAO")){
				CalculateSTOREs(instructions, i);
			}else if(instructions.get(i).get_ID().equals("LOAD") ||
						instructions.get(i).get_ID().equals("LOADAI") ||
						instructions.get(i).get_ID().equals("LOADAO")){
				CalculateLOADs(instructions, i);
			}else if(instructions.get(i).get_ID().equals("OUTPUTAI")){
				CalculateOUTPUT(instructions, i);
			}
		}
		ReduceDependencies(instructions);
		CalculateReverseDependencies(instructions);
		int longest_latency = 0;
		for(int a = 0; a < instructions.size(); a++){
			longest_latency = Math.max(longest_latency, CalculateLatencies(instructions.get(a)));
		}
	}

	//Function for calculating dependencies of LOADI instructions
	public static void CalculateLOADI(ArrayList<Instruction> instructions, int i){
		for(int j = (i-1); j >= 0; j--){
			if(instructions.get(j).get_ID().equals("ADD") ||
				instructions.get(j).get_ID().equals("SUB") ||
				instructions.get(j).get_ID().equals("DIV") ||
				instructions.get(j).get_ID().equals("MULT")){
				if(instructions.get(i).get_Token(3).equals(instructions.get(j).get_Token(1).replace(",","")) ||
					instructions.get(i).get_Token(3).equals(instructions.get(j).get_Token(2))){
					if(!instructions.get(i).anti_dependencies.contains(instructions.get(j))){
						instructions.get(i).anti_dependencies.add(instructions.get(j));
					}
				}
			}else if(instructions.get(j).get_ID().equals("LOAD") ||
						instructions.get(j).get_ID().equals("STORE") ||
						instructions.get(j).get_ID().equals("STOREAI") ||
						instructions.get(j).get_ID().equals("STOREAO")){
				if(instructions.get(i).get_Token(3).equals(instructions.get(j).get_Token(1))){
					if(!instructions.get(i).anti_dependencies.contains(instructions.get(j))){
						instructions.get(i).anti_dependencies.add(instructions.get(j));
					}
				}
			}else if(instructions.get(j).get_ID().equals("LOADI")){
				if(instructions.get(i).get_Token(3).equals(instructions.get(j).get_Token(3))){
					if(!instructions.get(i).anti_dependencies.contains(instructions.get(j))){
						instructions.get(i).anti_dependencies.add(instructions.get(j));
					}
				}
			}
		}
	}

	//Function for calculating dependencies of ADD, SUB, DIV, and MULT instructions
	public static void CalculateASDM(ArrayList<Instruction> instructions, int i){
		//Calculate true-dependencies
		for(int j = (i-1); j >= 0; j--){
			if(instructions.get(j).get_ID().equals("ADD") ||
				instructions.get(j).get_ID().equals("SUB") ||
				instructions.get(j).get_ID().equals("DIV") ||
				instructions.get(j).get_ID().equals("MULT")){
				if(instructions.get(i).get_Token(1).replace(",","").equals(instructions.get(j).get_Token(4)) ||
					instructions.get(i).get_Token(2).equals(instructions.get(j).get_Token(4))){
					if(!instructions.get(i).true_dependencies.contains(instructions.get(j))){
						instructions.get(i).true_dependencies.add(instructions.get(j));
					}
				}
			}else if(instructions.get(j).get_ID().equals("STORE") ||
						instructions.get(j).get_ID().equals("LOAD") ||
						instructions.get(j).get_ID().equals("LOADI") ||
						instructions.get(j).get_ID().equals("LOADAI") ||
						instructions.get(j).get_ID().equals("LOADAO")){
				if(instructions.get(i).get_Token(1).replace(",","").equals(instructions.get(j).get_Token(3)) ||
					instructions.get(i).get_Token(2).equals(instructions.get(j).get_Token(3))){
					if(!instructions.get(i).true_dependencies.contains(instructions.get(j))){
						instructions.get(i).true_dependencies.add(instructions.get(j));
					}
				}
			}
		}
		//Calculate anti-dependencies
		for(int j = (i-1); j >= 0; j--){
			if(instructions.get(j).get_ID().equals("ADD") ||
				instructions.get(j).get_ID().equals("SUB") ||
				instructions.get(j).get_ID().equals("DIV") ||
				instructions.get(j).get_ID().equals("MULT")){
				if(instructions.get(i).get_Token(4).equals(instructions.get(j).get_Token(1).replace(",","")) ||
					instructions.get(i).get_Token(4).equals(instructions.get(j).get_Token(2))){
					if(!instructions.get(i).anti_dependencies.contains(instructions.get(j))){
						instructions.get(i).anti_dependencies.add(instructions.get(j));
					}
				}
			}else if(instructions.get(j).get_ID().equals("LOAD") ||
						instructions.get(j).get_ID().equals("STORE") ||
						instructions.get(j).get_ID().equals("STOREAI") ||
						instructions.get(j).get_ID().equals("STOREAO")){
				if(instructions.get(i).get_Token(4).equals(instructions.get(j).get_Token(1))){
					if(!instructions.get(i).anti_dependencies.contains(instructions.get(j))){
						instructions.get(i).anti_dependencies.add(instructions.get(j));
					}
				}
			}
		}
	}

	//Function for calculating dependencies of STORE, STOREAI, and STOREAO instructions
	public static void CalculateSTOREs(ArrayList<Instruction> instructions, int i){
		//Calculate true-dependencies
		for(int j = (i-1); j >= 0; j--){
			if(instructions.get(j).get_ID().equals("ADD") ||
				instructions.get(j).get_ID().equals("SUB") ||
				instructions.get(j).get_ID().equals("DIV") ||
				instructions.get(j).get_ID().equals("MULT")){
				if(instructions.get(i).get_Token(1).equals(instructions.get(j).get_Token(4))){
					if(!instructions.get(i).true_dependencies.contains(instructions.get(j))){
						instructions.get(i).true_dependencies.add(instructions.get(j));
					}
				}
			}else if(instructions.get(j).get_ID().equals("LOAD") ||
						instructions.get(j).get_ID().equals("LOADAI") ||
						instructions.get(j).get_ID().equals("LOADAO")){
				if(instructions.get(i).get_Token(1).equals(instructions.get(j).get_Token(3))){
					if(!instructions.get(i).true_dependencies.contains(instructions.get(j))){
						instructions.get(i).true_dependencies.add(instructions.get(j));
					}
				}
			}else if(instructions.get(j).get_ID().equals("LOADI")){
				if(instructions.get(i).get_Token(3).substring(0,2).equals(instructions.get(j).get_Token(3)) ||
					instructions.get(i).get_Token(1).equals(instructions.get(j).get_Token(3))){
					if(!instructions.get(i).true_dependencies.contains(instructions.get(j))){
						instructions.get(i).true_dependencies.add(instructions.get(j));
					}
				}
			}
		}
		//Calculate anti-dependencies
		for(int j = (i-1); j >= 0; j--){
			if(instructions.get(j).get_ID().equals("LOAD") ||
				instructions.get(j).get_ID().equals("LOADAI") ||
				instructions.get(j).get_ID().equals("LOADAO")){
				if(instructions.get(i).get_Token(3).equals(instructions.get(j).get_Token(1))){
					if(!instructions.get(i).anti_dependencies.contains(instructions.get(j))){
						instructions.get(i).anti_dependencies.add(instructions.get(j));
					}
				}
			}
		}
	}

	//Function for calculating dependencies of LOAD, LOADAI, and LOADAO instructions
	public static void CalculateLOADs(ArrayList<Instruction> instructions, int i){
		//Calculate true-dependencies
		for(int j = (i-1); j >= 0; j--){
			if(instructions.get(j).get_ID().equals("STORE") ||
				instructions.get(j).get_ID().equals("STOREAI") ||
				instructions.get(j).get_ID().equals("STOREAO")){
				if(instructions.get(i).get_Token(1).equals(instructions.get(j).get_Token(3))){
					if(!instructions.get(i).true_dependencies.contains(instructions.get(j))){
						instructions.get(i).true_dependencies.add(instructions.get(j));
					}
				}
			}
		}
		//Calculate anti-dependencies
		for(int j = (i-1); j >= 0; j--){
			if(instructions.get(j).get_ID().equals("ADD") ||
				instructions.get(j).get_ID().equals("SUB") ||
				instructions.get(j).get_ID().equals("DIV") ||
				instructions.get(j).get_ID().equals("MULT")){
				if(instructions.get(i).get_Token(3).equals(instructions.get(j).get_Token(1).replace(",","")) ||
					instructions.get(i).get_Token(3).equals(instructions.get(j).get_Token(2))){
					if(!instructions.get(i).anti_dependencies.contains(instructions.get(j))){
						instructions.get(i).anti_dependencies.add(instructions.get(j));
					}
				}
			}else if(instructions.get(j).get_ID().equals("STORE") ||
						instructions.get(j).get_ID().equals("STOREAI") ||
						instructions.get(j).get_ID().equals("STOREAO")){
				if(instructions.get(i).get_Token(3).equals(instructions.get(j).get_Token(1))){
					if(!instructions.get(i).anti_dependencies.contains(instructions.get(j))){
						instructions.get(i).anti_dependencies.add(instructions.get(j));
					}
				}
			}
		}
	}

	//Function for calculating dependencies of OUTPUTAI instructions
	public static void CalculateOUTPUT(ArrayList<Instruction> instructions, int i){
		for(int j = (i-1); j >= 0; j--){
			if(instructions.get(j).get_ID().equals("STOREAI")){
				if(instructions.get(i).get_Token(1).equals(instructions.get(j).get_Token(3))){
					if(!instructions.get(i).true_dependencies.contains(instructions.get(j))){
						instructions.get(i).true_dependencies.add(instructions.get(j));
					}
				}
			}
		}
	}
	
	//Function for removing redundant dependencies
	public static void ReduceDependencies(ArrayList<Instruction> instructions){
		//Remove redundancies
		for(int a = 0; a < instructions.size(); a++){
			for(int b = 0; b < instructions.get(a).anti_dependencies.size(); b++){
				if(instructions.get(a).true_dependencies.contains(instructions.get(a).anti_dependencies.get(b))){
					instructions.get(a).anti_dependencies.remove(instructions.get(a).anti_dependencies.get(b));
				}
			}
		}
		for(int a = 0; a < instructions.size(); a++){
			//Reduce true-dependencies
			for(int b = 0; b < instructions.get(a).true_dependencies.size(); b++){
				for(int c = 0; c < instructions.get(a).true_dependencies.get(b).true_dependencies.size(); c++){
					if(instructions.get(a).true_dependencies.contains(instructions.get(a).true_dependencies.get(b).true_dependencies.get(c))){
						instructions.get(a).true_dependencies.remove(instructions.get(a).true_dependencies.get(b).true_dependencies.get(c));
						c--;
					}
				}
				for(int c = 0; c < instructions.get(a).true_dependencies.get(b).anti_dependencies.size(); c++){
					if(instructions.get(a).true_dependencies.contains(instructions.get(a).true_dependencies.get(b).anti_dependencies.get(c))){
						instructions.get(a).true_dependencies.remove(instructions.get(a).true_dependencies.get(b).anti_dependencies.get(c));
						c--;
					}
				}
			}
			//Reduce anti-dependencies
			for(int b = 0; b < instructions.get(a).anti_dependencies.size(); b++){
				for(int c = 0; c < instructions.get(a).anti_dependencies.get(b).anti_dependencies.size(); c++){
					if(instructions.get(a).anti_dependencies.contains(instructions.get(a).anti_dependencies.get(b).anti_dependencies.get(c))){
						instructions.get(a).anti_dependencies.remove(instructions.get(a).anti_dependencies.get(b).anti_dependencies.get(c));
						c--;
					}
				}
				for(int c = 0; c < instructions.get(a).anti_dependencies.get(b).true_dependencies.size(); c++){
					if(instructions.get(a).anti_dependencies.contains(instructions.get(a).anti_dependencies.get(b).true_dependencies.get(c))){
						instructions.get(a).anti_dependencies.remove(instructions.get(a).anti_dependencies.get(b).true_dependencies.get(c));
						c--;
					}
				}
			}
			//Reduce inter-dependencies
			for(int b = 0; b < instructions.get(a).true_dependencies.size(); b++){
				for(int c = 0; c < instructions.get(a).true_dependencies.get(b).true_dependencies.size(); c++){
					if(instructions.get(a).anti_dependencies.contains(instructions.get(a).true_dependencies.get(b).true_dependencies.get(c))){
						instructions.get(a).anti_dependencies.remove(instructions.get(a).true_dependencies.get(b).true_dependencies.get(c));
					}
				}
				for(int c = 0; c < instructions.get(a).true_dependencies.get(b).anti_dependencies.size(); c++){
					if(instructions.get(a).anti_dependencies.contains(instructions.get(a).true_dependencies.get(b).anti_dependencies.get(c))){
						instructions.get(a).anti_dependencies.remove(instructions.get(a).true_dependencies.get(b).anti_dependencies.get(c));
					}
				}
			}
			for(int b = 0; b < instructions.get(a).anti_dependencies.size(); b++){
				for(int c = 0; c < instructions.get(a).anti_dependencies.get(b).anti_dependencies.size(); c++){
					if(instructions.get(a).true_dependencies.contains(instructions.get(a).anti_dependencies.get(b).anti_dependencies.get(c))){
						instructions.get(a).true_dependencies.remove(instructions.get(a).anti_dependencies.get(b).anti_dependencies.get(c));
					}
				}
				for(int c = 0; c < instructions.get(a).anti_dependencies.get(b).true_dependencies.size(); c++){
					if(instructions.get(a).true_dependencies.contains(instructions.get(a).anti_dependencies.get(b).true_dependencies.get(c))){
						instructions.get(a).true_dependencies.remove(instructions.get(a).anti_dependencies.get(b).true_dependencies.get(c));
					}
				}
			}

		}
		//Establish output precedence
		for(int a = 0; a < instructions.size(); a++){
			if(instructions.get(a).get_ID().equals("OUTPUTAI")){
				for(int b = (a-1); b >= 0; b--){
					if(instructions.get(b).get_ID().equals("OUTPUTAI")){
						if(!instructions.get(a).precedence.contains(instructions.get(b))){
							instructions.get(a).precedence.add(instructions.get(b));
						}
					}
				}
			}
		}
	}
	
	//Function for calculating what instructions are dependent upon each instruction
	public static void CalculateReverseDependencies(ArrayList<Instruction> instructions){
		for(int a = 0; a < instructions.size(); a++){
			for(int b = 0; b < instructions.size(); b++){
				if(instructions.get(b).true_dependencies.contains(instructions.get(a)) ||
					instructions.get(b).anti_dependencies.contains(instructions.get(a))){
					if(!instructions.get(a).reverse_dependencies.contains(instructions.get(b))){
						instructions.get(a).reverse_dependencies.add(instructions.get(b));
					}
				}
			}
			if(instructions.get(a).get_ID().equals("OUTPUTAI") && a != (instructions.size() - 1)){
				for(int b = a+1; b < instructions.size(); b++){
					if(instructions.get(b).get_ID().equals("OUTPUTAI")){
						if(!instructions.get(a).reverse_dependencies.contains(instructions.get(b))){
							instructions.get(a).reverse_dependencies.add(instructions.get(b));
						}
					}
				}
			}
		}
		for(int a = 0; a < instructions.size(); a++){
			for(int b = 0; b < instructions.get(a).reverse_dependencies.size(); b++){
				for(int c = 0; c < instructions.get(a).reverse_dependencies.get(b).reverse_dependencies.size(); c++){
					if(instructions.get(a).reverse_dependencies.contains(instructions.get(a).reverse_dependencies.get(b).reverse_dependencies.get(c))){
						instructions.get(a).reverse_dependencies.remove(instructions.get(a).reverse_dependencies.get(b).reverse_dependencies.get(c));
						c--;
					}
				}
			}
		}
	}

	//Function recursively calculates longest-latency weighted paths
	public static int CalculateLatencies(Instruction instruct){
		if(instruct.reverse_dependencies.isEmpty()){
			instruct.set_PathLatency(instruct.get_Latency());
			return instruct.get_PathLatency();
		}else{
			int max = 0;
			for(int a = 0; a < instruct.reverse_dependencies.size(); a++){
				if(CalculateLatencies(instruct.reverse_dependencies.get(a)) > max){
					max = CalculateLatencies(instruct.reverse_dependencies.get(a));
				}
			}
			instruct.set_PathLatency(instruct.get_Latency() + max);
			return instruct.get_PathLatency();
		}
	}
}