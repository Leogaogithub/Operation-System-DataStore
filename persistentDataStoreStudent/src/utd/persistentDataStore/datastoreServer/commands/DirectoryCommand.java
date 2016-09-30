package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;
import java.util.List;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class DirectoryCommand extends ServerCommand {

	@Override
	public void run() throws IOException, ServerException {
		/*
		directory\n		
		*/
			
		/*
		ok\n
		<number of file names as ASCII integer>\n
		<name1>\n
		<name2>\n
		etc. 
		*/					
		List<String> fileList = FileUtil.directory();		
		String lines = "ok\n" + fileList.size();	
		StreamUtil.writeLine(lines, outputStream);
		for(String fileName : fileList){
			StreamUtil.writeLine(fileName, outputStream);
		}		
	}
}
