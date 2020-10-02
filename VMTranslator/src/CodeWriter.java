

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
	public CodeWriter(File infile) throws IOException {
		
		String filename = infile.getName().split("\\.")[0] + ".asm";
		File outfile = new File(infile.getAbsoluteFile().getParent() + File.separator + filename); 
		bw = new BufferedWriter(new FileWriter(outfile));
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
