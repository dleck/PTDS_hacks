package handlers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectionHandler {
	URL url;
	HttpURLConnection connection;
	String urlString;
	
	/**
	 * Default constructor. Takes a string as as url to parse the connection
	 * @param url - The url used for curl operations
	 */
	public ConnectionHandler(String URL, String operation, int mode) {
		try {
			urlString = URL;
			url = new URL(URL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			if (mode == 0)
				connection.setRequestProperty("Content-Type", "application/json");
			else if (mode == 1) {
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("X-Api-Key", "86224d817ec4b6913d5f607b461d1d9b");
			}
			connection.setRequestMethod(operation);
		} catch (MalformedURLException e) {
			// Bad URL BRO
			e.printStackTrace();
		} catch (IOException e) {
			// Can't connect BRO
			e.printStackTrace();
		}
	}
	
	/**
	 * Setup a connection to a specified URL
	 * @param newConnection - The new URL to connect to
	 * @param operation - The operation to perform (PUSH/GET)
	 * @throws IOException
	 */
	public void setConnection(String newConnection, String operation) throws IOException {
		URL temp = new URL(newConnection);
		connection = (HttpURLConnection)temp.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod(operation);
	}
	
	/** Getters for the members of this helper class **/
	public HttpURLConnection getConnection() { return connection; }
	public String getURLString() { return urlString; }
}
