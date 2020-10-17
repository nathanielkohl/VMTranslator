

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
			
		} else if ("call".contains(commandTokens[0])) {
			return translateCall(command);
			
		} else if ("function".contains(commandTokens[0])) {
			return translateFunction(command);
		
		} else if ("return".contains(commandTokens[0])) {
			return translateReturn(command);
			
		} else if ("label".contains(commandTokens[0])) {
			return translateLabel(command);
			
		} else if ("goto".contains(commandTokens[0])) {
			return translateGoto(command);
			
		} else if ("if-goto".contains(commandTokens[0])) {
			return translateIf(command);
			
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
	
	private String translateCall(StringBuilder sb) throws Exception {
		String functionName = arg1();
		return translateCall(sb, functionName);
	}
	
	private String translateCall(StringBuilder sb, String functionName) throws Exception {
		writeCall(sb, functionName);
		
		return sb.toString();
	}
	
	private void writeCall(StringBuilder sb, String functionName) throws Exception {
		String returnAddress = "return" + labelNum++;
		int nArgs = arg2();
		
		// push return address
		sb.append("@" + returnAddress + "\n");
		sb.append("D=A\n");
		sb.append("@SP\n");
		sb.append("M=M+1\n");
		sb.append("A=M-1\n");
		sb.append("M=D\n");
		
		// push LCL
		writePushContents(sb, "LCL");
		// push ARG
		writePushContents(sb, "ARG");
		// push THIS
		writePushContents(sb, "THIS");
		// push THAT
		writePushContents(sb, "THAT");
		
		// ARG = SP-n-5
		sb.append("@SP\n");
		sb.append("D=M\n");
		sb.append("@" + (nArgs+5) + "\n");
		sb.append("D=D-A\n");
		sb.append("@ARG\n");
		sb.append("M=D\n");
		
		// LCL = SP
		sb.append("@SP\n");
		sb.append("D=M\n");
		sb.append("@LCL\n");
		sb.append("M=D\n");
		
		// goto function
		writeGoto(sb, functionName);

		// (return address)
		sb.append("(" + returnAddress + ")\n");
	}
	
	private void writeFunction(StringBuilder sb) throws Exception {
		String functionName = arg1();
		int localVars = arg2();
		
		sb.append("(" + functionName + ")\n");
		
		for (int i = 0; i < localVars; i++) {
			writePushVal(sb, "0");
		}
	}
	
	private String translateFunction(StringBuilder sb) throws Exception {
		writeFunction(sb);
		return sb.toString();
	}
	
	private void writePushVal(StringBuilder sb, String value) throws Exception {
		sb.append("@" + value + "\n");
		sb.append("D=A\n");
		sb.append("@SP\n");
		sb.append("M=M+1\n");
		sb.append("A=M-1\n");
		sb.append("M=D\n");
	}
	
	private String translateReturn(StringBuilder sb) throws Exception {
		writeReturn(sb);
		return sb.toString();
	}

	private void restorePointer(StringBuilder sb, int offset, String pointer) {
		sb.append("@" + offset + "\n");
		sb.append("D=A\n");
		sb.append("@R13\n");
		sb.append("A=M-D\n");
		sb.append("D=M\n");
		sb.append("@" + pointer + "\n");
		sb.append("M=D\n");
	}
	
	private void writeReturn(StringBuilder sb) {
		// FRAME = LCL
		sb.append("@LCL\n");
		sb.append("D=M\n");
		sb.append("@R13\n");
		sb.append("M=D\n");
		
		// RET = *(FRAME-5)
		sb.append("@5\n");
		sb.append("A=D-A\n");
		sb.append("D=M\n");
		sb.append("@R14\n");
		sb.append("M=D\n");
		
		// *ARG = pop()
		sb.append("@SP\n");
		sb.append("AM=M-1\n");
		sb.append("D=M\n");
		sb.append("@ARG\n");
		sb.append("A=M\n");
		sb.append("M=D\n");
		
		// SP = ARG + 1
		sb.append("@ARG\n");
		sb.append("D=M+1\n");
		sb.append("@SP\n");
		sb.append("M=D\n");
		
		// THAT = *(FRAME-1)
		restorePointer(sb, 1, "THAT");
		// THIS = *(FRAME-2)
		restorePointer(sb, 2, "THIS");
		// ARG = *(FRAME-3)
		restorePointer(sb, 3, "ARG");
		// LCL = *(FRAME-4)
		restorePointer(sb, 4, "LCL");
		
		// goto RET
		sb.append("@R14\n");
		sb.append("A=M\n");
		sb.append("0;JMP\n");
	}
	
	public boolean isEmpty() {
		return commandTokens.length == 0;
	}
	
	private void tokenize() {
		command = command.trim();
		commandTokens = command.split("\\s");
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
	
	private String translateLabel(StringBuilder sb) throws Exception {
		writeLabel(sb);
		return sb.toString();
	}
	
	private String writeLabel(StringBuilder sb) throws Exception{
		String label = arg1();
		
		sb.append("(" + label + ")\n");
		
		return sb.toString();
	}
	
	private String translateGoto(StringBuilder sb) throws Exception {
		String label = arg1();
		return translateGoto(sb, label);
	}
	
	private String translateGoto(StringBuilder sb, String label) throws Exception {
		writeGoto(sb, label);
		return sb.toString();
	}
	
	private void writeGoto(StringBuilder sb, String label) throws Exception {
		sb.append("@" + label + "\n");
		sb.append("0;JMP\n");
	}
	
	private String translateIf(StringBuilder sb) throws Exception {
		writeIf(sb);
		return sb.toString();
	}
	
	private String writeIf(StringBuilder sb) throws Exception {
		String label = arg1();
		
		sb.append("@SP\n");
		sb.append("M=M-1\n");
		sb.append("A=M\n");
		sb.append("D=M\n");
		sb.append("@" + label + "\n");
		sb.append("D;JNE\n");
		
		return sb.toString();
	}
	
	private void writePushContents(StringBuilder sb, String target) throws Exception {
		sb.append("@" + target + "\n");
		sb.append("D=M\n");
		sb.append("@SP\n");
		sb.append("M=M+1\n");
		sb.append("A=M-1\n");
		sb.append("M=D\n");
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
		put("label", CommandType.C_LABEL);
		put("goto", CommandType.C_GOTO);
		put("if-goto", CommandType.C_IFGOTO);
		put("function", CommandType.C_FUNCTION);
		put("call", CommandType.C_CALL);
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
