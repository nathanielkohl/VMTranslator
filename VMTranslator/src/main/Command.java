package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Command {
	public Command(String currentCommand) {
		this.command = currentCommand;
		this.tokenize();
	}
	
	public String getCommand() {
		return command;
	}
	
	public CommandType commandType() {
		return dict.get(commandTokens[0]);
	}
	
	public String arg1() throws Exception {
		switch(commandType()) {
		case C_RETURN:
			throw new Exception("Cannot get first argument of a C_RETURN command.");
		case C_ARITHMETIC:
			return commandTokens[0];
		default:
			return commandTokens[1];
		}
	}
	
	public String arg2() throws Exception {
		switch(commandType()) {
		case C_PUSH:
		case C_POP:
		case C_FUNCTION:
		case C_CALL:
			return commandTokens[2];
		default:
			throw new Exception("Can only get second argument of C_PUSH, C_POP, C_FUNCTION, or C_CALL commands.");
		}
	}
	
	public String translateCommand() throws Exception {
		StringBuilder command = new StringBuilder();
		command.append("// " + this.command + "\n");
		
		switch(commandType()) {
		case C_ARITHMETIC:
			return translateArithmetic(command);
		case C_PUSH:
			return translatePush(command);
		case C_POP:
			return translatePop(command);
		default:
			throw new Exception("Does not recognize command, cannot translate.");
		}
	}
	
	private String translatePush(StringBuilder machineCode) throws Exception {

		if (arg1().equals("constant")) {
			machineCode.append("@" + arg2() + "\n");
			machineCode.append("D=A\n");
		} else {
			machineCode.append("@" + register.get(arg1()) + "\n");
			machineCode.append("D=M\n");
			machineCode.append("@" + arg2() + "\n");
			machineCode.append("A=D+A\n");
			machineCode.append("D=M\n");
		}
		machineCode.append("@SP\n");
		machineCode.append("A=M\n");
		machineCode.append("M=D\n");
		machineCode.append("@SP\n");
		machineCode.append("M=M+1\n");
		
		return machineCode.toString();
	}
	
	private String translatePop(StringBuilder machineCode) throws Exception {
		
		if (arg1().equals("constant")) throw new Exception("Cannot pop to constant");
		
		machineCode.append("@SP\n");
		machineCode.append("M=M-1\n");
		machineCode.append("A=M\n");
		machineCode.append("D=M\n");
		machineCode.append("@R13\n");
		machineCode.append("M=D\n");
		machineCode.append("@" + register.get(arg1()) + "\n");
		machineCode.append("D=M\n");
		machineCode.append("@" + arg2() + "\n");
		machineCode.append("D=D+A\n");
		machineCode.append("@R14\n");
		machineCode.append("M=D\n");
		machineCode.append("@R13\n");
		machineCode.append("D=M\n");
		machineCode.append("@R14\n");
		machineCode.append("A=M\n");
		machineCode.append("M=D\n");
		
		return machineCode.toString();
	}
	
	private String translateArithmetic(StringBuilder machineCode) throws Exception {
		
		machineCode.append("@SP\n");
		machineCode.append("M=M-1\n");
		machineCode.append("A=M\n");
		machineCode.append("D=M\n");
		machineCode.append("@SP\n");
		machineCode.append("M=M-1\n");
		machineCode.append("A=M\n");
		machineCode.append("D=M" + (arg1().equals("add") ? "+" : "-") + "D\n");
		machineCode.append("@SP\n");
		machineCode.append("A=M\n");
		machineCode.append("M=D\n");
		machineCode.append("@SP\n");
		machineCode.append("M=M+1\n");
		
		return machineCode.toString();
	}
	
	public boolean isEmpty() {
		return commandTokens.length == 0;
	}
	
	private void tokenize() {
		command = command.trim();
		commandTokens = command.split("\s");
	}
	
	private HashMap<String, CommandType> dict = new HashMap<String, CommandType>() {{
		put("push", CommandType.C_PUSH);
		put("pop", CommandType.C_POP);
		put("add", CommandType.C_ARITHMETIC);
		put("sub", CommandType.C_ARITHMETIC); 
		put("neg", CommandType.C_ARITHMETIC); 
		put("eq", CommandType.C_IF);
		put("gt", CommandType.C_IF);
		put("lt", CommandType.C_IF);
		put("and", CommandType.C_IF);
		put("or", CommandType.C_IF);
		put("not", CommandType.C_IF);
		put("//", CommandType.COMMENT);
	}};
	
	private HashMap<String, String> register = new HashMap<String, String>() {{
		put("local", "LCL");
		put("this", "THIS");
		put("that", "THAT");
		put("argument", "ARG");
		put("temp", "R15");
	}};
	
	private String[] commandTokens;
	private String command;
}
