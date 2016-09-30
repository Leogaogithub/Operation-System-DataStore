package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class WriteCommand extends ServerCommand {		
	
	@Override
	public void run() throws IOException, ServerException {
		/*
		 write\n
		<name>\n
		<data size in ANSII>\n
		<N Bytes of binary data>
		*/
		try{
			String name = StreamUtil.readLine(inputStream);
			String dataSize = StreamUtil.readLine(inputStream);
			int length = Integer.parseInt(dataSize);
			byte data[] = StreamUtil.readData(length, inputStream);
			FileUtil.writeData(name, data);		
			//<response code>\n		
		} catch (IOException e){
			StreamUtil.writeLine("the socket closes before N bytes is read from the client", outputStream);
		}
		String line = "ok";
		StreamUtil.writeLine(line, outputStream);
	}

}
