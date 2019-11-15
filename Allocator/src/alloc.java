//Michael Parrilla
//CS 415-01

import java.io.*;
import java.util.Scanner;

public class alloc {
	
	private static String[] instructions = new String[1000];

	public static void main(String[] args){		
		//Take arguments from command line
		//k = number of registers
		int k = (new Integer(args[0])).intValue();
		//flag denotes which algorithm to use
		char flag = args[1].charAt(0);
		//input contains name of input file
		String input = args[2];
		System.out.println(input);
		
		//Select algorithm based on input
		switch(flag){
			case 'b': System.out.println("Cycles: "+bottomUp(k, input));
						break;
			case 's': System.out.println("Cycles: "+simpleTopDown(k, input));
						break;
			case 't': System.out.println("Cycles: "+topDown(k, input));
						break;
			case 'o': System.out.println("Cycles: "+customTopDown(k, input));
						break;
		}
	}
	
	private static int bottomUp(int k, String input){
		
		//initialize variable for counting cycles
		int cycles = 0;
		
		//Parse input file
		parser(input);
		
		//Initialize array to represent registers
		String[] registers = new String[k];
		
		//Array to keep track of which registers are used soonest
		int[] registersUsedSoon = new int[k];
		
		//Array to represent stored values
		String[] stored = new String[256];
		
		//Initialize increment variable
		int i = 0;
		
		while(i < 1000 && instructions[i] != null){
			
			//Temporary number holder
			int tempNum = 0;
			
			//Index of last used register
			int lastUsed = 0;
			
			//Index of stored register
			int storedReg = 0;
			
			//Indices of registers in an expression
			int regNum1 = 0;
			int regNum2 = 0;
			int regNum3 = 0;
			
			for(int a = 0;a < k;a++){
				registersUsedSoon[a] = 1000;
			}
			
			//LoadI
			if(instructions[i].equals("loadI")){
				if(instructions[i+1].equals("1024") && instructions[i+3].equals("r0")){
					System.out.println("loadI 1024 => r0");
					cycles += 1;
					i += 4;
				}else{
					int j = 0;
					boolean isInRegister = false;
					//Check if already in register
					while(j < k){
						if(instructions[i+3].equals(registers[j])){
							isInRegister = true;
							regNum2 = j;
						}
						j++;
					}
					//Look for empty register
					if(!isInRegister){
						j = 0;
						while(j < k){
							if(registers[j] == null){
								registers[j] = instructions[i+3];
								System.out.println("loadI "+instructions[i+1]+" => r"+(j+1));
								cycles += 1;
								break;
							}else{
								j++;
							}
						}
					}else{
						//Check if value was stored
						boolean isStored = false;
						j = 0;
						while(j < 256){
							if(registers[regNum2].equals(stored[j])){
								isStored = true;
								break;
							}
							j++;
						}
						//Store value if it has not been stored
						if(!isStored){
							j = 0;
							while(j < 256){
								if(stored[j] == null){
									stored[j] = registers[regNum2];
									System.out.println("storeAI r"+(regNum2+1)+" => r0, "+(-4*j-4));
									cycles += 3;
									break;
								}
								j++;
							}
						}
						registers[regNum2] = instructions[i+3];
						System.out.println("loadI "+instructions[i+1]+" => r"+(regNum2+1));
						cycles += 1;
					}
					//If no registers are empty look for last used register
					j = 0;
					while(j < k){
						if(instructions[i+3].equals(registers[j])){
							isInRegister = true;
						}
						j++;
					}
					//Find last used register
					if(!isInRegister){
						j = 0;
						while(j < k){
							for(int n = i;n < 1000 && instructions[n] != null;n++){
								if(instructions[n].equals(registers[j])){
									if(!(n > registersUsedSoon[j])){
										registersUsedSoon[j] = n;
									}								
								}
							}
							j++;
						}
						for(int a = 0;a < k;a++){
							if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
								lastUsed = a;
							}
						}
						//Check if last used register has been stored
						boolean isStored = false;
						j = 0;
						while(j < 256){
							if(registers[lastUsed].equals(stored[j])){
								isStored = true;
								break;
							}
							j++;
						}
						if(!isStored){
							j = 0;
							//Spill register to memory
							while(j < 256){
								if(stored[j] == null){
									stored[j] = registers[lastUsed];
									System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
									cycles += 3;
									break;
								}
								j++;
							}
						}
						//Write loadI instruction
						registers[lastUsed] = instructions[i+3];
						System.out.println("loadI "+instructions[i+1]+" => r"+(lastUsed+1));
						cycles += 1;
					}
					i += 4;
				}
			//Load
			}else if(instructions[i].equals("load")){
				int j = 0;
				boolean isInRegister = false;
				//Check if item to be loaded is in register
				while(j < k){
					if(instructions[i+1].equals(registers[j])){
						isInRegister = true;
						regNum1 = j;
					}
					j++;
				}
				//Check where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+1].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+1];
					regNum1 = lastUsed;
					cycles += 3;
				}
				//Check if target is in register
				j = 0;
				isInRegister = false;
				while(j < k){
					if(instructions[i+3].equals(registers[j])){
						isInRegister = true;
						regNum2 = j;
					}
					j++;
				}
				//Look for empty register
				if(!isInRegister){
					j = 0;
					while(j < k){
						if(registers[j] == null){
							registers[j] = instructions[i+3];
							regNum2 = j;
							System.out.println("load r"+(regNum1+1)+" => r"+(regNum2+1));
							cycles += 3;
							break;
						}
						j++;
					}
				}else{
					//Check if value was stored
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[regNum2].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					//Store value if it has not been stored
					if(!isStored){
						j = 0;
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[regNum2];
								System.out.println("storeAI r"+(regNum2+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					registers[regNum2] = instructions[i+3];
					System.out.println("load r"+(regNum1+1)+" => r"+(regNum2+1));
					cycles += 3;
				}
				//Check if empty register was found for the virtual register
				isInRegister = false;
				j = 0;
				while(j < k){
					if(instructions[i+3].equals(registers[j])){
						isInRegister = true;
					}
					j++;
				}
				//if no empty register was found lo0k for last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = (i+4);n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(n > tempNum){
									tempNum = n;
									lastUsed = j;
								}
							}
						}
						j++;
					}
					//Check if last used register has been stored
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					//Write loadI instruction
					registers[lastUsed] = instructions[i+3];
					System.out.println("load r"+(regNum1+1)+" => r"+(lastUsed+1));
					cycles += 3;
				}
				i += 4;
			//Store
			}else if(instructions[i].equals("store")){
				int j = 0;
				boolean isInRegister = false;
				//Check if item to be stored is in register
				while(j < k){
					if(instructions[i+1].equals(registers[j])){
						isInRegister = true;
						regNum1 = j;
					}
					j++;
				}
				//Check where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+1].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+1];
					regNum1 = lastUsed;
					cycles += 3;
				}
				//Check if target is in register
				j = 0;
				isInRegister = false;
				while(j < k){
					if(instructions[i+3].equals(registers[j])){
						isInRegister = true;
						regNum2 = j;
					}
					j++;
				}
				//Look for empty register
				if(!isInRegister){
					j = 0;
					while(j < k){
						if(registers[j] == null){
							registers[j] = instructions[i+3];
							regNum2 = j;
							System.out.println("store r"+(regNum1+1)+" => r"+(j+1));
							cycles += 3;
							break;
						}else{
							j++;
						}
					}
				}
				//Check if empty register was found for the virtual register
				isInRegister = false;
				j = 0;
				while(j < k){
					if(instructions[i+3].equals(registers[j])){
						isInRegister = true;
					}
					j++;
				}
				//if no empty register was found look for last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = (i+4);n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(n > tempNum){
									tempNum = n;
									lastUsed = j;
								}
							}
						}
						j++;
					}
					//Check if last used register has been stored
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					//Write loadI instruction
					registers[lastUsed] = instructions[i+3];
					System.out.println("store r"+(regNum1+1)+" => r"+(lastUsed+1));
					cycles += 3;
				}else{
					//Check if value was stored
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[regNum2].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					//Store value if it has not been stored
					if(!isStored){
						j = 0;
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[regNum2];
								System.out.println("storeAI r"+(regNum2+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					registers[regNum2] = instructions[i+3];
					System.out.println("store r"+(regNum1+1)+" => r"+(regNum2+1));
					cycles += 3;
				}
				i += 4;
			//Add
			}else if(instructions[i].equals("add")){
				int j = 0;
				boolean isInRegister = false;
				//Check if regNum1 is in register
				while(j < k){
					if(instructions[i+1].equals(registers[j])){
						isInRegister = true;
						regNum1 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+1].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+1];
					regNum1 = lastUsed;
					cycles += 3;
				}
				j = 0;
				isInRegister = false;
				//Check if regNum2 is in register
				while(j < k){
					if(instructions[i+2].equals(registers[j])){
						isInRegister = true;
						regNum2 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+2].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+2];
					regNum2 = lastUsed;
					cycles += 3;
				}
				//Check if regNum3 is in register
				while(j < k){
					if(registers[j].equals(instructions[i+4])){
						isInRegister = true;
						regNum3 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+4].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					registers[lastUsed] = instructions[i+4];
					regNum3 = lastUsed;
					cycles += 3;
				}
				System.out.println("add r"+(regNum1+1)+", r"+(regNum2+1)+" => r"+(regNum3+1));
				cycles += 1;
				i += 5;
			//Mult
			}else if(instructions[i].equals("mult")){
				int j = 0;
				boolean isInRegister = false;
				//Check if regNum1 is in register
				while(j < k){
					if(instructions[i+1].equals(registers[j])){
						isInRegister = true;
						regNum1 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+1].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+1];
					regNum1 = lastUsed;
					cycles += 3;
				}
				j = 0;
				isInRegister = false;
				//Check if regNum2 is in register
				while(j < k){
					if(instructions[i+2].equals(registers[j])){
						isInRegister = true;
						regNum2 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+2].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+2];
					regNum2 = lastUsed;
					cycles += 3;
				}
				//Check if regNum3 is in register
				while(j < k){
					if(instructions[i+4].equals(registers[j])){
						isInRegister = true;
						regNum3 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+4].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					registers[lastUsed] = instructions[i+4];
					regNum3 = lastUsed;
					cycles += 3;
				}
				System.out.println("mult r"+(regNum1+1)+", r"+(regNum2+1)+" => r"+(regNum3+1));
				cycles += 2;
				i += 5;
			//Sub
			}else if(instructions[i].equals("sub")){
				int j = 0;
				boolean isInRegister = false;
				//Check if regNum1 is in register
				while(j < k){
					if(instructions[i+1].equals(registers[j])){
						isInRegister = true;
						regNum1 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+1].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+1];
					regNum1 = lastUsed;
					cycles += 3;
				}
				j = 0;
				isInRegister = false;
				//Check if regNum2 is in register
				while(j < k){
					if(instructions[i+2].equals(registers[j])){
						isInRegister = true;
						regNum2 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+2].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+2];
					regNum2 = lastUsed;
					cycles += 3;
				}
				//Check if regNum3 is in register
				while(j < k){
					if(instructions[i+4].equals(registers[j])){
						isInRegister = true;
						regNum3 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+4].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					registers[lastUsed] = instructions[i+4];
					regNum3 = lastUsed;
					cycles += 3;
				}
				System.out.println("sub r"+(regNum1+1)+", r"+(regNum2+1)+" => r"+(regNum3+1));
				cycles += 1;
				i += 5;
			//Output
			}else if(instructions[i].equals("output")){
				System.out.println("output "+instructions[i+1]);
				i += 2;
				cycles += 1;
			//LShift
			}else if(instructions[i].equals("lshift")){
				int j = 0;
				boolean isInRegister = false;
				//Check if regNum1 is in register
				while(j < k){
					if(instructions[i+1].equals(registers[j])){
						isInRegister = true;
						regNum1 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+1].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+1];
					regNum1 = lastUsed;
					cycles += 3;
				}
				j = 0;
				isInRegister = false;
				//Check if regNum2 is in register
				while(j < k){
					if(instructions[i+2].equals(registers[j])){
						isInRegister = true;
						regNum2 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+2].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+2];
					regNum2 = lastUsed;
					cycles += 3;
				}
				//Check if regNum3 is in register
				while(j < k){
					if(instructions[i+4].equals(registers[j])){
						isInRegister = true;
						regNum3 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+4].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					registers[lastUsed] = instructions[i+4];
					regNum3 = lastUsed;
					cycles += 3;
				}
				System.out.println("lshift r"+(regNum1+1)+", r"+(regNum2+1)+" => r"+(regNum3+1));
				cycles += 1;
				i += 5;
			//RShift
			}else if(instructions[i].equals("rshift")){
				int j = 0;
				boolean isInRegister = false;
				//Check if regNum1 is in register
				while(j < k){
					if(instructions[i+1].equals(registers[j])){
						isInRegister = true;
						regNum1 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+1].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+1];
					regNum1 = lastUsed;
					cycles += 3;
				}
				j = 0;
				isInRegister = false;
				//Check if regNum2 is in register
				while(j < k){
					if(instructions[i+2].equals(registers[j])){
						isInRegister = true;
						regNum2 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+2].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					System.out.println("loadAI r0, "+(-4*storedReg-4)+" => r"+(lastUsed+1));
					registers[lastUsed] = instructions[i+2];
					regNum2 = lastUsed;
					cycles += 3;
				}
				//Check if regNum3 is in register
				while(j < k){
					if(instructions[i+4].equals(registers[j])){
						isInRegister = true;
						regNum3 = j;
					}
					j++;
				}
				//Find where register was stored
				if(!isInRegister){
					j = 0;
					while(j < 256){
						if(instructions[i+4].equals(stored[j])){
							storedReg = j;
						}
						j++;
					}
				}
				//Find last used register
				if(!isInRegister){
					j = 0;
					while(j < k){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(registers[j])){
								if(!(n > registersUsedSoon[j])){
									registersUsedSoon[j] = n;
								}								
							}
						}
						j++;
					}
					for(int a = 0;a < k;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}
				}
				//Check if last used register has been stored
				if(!isInRegister){
					boolean isStored = false;
					j = 0;
					while(j < 256){
						if(registers[lastUsed].equals(stored[j])){
							isStored = true;
							break;
						}
						j++;
					}
					if(!isStored){
						j = 0;
						//Spill register to memory
						while(j < 256){
							if(stored[j] == null){
								stored[j] = registers[lastUsed];
								System.out.println("storeAI r"+(lastUsed+1)+" => r0, "+(-4*j-4));
								cycles += 3;
								break;
							}
							j++;
						}
					}
					registers[lastUsed] = instructions[i+4];
					regNum3 = lastUsed;
					cycles += 3;
				}
				System.out.println("rshift r"+(regNum1+1)+", r"+(regNum2+1)+" => r"+(regNum3+1));
				cycles += 1;
				i += 5;
			}
		}				
		//return number of cycles
		return cycles;		
	}
	
	private static int simpleTopDown(int k, String input){
		
		//initialize variable for counting cycles
		int cycles = 0;
				
		//Parse input file
		parser(input);
		
		//Initialize arrays to represent registers
		String[] registers = new String[k-3];
		String[] feasible = new String[3];
		
		//Initialize array to track stored registers in memory
		String[] stored = new String[256];
		
		//Instantiate array to track number of occurrences of registers
		int[] occurrenceCount = new int[256];
		
		//Array to keep track of which registers are used soonest
		int[] registersUsedSoon = new int[feasible.length];
		
		//Count number of occurrences for each register
		for(int a = 0;a < 1000 && instructions[a] != null;a++){
			for(int b = 0;b < 256;b++){
				if(instructions[a].equals("r"+(b+1))){
					occurrenceCount[b]++;
				}
			}
		}
		
		int largest = occurrenceCount[0];
		int largestIndex = 0;
		
		//Insert registers with most occurrences into the registers array
		for(int a = 0;a < registers.length;a++){
			for(int b = 0; b < 256;b++){
				if(occurrenceCount[b] > largest){
					largest = occurrenceCount[b];
					largestIndex = b;
				}
			}
			registers[a] = "r"+(largestIndex+1);
			occurrenceCount[largestIndex] = 0;
			largest = occurrenceCount[largestIndex];
		}
		
		//Read through array of instructions
		int i = 0;
		while(i < 1000 && instructions[i] != null){
			
			//Index of last used register
			int lastUsed = 0;
			
			//Indices of registers in an expression
			int regNum1 = 0;
			int regNum2 = 0;
			int regNum3 = 0;
			
			for(int a = 0;a < registersUsedSoon.length;a++){
				registersUsedSoon[a] = 1000;
			}
			
			//Initialize boolean for checking if a register is allocated
			boolean isInRegister = false;
			
			//Initialize boolean to check if value was stored
			boolean isStored = false;
			
			//LoadI
			if(instructions[i].equals("loadI")){
				if(instructions[i+1].equals("1024") && instructions[i+3].equals("r0")){
					System.out.println("loadI 1024 => r0");
					cycles += 1;
				}else{
					//Check if register is part of assigned register set
					for(int a = 0;a < registers.length;a++){
						if(instructions[i].equals(registers[a])){
							System.out.println("loadI "+instructions[i+1]+" => r"+(a+1));
							cycles += 1;
							isInRegister = true;
						}
					}
					if(!isInRegister){
						//Look for empty register in feasible set
						for(int a = 0; a < feasible.length;a++){
							if(feasible[a] == null){
								feasible[a] = instructions[i+3];
								System.out.println("loadI "+instructions[i+1]+" => r"+(registers.length+a+1));
								cycles += 1;
								isInRegister = true;
								break;
							}
						}
					}
					if(!isInRegister){
						//Find last used register
						for(int a = 0;a < feasible.length;a++){
							for(int n = i;n < 1000 && instructions[n] != null;n++){
								if(instructions[n].equals(feasible[a])){
									if(!(n > registersUsedSoon[a])){
										registersUsedSoon[a] = n;
									}								
								}
							}
						}
						for(int a = 0;a < feasible.length;a++){
							if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
								lastUsed = a;
							}
						}						
						//Check if value was stored
						for(int a = 0;a < 256 && stored[a] != null;a++){
							if(feasible[lastUsed].equals(stored[a])){
								feasible[lastUsed] = instructions[i+3];
								System.out.println("loadI "+instructions[i+1]+" => r"+(registers.length+lastUsed+1));
								cycles += 1;
								isStored = true;
							}
						}
						//Store value if it is not stored
						if(!isStored){
							for(int a = 0;a < 256;a++){
								if(stored[a] == null){
									stored[a] = feasible[lastUsed];
									System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
									cycles += 3;
									feasible[lastUsed] = instructions[i+3];
									System.out.println("loadI "+instructions[i+1]+" => r"+(registers.length+lastUsed+1));
									cycles += 1;
									break;
								}
							}
						}
					}
				}
				i += 4;
			//Load
			}else if(instructions[i].equals("load")){
				//Find register to be loaded
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register that will be loaded into the target
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+3].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+3].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//Check if there is an available register in the feasible set
				if(!isInRegister){
					for(int a = 0;a < 3; a++){
						if(feasible[a] == null){
							feasible[a] = instructions[i+3];
							isInRegister = true;
							regNum2 = registers.length+a+1;
							break;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum2 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+3];
				}
				System.out.println("load r"+regNum1+" => r"+regNum2);
				cycles += 3;
				i += 4;
			//Store
			}else if(instructions[i].equals("store")){
				//Find register to be loaded
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register that will be stored into the target
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+3].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+3].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum2 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+3];
				}
				System.out.println("store r"+regNum1+" => r"+regNum2);
				cycles += 3;
				i += 4;
			//Add
			}else if(instructions[i].equals("add")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("add r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//Sub
			}else if(instructions[i].equals("sub")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("sub r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//Mult
			}else if(instructions[i].equals("mult")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("mult r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 2;
				i += 5;
			//LShift
			}else if(instructions[i].equals("lshift")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("lshift r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//RShift
			}else if(instructions[i].equals("rshift")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("rshift r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//Output
			}else if(instructions[i].equals("output")){
				System.out.println("output "+instructions[i+1]);
				i += 2;
				cycles += 2;
			}
		}
		
		//return number of cycles
		return cycles;
	}
	
	private static int topDown(int k, String input){
		
		//initialize variable for counting cycles
		int cycles = 0;
				
		//Parse input file
		parser(input);
		
		//Initialize array to store first and last occurrences of registers
		int[][] liveRange = new int[2][256];
		for(int a = 0;a < 256;a++){
			liveRange[0][a] = 1000;
			liveRange[1][a] = 0;
		}
		
		//Store first and last occurrences of each register in 2D array
		for(int a = 0;a < 1000 && instructions[a] != null;a++){
			for(int b = 0;b < 256;b++){
				if(instructions[a].equals("r"+(b+1))){
					if(a < liveRange[0][b]){
						liveRange[0][b] = a;
					}
					if(a > liveRange[1][b]){
						liveRange[1][b] = a;
					}
				}
			}
		}
		
		//Initialize array to calculate MAXLIVE
		int[] maxLiveArr = new int[1000];
		
		//Initialize variable to store MAXLIVE
		int maxLive = 0;
		
		//Calculate MAXLIVE
		for(int a = 0;a < 1000;a++){
			for(int b = 0;b < 256;b++){
				if(a > liveRange[0][b] && a <= liveRange[1][b]){
					maxLiveArr[a]++;
				}
			}
		}
		for(int a = 0;a < 1000;a++){
			if(maxLiveArr[a] > maxLive){
				maxLive = maxLiveArr[a];
			}
		}
		
		//Initialize array to store length of live ranges for each register
		int[] liveRangeNum = new int[256];
		for(int a = 0;a < 256;a++){
			liveRangeNum[a] = liveRange[1][a]-liveRange[0][a];
		}
		
		//Initialize arrays to represent registers
		String[] registers = new String[k-2];
		String[] feasible = new String[2];
				
		//Initialize array to track stored registers in memory
		String[] stored = new String[256];
				
		//Instantiate array to track number of occurrences of registers
		int[] occurrenceCount = new int[256];
				
		//Array to keep track of which registers are used soonest
		int[] registersUsedSoon = new int[feasible.length];
				
		//Count number of occurrences for each register
		for(int a = 0;a < 1000 && instructions[a] != null;a++){
			for(int b = 0;b < 256;b++){
				if(instructions[a].equals("r"+(b+1))){
					occurrenceCount[b]++;
				}
			}
		}
				
		int largest = occurrenceCount[0];
		int largestIndex = 0;
				
		//Insert registers with most occurrences into the registers array
		for(int a = 0;a < registers.length;a++){
			for(int b = 0; b < 256;b++){
				if(occurrenceCount[b] > largest){
					largest = occurrenceCount[b];
					largestIndex = b;
				}
			}
			registers[a] = "r"+(largestIndex+1);
			occurrenceCount[largestIndex] = 0;
			largest = occurrenceCount[largestIndex];
		}
		
		//Read through array of instructions
		int i = 0;
		while(i < 1000 && instructions[i] != null){
			
			//Index of last used register
			int lastUsed = 0;
			
			//Indices of registers in an expression
			int regNum1 = 0;
			int regNum2 = 0;
			int regNum3 = 0;
			
			for(int a = 0;a < registersUsedSoon.length;a++){
				registersUsedSoon[a] = 1000;
			}
			
			//Initialize boolean for checking if a register is allocated
			boolean isInRegister = false;
			
			//Initialize boolean to check if value was stored
			boolean isStored = false;
			
			//LoadI
			if(instructions[i].equals("loadI")){
				if(instructions[i+1].equals("1024") && instructions[i+3].equals("r0")){
					System.out.println("loadI 1024 => r0");
					cycles += 1;
				}else{
					//Check if register is part of assigned register set
					for(int a = 0;a < registers.length;a++){
						if(instructions[i].equals(registers[a])){
							System.out.println("loadI "+instructions[i+1]+" => r"+(a+1));
							cycles += 1;
							isInRegister = true;
						}
					}
					if(!isInRegister){
						//Look for empty register in feasible set
						for(int a = 0; a < feasible.length;a++){
							if(feasible[a] == null){
								feasible[a] = instructions[i+3];
								System.out.println("loadI "+instructions[i+1]+" => r"+(registers.length+a+1));
								cycles += 1;
								isInRegister = true;
								break;
							}
						}
					}
					if(!isInRegister){
						//Find last used register
						for(int a = 0;a < feasible.length;a++){
							for(int n = i;n < 1000 && instructions[n] != null;n++){
								if(instructions[n].equals(feasible[a])){
									if(!(n > registersUsedSoon[a])){
										registersUsedSoon[a] = n;
									}								
								}
							}
						}
						for(int a = 0;a < feasible.length;a++){
							if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
								lastUsed = a;
							}
						}						
						//Check if value was stored
						for(int a = 0;a < 256 && stored[a] != null;a++){
							if(feasible[lastUsed].equals(stored[a])){
								feasible[lastUsed] = instructions[i+3];
								System.out.println("loadI "+instructions[i+1]+" => r"+(registers.length+lastUsed+1));
								cycles += 1;
								isStored = true;
							}
						}
						//Store value if it is not stored
						if(!isStored){
							for(int a = 0;a < 256;a++){
								if(stored[a] == null){
									stored[a] = feasible[lastUsed];
									System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
									cycles += 3;
									feasible[lastUsed] = instructions[i+3];
									System.out.println("loadI "+instructions[i+1]+" => r"+(registers.length+lastUsed+1));
									cycles += 1;
									break;
								}
							}
						}
					}
				}
				i += 4;
			//Load
			}else if(instructions[i].equals("load")){
				//Find register to be loaded
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register that will be loaded into the target
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+3].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+3].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//Check if there is an available register in the feasible set
				if(!isInRegister){
					for(int a = 0;a < 2; a++){
						if(feasible[a] == null){
							feasible[a] = instructions[i+3];
							isInRegister = true;
							regNum2 = registers.length+a+1;
							break;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum2 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+3];
				}
				System.out.println("load r"+regNum1+" => r"+regNum2);
				cycles += 3;
				i += 4;
			//Store
			}else if(instructions[i].equals("store")){
				//Find register to be loaded
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register that will be stored into the target
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+3].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+3].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum2 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+3];
				}
				System.out.println("store r"+regNum1+" => r"+regNum2);
				cycles += 3;
				i += 4;
			//Add
			}else if(instructions[i].equals("add")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("add r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//Sub
			}else if(instructions[i].equals("sub")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("sub r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//Mult
			}else if(instructions[i].equals("mult")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("mult r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 2;
				i += 5;
			//LShift
			}else if(instructions[i].equals("lshift")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("lshift r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//RShift
			}else if(instructions[i].equals("rshift")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("rshift r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//Output
			}else if(instructions[i].equals("output")){
				System.out.println("output "+instructions[i+1]);
				i += 2;
				cycles += 2;
			}
		}				
		//return number of cycles
		return cycles;
	}
	
	private static int customTopDown(int k, String input){
		
		//initialize variable for counting cycles
		int cycles = 0;
				
		//Parse input file
		parser(input);
		
		//Initialize array to store first and last occurrences of registers
		int[][] liveRange = new int[2][256];
		for(int a = 0;a < 256;a++){
			liveRange[0][a] = 1000;
			liveRange[1][a] = 0;
		}
		
		//Store first and last occurrences of each register in 2D array
		for(int a = 0;a < 1000 && instructions[a] != null;a++){
			for(int b = 0;b < 256;b++){
				if(instructions[a].equals("r"+(b+1))){
					if(a < liveRange[0][b]){
						liveRange[0][b] = a;
					}
					if(a > liveRange[1][b]){
						liveRange[1][b] = a;
					}
				}
			}
		}
		
		//Initialize array to calculate MAXLIVE
		int[] maxLiveArr = new int[1000];
		
		//Initialize variable to store MAXLIVE
		int maxLive = 0;
		
		//Calculate MAXLIVE
		for(int a = 0;a < 1000;a++){
			for(int b = 0;b < 256;b++){
				if(a > liveRange[0][b] && a <= liveRange[1][b]){
					maxLiveArr[a]++;
				}
			}
		}
		for(int a = 0;a < 1000;a++){
			if(maxLiveArr[a] > maxLive){
				maxLive = maxLiveArr[a];
			}
		}
		
		//Initialize array to store length of live ranges for each register
		int[] liveRangeNum = new int[256];
		for(int a = 0;a < 256;a++){
			liveRangeNum[a] = liveRange[1][a]-liveRange[0][a];
		}
		
		//Initialize arrays to represent registers
		String[] registers = new String[1];
		String[] feasible = new String[k-1];
				
		//Initialize array to track stored registers in memory
		String[] stored = new String[256];
				
		//Instantiate array to track number of occurrences of registers
		int[] occurrenceCount = new int[256];
				
		//Array to keep track of which registers are used soonest
		int[] registersUsedSoon = new int[feasible.length];
				
		//Count number of occurrences for each register
		for(int a = 0;a < 1000 && instructions[a] != null;a++){
			for(int b = 0;b < 256;b++){
				if(instructions[a].equals("r"+(b+1))){
					occurrenceCount[b]++;
				}
			}
		}
				
		int largest = occurrenceCount[0];
		int largestIndex = 0;
				
		//Insert registers with most occurrences into the registers array
		for(int a = 0;a < registers.length;a++){
			for(int b = 0; b < 256;b++){
				if(occurrenceCount[b] > largest){
					largest = occurrenceCount[b];
					largestIndex = b;
				}
			}
			registers[a] = "r"+(largestIndex+1);
			occurrenceCount[largestIndex] = 0;
			largest = occurrenceCount[largestIndex];
		}
		
		//Read through array of instructions
		int i = 0;
		while(i < 1000 && instructions[i] != null){
			
			//Index of last used register
			int lastUsed = 0;
			
			//Indices of registers in an expression
			int regNum1 = 0;
			int regNum2 = 0;
			int regNum3 = 0;
			
			for(int a = 0;a < registersUsedSoon.length;a++){
				registersUsedSoon[a] = 1000;
			}
			
			//Initialize boolean for checking if a register is allocated
			boolean isInRegister = false;
			
			//Initialize boolean to check if value was stored
			boolean isStored = false;
			
			//LoadI
			if(instructions[i].equals("loadI")){
				if(instructions[i+1].equals("1024") && instructions[i+3].equals("r0")){
					System.out.println("loadI 1024 => r0");
					cycles += 1;
				}else{
					//Check if register is part of assigned register set
					for(int a = 0;a < registers.length;a++){
						if(instructions[i].equals(registers[a])){
							System.out.println("loadI "+instructions[i+1]+" => r"+(a+1));
							cycles += 1;
							isInRegister = true;
						}
					}
					if(!isInRegister){
						//Look for empty register in feasible set
						for(int a = 0; a < feasible.length;a++){
							if(feasible[a] == null){
								feasible[a] = instructions[i+3];
								System.out.println("loadI "+instructions[i+1]+" => r"+(registers.length+a+1));
								cycles += 1;
								isInRegister = true;
								break;
							}
						}
					}
					if(!isInRegister){
						//Find last used register
						for(int a = 0;a < feasible.length;a++){
							for(int n = i;n < 1000 && instructions[n] != null;n++){
								if(instructions[n].equals(feasible[a])){
									if(!(n > registersUsedSoon[a])){
										registersUsedSoon[a] = n;
									}								
								}
							}
						}
						for(int a = 0;a < feasible.length;a++){
							if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
								lastUsed = a;
							}
						}						
						//Check if value was stored
						for(int a = 0;a < 256 && stored[a] != null;a++){
							if(feasible[lastUsed].equals(stored[a])){
								feasible[lastUsed] = instructions[i+3];
								System.out.println("loadI "+instructions[i+1]+" => r"+(registers.length+lastUsed+1));
								cycles += 1;
								isStored = true;
							}
						}
						//Store value if it is not stored
						if(!isStored){
							for(int a = 0;a < 256;a++){
								if(stored[a] == null){
									stored[a] = feasible[lastUsed];
									System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
									cycles += 3;
									feasible[lastUsed] = instructions[i+3];
									System.out.println("loadI "+instructions[i+1]+" => r"+(registers.length+lastUsed+1));
									cycles += 1;
									break;
								}
							}
						}
					}
				}
				i += 4;
			//Load
			}else if(instructions[i].equals("load")){
				//Find register to be loaded
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register that will be loaded into the target
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+3].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+3].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//Check if there is an available register in the feasible set
				if(!isInRegister){
					for(int a = 0;a < 2; a++){
						if(feasible[a] == null){
							feasible[a] = instructions[i+3];
							isInRegister = true;
							regNum2 = registers.length+a+1;
							break;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum2 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+3];
				}
				System.out.println("load r"+regNum1+" => r"+regNum2);
				cycles += 3;
				i += 4;
			//Store
			}else if(instructions[i].equals("store")){
				//Find register to be loaded
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register that will be stored into the target
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+3].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+3].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum2 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+3];
				}
				System.out.println("store r"+regNum1+" => r"+regNum2);
				cycles += 3;
				i += 4;
			//Add
			}else if(instructions[i].equals("add")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("add r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//Sub
			}else if(instructions[i].equals("sub")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("sub r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//Mult
			}else if(instructions[i].equals("mult")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("mult r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 2;
				i += 5;
			//LShift
			}else if(instructions[i].equals("lshift")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("lshift r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//RShift
			}else if(instructions[i].equals("rshift")){
				//Find register of first number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+1].equals(registers[a])){
						regNum1 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+1].equals(feasible[a])){
							regNum1 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+1];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum1 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find register of second number
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+2].equals(registers[a])){
						regNum2 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+2].equals(feasible[a])){
							regNum2 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, load from memory
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								break;
							}
						}
					}
					//Load register of first number
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(instructions[i+1].equals(stored[a])){
							feasible[lastUsed] = instructions[i+2];
							System.out.println("loadAI r0, "+(-4*a-4)+" => r"+(registers.length+lastUsed+1));
							regNum2 = registers.length+a+1;
							cycles += 3;
							isInRegister = true;
						}
					}
				}
				isInRegister = false;
				isStored = false;
				//Find target register
				//Check register set
				for(int a = 0;a < registers.length;a++){
					if(instructions[i+4].equals(registers[a])){
						regNum3 = a+1;
						isInRegister = true;
					}
				}
				//Check feasible set
				if(!isInRegister){
					for(int a = 0;a < feasible.length;a++){
						if(instructions[i+4].equals(feasible[a])){
							regNum3 = registers.length+a+1;
							isInRegister = true;
						}
					}
				}
				//If not in register, prepare register
				if(!isInRegister){
					//Find last used register
					for(int a = 0;a < feasible.length;a++){
						for(int n = i;n < 1000 && instructions[n] != null;n++){
							if(instructions[n].equals(feasible[a])){
								if(!(n > registersUsedSoon[a])){
									registersUsedSoon[a] = n;
								}								
							}
						}
					}
					for(int a = 0;a < feasible.length;a++){
						if(registersUsedSoon[a] > registersUsedSoon[lastUsed]){
							lastUsed = a;
						}
					}						
					//Check if value was stored
					for(int a = 0;a < 256 && stored[a] != null;a++){
						if(feasible[lastUsed].equals(stored[a])){
							isStored = true;
						}
					}
					//Store value if it is not stored
					if(!isStored){
						for(int a = 0;a < 256;a++){
							if(stored[a] == null){
								stored[a] = feasible[lastUsed];
								System.out.println("storeAI r"+(registers.length+lastUsed+1)+" => r0, "+(-4*a-4));
								cycles += 3;
								isStored = true;
								regNum3 = registers.length+lastUsed+1;
								break;
							}
						}
					}
					feasible[lastUsed] = instructions[i+4];
				}
				System.out.println("rshift r"+regNum1+", r"+regNum2+" => r"+regNum3);
				cycles += 1;
				i += 5;
			//Output
			}else if(instructions[i].equals("output")){
				System.out.println("output "+instructions[i+1]);
				i += 2;
				cycles += 1;
			}
		}				
		//return number of cycles
		return cycles;
	}
	
	private static void parser(String input){
		
		//initialize variable for indexing instructions array
		int i = 0;
		
		try{
			//Initialize file reader with input file
			Scanner scanner = new Scanner(new File(input));
			
			//Read file and store tokens in array
			while(scanner.hasNext()){
				instructions[i] = scanner.next();
				if(instructions[i].equals("//")){
					scanner.nextLine();
				}else{
					instructions[i] = instructions[i].replace(",", "");
					i++;
				}
			}
			
			//Close file
			scanner.close();
		}catch(FileNotFoundException FNFE){
			System.out.println("File Not Found!");
		}
	}
}
