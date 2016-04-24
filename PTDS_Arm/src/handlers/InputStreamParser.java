package handlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class InputStreamParser {
	InputStream inputStream;
	StringBuffer stringBuffer;
	
	/**
	 * Default constructor. Set up the parser to obtain a response from the connection
	 * @param ch - The connection handler
	 * @throws IOException 
	 */
	public InputStreamParser(HttpURLConnection connection) throws IOException {
		inputStream = connection.getInputStream();
		stringBuffer = new StringBuffer();
	}
	
	/**
	 * Reads the input stream and returns a string buffer with the contents of
	 * the stream
	 * @return - String Buffer of input stream content
	 * @throws IOException
	 */
	public StringBuffer getResponseString() throws IOException {
		int ch;
		while ((ch = inputStream.read()) != -1) {
			stringBuffer.append((char) ch);
		}
		
		return stringBuffer;
	}
}
