package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Command {
	public Command(String currentCommand, File file) {
		this.file = file;
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
		case C_IF:
			return commandTokens[0];
		default:
			return commandTokens[1];
		}
	}
	
	public int arg2() throws Exception {
		switch(commandType()) {
		case C_PUSH:
		case C_POP:
		case C_FUNCTION:
		case C_CALL:
			return Integer.parseInt(commandTokens[2]);
		default:
			throw new Exception("Can only get second argument of C_PUSH, C_POP, C_FUNCTION, or C_CALL commands.");
		}
	}
	
	public String translateCommand() throws Exception {
		StringBuilder command = new StringBuilder();
		command.append("// " + this.command + "\n");
		
		if ("add sub".contains(commandTokens[0])) {
			return translateArithmetic(command);
			
		} else if ("neg not".contains(commandTokens[0])) {
			return translateUnary(command);
			
		} else if ("and or".contains(commandTokens[0])) {
			return translateLogical(command);
			
		} else if ("eq lt gt".contains(commandTokens[0])) {
			return translateCompare(command);
			
		} else if ("push".contains(commandTokens[0])) {
			return translatePush(command);

		} else if ("pop".contains(commandTokens[0])) {
			return translatePop(command);
		
		} else {
			throw new Exception("Following command preface not able to be translated: " + arg1());
		}
	}
	
	private String translateArithmetic(StringBuilder sb) throws Exception {
		
		sb.append("@SP\n");
		sb.append("AM=M-1\n");
		sb.append("D=M\n");
		sb.append("@SP\n");
		sb.append("AM=M-1\n");
		// need to decide whether to add or subtract
		sb.append("M=M" + symbols.get(arg1()) + "D\n");
		sb.append("@SP\n");
		sb.append("M=M+1\n");
		
		return sb.toString();
	}
	
	private String translateUnary(StringBuilder sb) throws Exception {
		
		sb.append("@SP\n");
		sb.append("A=M-1\n");
		sb.append("M=" + symbols.get(arg1()) + "M\n");
		
		return sb.toString();
	}
	
	private String translateLogical(StringBuilder sb) throws Exception {
		
		sb.append("@SP\n");
		sb.append("M=M-1\n");
		sb.append("A=M-1\n");
		sb.append("D=M\n");
		sb.append("A=A+1\n");
		sb.append("D=D" + symbols.get(arg1()) + "M\n");
		sb.append("A=A-1\n");
		sb.append("M=D\n");
		
		return sb.toString();
	}
	
	private String translateCompare(StringBuilder sb) throws Exception {
		
		sb.append("@SP\n");
		sb.append("M=M-1\n");
		sb.append("A=M-1\n");
		sb.append("D=M\n");
		sb.append("A=A+1\n");
		sb.append("D=D-M\n");
		sb.append("@TRUE" + labelNum + "\n");
		sb.append("D;" + symbols.get(arg1()) + "\n");
		sb.append("@0\n");
		sb.append("D=A\n");
		sb.append("@EXIT" + labelNum + "\n");
		sb.append("0;JMP\n");
		sb.append("(TRUE" + labelNum + ")\n");
		sb.append("@1\n");
		sb.append("D=-A\n");
		sb.append("(EXIT" + labelNum++ + ")\n");
		sb.append("@SP\n");
		sb.append("A=M-1\n");
		sb.append("M=D\n");
		 
		return sb.toString();
	}
	
	private String translatePush(StringBuilder sb) throws Exception {

		getAddress(sb);
		
		if (!"constant".contains(arg1())) {
			sb.append("A=M\n");
			sb.append("D=M\n");
		}
		sb.append("@SP\n");
		sb.append("M=M+1\n");
		sb.append("A=M-1\n");
		sb.append("M=D\n");
		
		return sb.toString();
	}
	
	private String translatePop(StringBuilder sb) throws Exception {
		
		if (arg1().equals("constant")) throw new Exception("Cannot pop to constant");
		
		getAddress(sb);
		
		sb.append("@SP\n");
		sb.append("AM=M-1\n");
		sb.append("D=M\n");
		sb.append("@R13\n");
		sb.append("A=M\n");
		sb.append("M=D\n");

		return sb.toString();
	}
	

	
	public boolean isEmpty() {
		return commandTokens.length == 0;
	}
	
	private void tokenize() {
		command = command.trim();
		commandTokens = command.split("\\s");
	}
	
	private String getPointer() throws Exception {
		return arg2() == 0 ? "THIS" : "THAT";
	}
	
	private void getAddress(StringBuilder sb) throws Exception {
		if ("local argument this that".contains(arg1())) {
			sb.append("@" + registerName.get(arg1()) + "\n");
			sb.append("D=M\n");
			sb.append("@" + arg2() + "\n");
			sb.append("D=D+A\n");
			sb.append("@R13\n");
			sb.append("M=D\n");
		} else if (arg1().equals("temp")) {
			sb.append("@" + (5 + arg2()) + "\n");
			sb.append("D=A\n");
			sb.append("@R13\n");
			sb.append("M=D\n");
		} else if (arg1().equals("constant")) {
			sb.append("@" + arg2() + "\n");
			sb.append("D=A\n");
		} else if (arg1().equals("pointer")) {
			sb.append("@" + (arg2()==0 ? "THIS" : "THAT") + "\n");
			sb.append("D=A\n");
			sb.append("@R13\n");
			sb.append("M=D\n");
		} else if (arg1().equals("static")) {
			sb.append("@" + file.getName().split("\\.")[0] + "." + arg2() + "\n");
			sb.append("D=A\n");
			sb.append("@R13\n");
			sb.append("M=D\n");
		}
	}
	
	private HashMap<String, Object> symbols = new HashMap<String, Object>() {{
		put("add", "+");
		put("sub", "-");
		put("neg", "-");
		put("not", "!");
		put("and", "&");
		put("or",  "|");
		put("eq",  "JEQ");
		put("lt",  "JLT");
		put("gt",  "JGT");
	}};
	
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
	
	private HashMap<String, String> registerName = new HashMap<String, String>() {{
		put("local", "LCL");
		put("argument", "ARG");
		put("this", "THIS");
		put("that", "THAT");
	}};
	
	private String[] commandTokens;
	private String command;
	private File file;
	private static int labelNum = 0;
}
