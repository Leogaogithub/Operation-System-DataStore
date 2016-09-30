package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class ReadCommand extends ServerCommand {

	@Override
	public void run() throws IOException, ServerException {			
		/*
		read\n
		<name>\n
		*/
		String name = StreamUtil.readLine(inputStream);		
		/*
		<responseCode>\n
		<data size in ASCII bytes>\n
		<N bytes of binary data>
		*/				
		byte data[] = FileUtil. readData(name);		
		String lines = "ok\n"+data.length+"\n";
		StreamUtil.writeLine(lines, outputStream);	
		StreamUtil.writeData(data, outputStream);	
	}

}
