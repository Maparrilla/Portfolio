//Michael Parrilla

import java.util.Scanner;
import java.util.ArrayList;

public class Instruction{

	//Instruction elements
	private boolean isReady;
	private String instruction_ID;
	private int latency;
	private int path_Latency;
	private String[] tokens;
	public ArrayList<Instruction> true_dependencies;
	public ArrayList<Instruction> anti_dependencies;
	public ArrayList<Instruction> reverse_dependencies;
	public ArrayList<Instruction> precedence;

	//Constructor
	public Instruction(String instruction){

		//Identify instruction
		if(instruction.contains("add")){
			latency = 1;
			tokens = new String[5];
			instruction_ID = "ADD";
		}else if(instruction.contains("sub")){
			latency = 1;
			tokens = new String[5];
			instruction_ID = "SUB";
		}else if(instruction.contains("mult")){
			latency = 3;
			tokens = new String[5];
			instruction_ID = "MULT";
		}else if(instruction.contains("div")){
			latency = 3;
			tokens = new String[5];
			instruction_ID = "DIV";
		}else if(instruction.contains("loadAO")){
			latency = 5;
			tokens = new String[4];
			instruction_ID = "LOAD";
		}else if(instruction.contains("loadI")){
			latency = 1;
			tokens = new String[4];
			instruction_ID = "LOADI";
		}else if(instruction.contains("loadAI")){
			latency = 5;
			tokens = new String[4];
			instruction_ID = "LOADAI";
		}else if(instruction.contains("load")){
			latency = 5;
			tokens = new String[4];
			instruction_ID = "LOADAO";
		}else if(instruction.contains("storeAO")){
			latency = 5;
			tokens = new String[4];
			instruction_ID = "STORE";
		}else if(instruction.contains("storeAI")){
			latency = 5;
			tokens = new String[4];
			instruction_ID = "STOREAI";
		}else if(instruction.contains("store")){
			latency = 5;
			tokens = new String[4];
			instruction_ID = "STOREAO";
		}else if(instruction.contains("outputAI")){
			latency = 1;
			tokens = new String[2];
			instruction_ID = "OUTPUTAI";
		}
		
		//Initialize ArrayLists for dependencies
		true_dependencies = new ArrayList<Instruction>();
		anti_dependencies = new ArrayList<Instruction>();
		reverse_dependencies = new ArrayList<Instruction>();
		precedence = new ArrayList<Instruction>();
		
		//Read tokens from instruction
		Scanner scan = new Scanner(instruction);
		for(int i = 0; i < tokens.length; i++){
			tokens[i] = scan.next();
			if(tokens[i].equals("r0,")){
				tokens[i] += (" "+scan.next());
			}
		}
		scan.close();
	}

	//Instruction identity accessor function
	public String get_ID(){
		return this.instruction_ID;
	}

	//Instruction latency accessor function
	public int get_Latency(){
		return this.latency;
	}
	
	//Instruction token accessor function
	public String get_Token(int i){
		return tokens[i];
	}

	public int get_PathLatency(){
		return path_Latency;
	}

	public void set_PathLatency(int i){
		path_Latency = i;
	}

	public boolean get_Ready(){
		return isReady;
	}

	public void set_Ready(boolean readiness){
		isReady = readiness;
	}
	
	//Returns instruction as a string
	public String toString(){
		String instruct = new String();
		for(int i = 0; i < tokens.length; i++){
			instruct += (tokens[i]+" ");
		}
		instruct = instruct.substring(0, instruct.length() - 1);
		return instruct;
	}
}