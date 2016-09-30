package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class DeleteCommand extends ServerCommand {

	@Override
	public void run() throws IOException, ServerException {
		/*
		delete\n
		<name>\n
		*/
		String name = StreamUtil.readLine(inputStream);		
		/*
		<response code>\n
		*/				
		try{
			FileUtil. deleteData(name);		
			String lines = "ok\n";
			StreamUtil.writeLine(lines, outputStream);	
		} catch(ServerException e){
			StreamUtil.writeLine(e.getMessage(), outputStream);
		}
	}

}
