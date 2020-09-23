package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
	public Parser(File file) throws IOException {
		br = new BufferedReader(new FileReader(file));
	}
		
	public boolean hasMoreCommands() throws IOException {
		String temp = br.readLine();
		
		if (temp == null) return false;
		
		if (temp.isEmpty() || temp.trim().startsWith("//"))
			 return hasMoreCommands();

		currentCommand = new Command(temp);
		return !(currentCommand==null || currentCommand.isEmpty());
	}
	
	public Command getCommand() {
		return currentCommand;
	}
		
	private BufferedReader br;
	private Command currentCommand;
}
