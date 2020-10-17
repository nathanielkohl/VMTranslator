

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VMTranslator {
	
	private static void writePushContents(StringBuilder sb, String target) throws Exception {
		sb.append("@" + target + "\n");
		sb.append("D=M\n");
		sb.append("@SP\n");
		sb.append("M=M+1\n");
		sb.append("A=M-1\n");
		sb.append("M=D\n");
	}
	
	private static void writeGoto(StringBuilder sb, String label) throws Exception {
		sb.append("@" + label + "\n");
		sb.append("0;JMP\n");
	}
	
	private static void writeCall(StringBuilder sb, String functionName) throws Exception {
		String returnAddress = "returnInit";
		int nArgs = 0;
		
		// push return address
		writePushContents(sb, returnAddress);
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
	
	// First add the init commands
	private static String writeInit() throws Exception {
		StringBuilder sb = new StringBuilder();
		
		// SP=256
		sb.append("@256\n");
		sb.append("D=A\n");
		sb.append("@SP\n");
		sb.append("M=D\n");
		
		// call Sys.init
		writeCall(sb, "Sys.init");
		
		return sb.toString();
	}
	
	private static void writeFile(File file, CodeWriter cw) throws IOException, Exception {
		System.out.println("writeFile method...");
		
		Parser p = new Parser(file);
		
		ArrayList<String> commands = new ArrayList<String>();
		
		while (p.hasMoreCommands()) {
			Command c = p.getCommand();
			commands.add(c.translateCommand());
		}
		
		for (String command : commands) {
			cw.writeCommand(command);
		}
	}
	
	
	
	public static void main(String[] args) throws Exception {
		File file = new File(args[0]);
		System.out.println("file name: " + file.getName());
		
		if (file.isDirectory()) {
			System.out.println("file is directory");
			
			CodeWriter cw = new CodeWriter(file);
			cw.writeCommand(writeInit());
			
			for (File subFile : file.listFiles()) {
				System.out.println("working on subfile: " + subFile.getName());
				
				if (subFile.getName().contains(".vm")) writeFile(subFile, cw);
			}
			cw.close();
			
		} else {
			CodeWriter cw = new CodeWriter(file);

			writeFile(file, cw);
			cw.close();
		}
	}
}
