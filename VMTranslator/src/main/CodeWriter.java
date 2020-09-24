package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
	public CodeWriter(File infile) throws IOException {
		String filename = infile.getName().split("\\.")[0] + ".asm";
		File outfile = new File(filename); 
		bw = new BufferedWriter(new FileWriter(outfile));
//		bw.write("// set the temp space pointer\n");
//		bw.write("@5\n");
//		bw.write("D=A\n");
//		bw.write("@R15\n");
//		bw.write("M=D\n");
	}
	
	public void writeCommand(String command) throws IOException {
		bw.write(command);
	}
	
	public void close() throws IOException {
		if (bw!=null) {
			bw.close();
		}
	}
	
	private BufferedWriter bw;
}
