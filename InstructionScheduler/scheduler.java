//Michael Parrilla

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;

public class scheduler{
	
	public static void main(String[] args){
		
		//Read command line arguments
		String strategy = args[0];
		if(!(strategy.equals("-a") || strategy.equals("-b") || strategy.equals("-c"))){
			System.out.println("INVALID INPUT: "+strategy);
			System.exit(0);
		}
		File inputFile = new File(args[1]);
		File outputFile = new File(args[2]);
		
		//Read input file
		ArrayList<Instruction> instructions = new ArrayList<Instruction>();
		try{
			Scanner reader = new Scanner(inputFile);
			while(reader.hasNextLine()){
				instructions.add(new Instruction(reader.nextLine()));
			}
			reader.close();
		}catch(FileNotFoundException fnfe){
			System.out.println("INPUT FILE NOT FOUND: "+inputFile);
			fnfe.printStackTrace();
		}
		
		//Calculate dependencies
		DependencyGrapher.CalculateGraph(instructions);

		if(strategy.equals("-a")){
			LongestLatency(instructions, outputFile);
		}else if(strategy.equals("-b")){
			HighestLatency(instructions, outputFile);
		}else if(strategy.equals("-c")){
			Hybrid(instructions, outputFile);
		}
	}

	public static void CalculateCycles(ArrayList<Instruction> schedule, File outputFile){
		int[] cycles = new int[schedule.size()];
		cycles[0] = 0;
		int totalDelay = 0;

		for(int a = 1; a < schedule.size(); a++){
			int distance = 0;
			int j = 0;
			for(int b = 0; b < schedule.get(a).true_dependencies.size(); b++){
				int i = schedule.indexOf(schedule.get(a).true_dependencies.get(b));
				if((a - i) < schedule.get(i).get_Latency()){
					int delay = schedule.get(i).get_Latency() - (a - i);
					if(delay > distance){
						distance = delay;
						j = i;
					}
				}
			}
			for(int b = 0; b < schedule.get(a).anti_dependencies.size(); b++){
				int i = schedule.indexOf(schedule.get(a).anti_dependencies.get(b));
				if((a - i) < schedule.get(i).get_Latency()){
					int delay = schedule.get(i).get_Latency() - (a - i);
					if(delay > distance){
						distance = delay;
						j = i;
					}
				}
			}
			if((cycles[j] + schedule.get(j).get_Latency()) > (cycles[a-1] + 1)){
				cycles[a] = cycles[j] + schedule.get(j).get_Latency();
			}else{
				cycles[a] = cycles[a-1] + 1;
			}
		}

		int totalCycles = cycles[cycles.length - 1] + schedule.get(schedule.size() - 1).get_Latency();
		try{
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile, true));
			writer.println("Cycles: " + totalCycles);
			writer.close();
		}catch(IOException ioe){
			System.out.println("Input Output Exception!");
		}
	}

	//Function uses longest latency-weighted path hueristic to schedule instructions
	public static void LongestLatency(ArrayList<Instruction> instructions, File outputFile){

		//Initialize ready and active array lists
		ArrayList<Instruction> schedule = new ArrayList<Instruction>();
		ArrayList<Instruction> ready = new ArrayList<Instruction>();
		ArrayList<Instruction> active = new ArrayList<Instruction>();
		Random rand = new Random(System.currentTimeMillis());

		try{
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile, false));
			while(active.size() != instructions.size()){
				//Find ready instructions
				for(int a = 0; a < instructions.size(); a++){
					int b = 0;
					int c = 0;
					int d = 0;
					while(b < instructions.get(a).true_dependencies.size()){
						if(!active.contains(instructions.get(a).true_dependencies.get(b))){
							break;
						}
						b++;
					}
					while(c < instructions.get(a).anti_dependencies.size()){
						if(!active.contains(instructions.get(a).anti_dependencies.get(c))){
							break;
						}
						c++;
					}
					while(d < instructions.get(a).precedence.size()){
						if(!active.contains(instructions.get(a).precedence.get(d))){
							break;
						}
						d++;
					}
					if(b == instructions.get(a).true_dependencies.size() &&
						c == instructions.get(a).anti_dependencies.size() &&
						d == instructions.get(a).precedence.size() &&
						!ready.contains(instructions.get(a)) && !active.contains(instructions.get(a))){
						ready.add(instructions.get(a));
					}
				}
				//Find longest latency-weighted path amongst ready instructions
				int longest = 0;
				for(int a = 0; a < ready.size(); a++){
					if(ready.get(a).get_PathLatency() > longest){
					longest = ready.get(a).get_PathLatency();
					}
				}
				//Choose instruction to write
				for(int a = 0; a < ready.size(); a++){
					if(ready.get(a).get_PathLatency() < longest){
						ready.remove(a);
						a--;
					}
				}
				//Write instruction
				int random = rand.nextInt(ready.size());
				schedule.add(ready.get(random));
				writer.println("\t" + ready.get(random));
				active.add(ready.get(random));
				ready.remove(ready.get(random));
			}
			writer.close();
		}catch(IOException ioe){
			System.out.println("Input Output Exception!");
		}
		//Call cycle calculator
		CalculateCycles(schedule, outputFile);
	}

	//Function uses highest latency hueristic to schedule instructions
	public static void HighestLatency(ArrayList<Instruction> instructions, File outputFile){
		//Initialize ready and active array lists
		ArrayList<Instruction> schedule = new ArrayList<Instruction>();
		ArrayList<Instruction> ready = new ArrayList<Instruction>();
		ArrayList<Instruction> active = new ArrayList<Instruction>();
		Random rand = new Random(System.currentTimeMillis());

		try{
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile, false));
			while(active.size() != instructions.size()){
				//Find ready instructions
				for(int a = 0; a < instructions.size(); a++){
					int b = 0;
					int c = 0;
					int d = 0;
					while(b < instructions.get(a).true_dependencies.size()){
						if(!active.contains(instructions.get(a).true_dependencies.get(b))){
							break;
						}
						b++;
					}
					while(c < instructions.get(a).anti_dependencies.size()){
						if(!active.contains(instructions.get(a).anti_dependencies.get(c))){
							break;
						}
						c++;
					}
					while(d < instructions.get(a).precedence.size()){
						if(!active.contains(instructions.get(a).precedence.get(d))){
							break;
						}
						d++;
					}
					if(b == instructions.get(a).true_dependencies.size() &&
						c == instructions.get(a).anti_dependencies.size() &&
						d == instructions.get(a).precedence.size() &&
						!ready.contains(instructions.get(a)) && !active.contains(instructions.get(a))){
						ready.add(instructions.get(a));
					}
				}
				//Find highest latency amongst ready instructions
				int highest = 0;
				for(int a = 0; a < ready.size(); a++){
					if(ready.get(a).get_Latency() > highest){
						highest = ready.get(a).get_Latency();
					}
				}
				//Choose instruction to write
				for(int a = 0; a < ready.size(); a++){
					if(ready.get(a).get_Latency() < highest){
						ready.remove(a);
						a--;
					}
				}
				//Write instruction
				int random = rand.nextInt(ready.size());
				schedule.add(ready.get(random));
				writer.println("\t" + ready.get(random));
				active.add(ready.get(random));
				ready.remove(ready.get(random));
			}
			writer.close();
		}catch(IOException ioe){
			System.out.println("Input Output Exception!");
		}
		//Call cycle calculator
		CalculateCycles(schedule, outputFile);
	}

	public static void Hybrid(ArrayList<Instruction> instructions, File outputFile){
		//Initialize ready and active array lists
		ArrayList<Instruction> schedule = new ArrayList<Instruction>();
		ArrayList<Instruction> ready = new ArrayList<Instruction>();
		ArrayList<Instruction> active = new ArrayList<Instruction>();
		Random rand = new Random(System.currentTimeMillis());

		try{
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile, false));
			while(active.size() != instructions.size()){
				//Find ready instructions
				for(int a = 0; a < instructions.size(); a++){
					int b = 0;
					int c = 0;
					int d = 0;
					while(b < instructions.get(a).true_dependencies.size()){
						if(!active.contains(instructions.get(a).true_dependencies.get(b))){
							break;
						}
						b++;
					}
					while(c < instructions.get(a).anti_dependencies.size()){
						if(!active.contains(instructions.get(a).anti_dependencies.get(c))){
							break;
						}
						c++;
					}
					while(d < instructions.get(a).precedence.size()){
						if(!active.contains(instructions.get(a).precedence.get(d))){
							break;
						}
						d++;
					}
					if(b == instructions.get(a).true_dependencies.size() &&
						c == instructions.get(a).anti_dependencies.size() &&
						d == instructions.get(a).precedence.size() &&
						!ready.contains(instructions.get(a)) && !active.contains(instructions.get(a))){
						ready.add(instructions.get(a));
					}
				}
				//Find longest latency-weighted path amongst ready instructions
				int longest = 0;
				for(int a = 0; a < ready.size(); a++){
					if(ready.get(a).get_PathLatency() > longest){
					longest = ready.get(a).get_PathLatency();
					}
				}
				//Choose instruction to write
				for(int a = 0; a < ready.size(); a++){
					if(ready.get(a).get_PathLatency() < longest){
						ready.remove(a);
						a--;
					}
				}
				//Find highest latency amongst ready instructions
				int highest = 0;
				for(int a = 0; a < ready.size(); a++){
					if(ready.get(a).get_Latency() > highest){
						highest = ready.get(a).get_Latency();
					}
				}
				//Choose instruction to write
				for(int a = 0; a < ready.size(); a++){
					if(ready.get(a).get_Latency() < highest){
						ready.remove(a);
						a--;
					}
				}
				//Write instruction
				int random = rand.nextInt(ready.size());
				schedule.add(ready.get(random));
				writer.println("\t" + ready.get(random));
				active.add(ready.get(random));
				ready.remove(ready.get(random));
			}
			writer.close();
		}catch(IOException ioe){
			System.out.println("Input Output Exception!");
		}
		//Call cycle calculator
		CalculateCycles(schedule, outputFile);
	}
}