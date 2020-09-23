package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VMTranslator {
	public static void main(String[] args) throws Exception {
		File file = new File(args[0]);
		Parser p = new Parser(file);
		
		ArrayList<String> commands = new ArrayList<String>();
		
		while (p.hasMoreCommands()) {
			Command c = p.getCommand();
			commands.add(c.translateCommand());
		}
		
		CodeWriter cw = new CodeWriter(file);
		
		for (String command : commands) {
			cw.writeCommand(command);
		}
		cw.close();
	}
}
