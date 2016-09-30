package utd.persistentDataStore.datastoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import utd.persistentDataStore.utils.StreamUtil;

public class DatastoreClientImpl implements DatastoreClient
{
	private static Logger logger = Logger.getLogger(DatastoreClientImpl.class);

	private InetAddress address;
	private int port;
	InputStream inputStream ;
	OutputStream outputStream ;
	Socket socket;

	public DatastoreClientImpl(InetAddress address, int port)
	{
		this.address = address;
		this.port = port;
	}

	private  void  configureClient() throws IOException{
		logger.debug("Opening Socket");
		socket = new Socket();
		SocketAddress saddr = new InetSocketAddress(address, port);
		socket.connect(saddr);
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();				
	}
	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#write(java.lang.String, byte[])
	 */
	public void write(String name, byte data[]) throws ClientException
	{
		logger.debug("Executing Write Operation");
		try {
			configureClient();
			/*
			 write\n
			<name>\n
			<data size in ANSII>\n
			<N Bytes of binary data>
			 */
			logger.debug("Writing Message");
			StreamUtil.writeLine("write\n", outputStream);
			StreamUtil.writeLine(name, outputStream);
			String dataSize = ""+data.length;
			StreamUtil.writeLine(dataSize, outputStream);
			StreamUtil.writeData(data, outputStream);			
			/*
			 <response code>\n
			 */
			logger.debug("Reading Response");
			String result = StreamUtil.readLine(inputStream);
			logger.debug("Response:\n" + result);	
			socket.close();			
			logger.debug("Closing Socket");
		}
		catch (IOException ex) {
			throw new ClientException(ex.getMessage(), ex);
		}
	}

	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#read(java.lang.String)
	 */
	public byte[] read(String name) throws ClientException
	{
		byte[] data = null;
		logger.debug("Executing Read Operation");
		try {
			configureClient();			
			/*
				 read\n
				<name>\n
			 */
			logger.debug("Writing Message");
			StreamUtil.writeLine("read\n", outputStream);
			StreamUtil.writeLine(name, outputStream);			
			/*
			<responseCode>\n
			<data size in ASCII bytes>\n
			<N bytes of binary data>
			*/
			logger.debug("Reading Response");
			String responseCode = StreamUtil.readLine(inputStream);			
			logger.debug("Response:\n" + responseCode);	
			if(responseCode.equalsIgnoreCase("ok")){
				String dataSize = StreamUtil.readLine(inputStream);	
				data = StreamUtil.readData(Integer.parseInt(dataSize), inputStream);
				System.out.println(dataSize);
				for (int i = 0; i < data.length; i++) {
					System.out.print(data[i]+" ");
				}
			}else{
				throw new ClientException(responseCode);
			}
			System.out.println();
			socket.close();			
			logger.debug("Closing Socket");
		}
		catch (IOException ex) {
			throw new ClientException(ex.getMessage(), ex);
			
		}
		return data;
	}

	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#delete(java.lang.String)
	 */
	public void delete(String name) throws ClientException
	{
		logger.debug("Executing Delete Operation");
		try {
			configureClient();			
			/*
			 delete\n
			<name>\n
			 */
			logger.debug("Writing Message");
			StreamUtil.writeLine("delete\n", outputStream);
			StreamUtil.writeLine(name, outputStream);			
			/*
			 <response code>\n
			 */
			logger.debug("Reading Response");
			String result = StreamUtil.readLine(inputStream);
			if(result.equalsIgnoreCase("ok")){
				logger.debug("Response " + result);	
				System.out.println(result);
			}else{
				throw new ClientException(result);
			}
			
			socket.close();			
			logger.debug("Closing Socket");
		}
		catch (IOException ex) {
			throw new ClientException(ex.getMessage(), ex);
		}
	}

	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#directory()
	 */
	public List<String> directory() throws ClientException
	{
		List<String> directoryList = new ArrayList<String>();
		logger.debug("Executing Directory Operation");
		try {
			configureClient();		
			/*
			directory\n
			 */
			logger.debug("Writing Message");
			StreamUtil.writeLine("directory\n", outputStream);			
			/*
			 ok\n
			<number of file names as ASCII integer>\n
			<name1>\n
			<name2>\n
			etc. 
			 */
			logger.debug("Reading Response");
			String result = StreamUtil.readLine(inputStream);
			logger.debug("Response:\n" + result);	
			if(result.equalsIgnoreCase("ok")){
				String size = StreamUtil.readLine(inputStream);
				int length = Integer.parseInt(size);
				//System.out.println(length);
				for(int i = 0; i < length; i++){
					String fileName = StreamUtil.readLine(inputStream);
					//System.out.println(fileName);
					directoryList.add(fileName);
				}
			}
			
			socket.close();			
			logger.debug("Closing Socket");
		}
		catch (IOException ex) {
			throw new ClientException(ex.getMessage(), ex);
		}
		return directoryList;
	}
	
//	public static void main(String args[]) throws ClientException, IOException{
//		byte byteAddr[] = {(byte)10, (byte)21, 63, 103}; // Server Address		
//		try {
//			InetAddress address;
//			//address = InetAddress.getByName("10.176.65.156");
//			//address = InetAddress.getLocalHost();
//			//address = InetAddress.getByName("ec2-52-35-97-192.us-west-2.compute.amazonaws.com");
//			address = InetAddress.getByAddress(byteAddr);
//			DatastoreClientImpl client = new DatastoreClientImpl(address, 10023);
//			//client.configureClient();
//			byte data[] = generateData(10);
//			String name = "clientdata2.txt";
//			System.out.println("write ***************************");
//			client.write(name, data);
//			System.out.println("read ***************************");
//			client.read(name);
//			System.out.println("directory ***************************");
//			client.directory();
//			System.out.println("detele ***************************");
//			client.delete(name);
//			
//		} catch (UnknownHostException e) {			
//			e.printStackTrace();
//		}
//	}
//	
//	private static byte[] generateData(int size)
//	{
//		byte data[] = new byte[size];
//		Random random = new Random();
//		random.nextBytes(data);
//		return data;
//	}
}
