package main;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import handlers.ConnectionHandler;
import handlers.DeliveryHandler;
import handlers.InputStreamParser;
import objects.RateObject;
import objects.ShipmentObject;

public class PTDS_ArmTester {
	// Connection handler to process http requests
	static ConnectionHandler chandler;
	// Objects
	static RateObject rate;
	static ShipmentObject shipment;
	// JSON Object for updating. Only functionality needed is "delivered"
	static JSONObject shipmentStatus;
	static JSONObject trackingStatus = new JSONObject();
	// Strings to track the ID's of rates and shipments
	static String rateID;
	public static String shipmentID;
	
	// Hard-coded URLS for connections
	static String rateURL = "https://sandbox.shiphawk.com/api/v4/rates?api_key=86224d817ec4b6913d5f607b461d1d9b";
	static String shipmentURL = "https://sandbox.shiphawk.com/api/v4/shipments?api_key=86224d817ec4b6913d5f607b461d1d9b";
	static String updateURL = "https://sandbox.shiphawk.com/api/v4/shipments/";
	
	public static void main(String[] args) throws IOException {
		boolean running = true;
		
		userPrompt(running);
	}
	
	/**
	 * All user prompts. Modularized method utilizing a switch statement to guide the
	 * user through specific, predetermined commands.
	 * @param running - While the application is running. Will be modified.
	 * @throws IOException - IOException when failed to clear screen.
	 */
	@SuppressWarnings("resource")
	private static void userPrompt(boolean running) throws IOException {
		// Scanner for reading user input
		Scanner scanner = new Scanner(System.in);
		// Console header
		System.out.println("Package Theft Deterrent System" + "\n");
		
		// While the program is running
		while (running) {
			// Console prompt
			System.out.print("What would you like to do?: ");
			String input = scanner.nextLine();
			
			JSONObject rRate, rShipment;
			
			// Switch statement for predetermined commands
			switch (input) {
			// Create a rate
			case "create rate":
				rate = createRate();
				// Send the rate creation command to the rate url
				writeToURL(chandler);
				// Acquire the rate id and set it to the rateID global variable
				rRate = new JSONObject(responseToJSON().getJSONArray("rates").get(0).toString());
				rateID = rRate.getString("id");
				break;
				
			// Create a shipment
			case "create shipment":
				shipment = createShipment();
				// Write the shipment creation to the connection url
				writeToURL(chandler);
				// Read the response and print it out
				rShipment = responseToJSON();
				System.out.println("Package ID: " + rShipment.get("id"));
				shipmentID = rShipment.get("id").toString();
				break;
				
			// Update the shipment status
			case "update status":
				shipmentStatus = new JSONObject();
				shipmentStatus.put("status", "delivered");
				// Modify the update url
				updateURL = updateURL + shipmentID;
				// Update the connection url
				chandler = newConnection(updateURL, "POST", 1);
				writeToURL(chandler);
				System.out.println("Package: " + responseToJSON().get("status").toString());
				new Thread(new DeliveryHandler(shipmentID)).start();
				break;
				
			// Exit the program
			case "quit":
				running = false;
				break;
				
			// Invalid input
			default:
				System.out.println("Invalid command. Press any key to continue...");
				input = scanner.nextLine();
				break;
			}
		}
	}
	
	/**
	 * Helper method used to prompt the user for specific rate object information
	 * then uses that information to create a rate object.
	 * @return - The rate object using acquired information
	 * @throws IOException 
	 */
	private static RateObject createRate() throws IOException {
		// Temporary rate object to be returned
		RateObject temp = new RateObject();
		// Set the connection to the rate url
		chandler = newConnection(rateURL, "POST", 0);
		
		// Hard-coded information for demo purposes.
		temp.createRateItemsArray(10, 10, 10, 10, 25.00);
		temp.createOrigin(92109);
		temp.createDestination(92683);
		
		// Return temporary rate object
		return temp;
	}
	
	/**
	 * Helper method used to acquire information on a shipment, then using the acquired
	 * information to create a shipment object and returns it.
	 * @return - The shipment object created using acquired information
	 * @throws IOException 
	 */
	private static ShipmentObject createShipment() throws IOException {
		// Temporary shipment object to be created and manipulated
		ShipmentObject temp = new ShipmentObject();
		// Set the connection to the shipment url
		chandler = newConnection(shipmentURL, "POST", 0);
		
		// Hard-coded shipment object for demonstration purposes
		temp.createAddress(temp.getOrigin(), "yolo", "swag", "420", "CA", 92109);
		temp.createAddress(temp.getDestination(), "blaze", "it", "aayy", "CA", 92683);
		temp.createShipment(rateID);
		
		// Return temporary shipment object
		return temp;
	}
	
	/**
	 * Helper method to change the connection url
	 * @param URL - New connection URL
	 * @param operation - New operation for this url (POST or GET)
	 * @param mode - The connection or x-api-key
	 * @return - The updated connection handler
	 */
	private static ConnectionHandler newConnection(String URL, String operation, int mode) {
		ConnectionHandler temp = new ConnectionHandler(URL, operation, mode);
		return temp;
	}
	
	private static void writeToURL(ConnectionHandler ch) throws IOException {
		OutputStreamWriter osw;
		osw = new OutputStreamWriter(ch.getConnection().getOutputStream());
		
		if (ch.getURLString().indexOf("rates?") != -1) {
			osw.write(rate.getRate().toString());
			osw.flush();		
		}
		else if (ch.getURLString().indexOf("shipments?") != -1) {
			osw.write(shipment.getShipment().toString());
			osw.flush();
		}
		else if (ch.getURLString().indexOf("shipments/") != -1) {
			osw.write(shipmentStatus.toString());
			osw.flush();
		}
	}
	
	/**
	 * Read the input stream from the connection handler and return it
	 * @return - the input stream read from chandler
	 * @throws IOException
	 */
	private static InputStreamParser getISP() throws IOException {
		InputStreamParser isp = new InputStreamParser(chandler.getConnection());
		return isp;
	}
	
	/**
	 * Parse the http response string into a JSON Object, 
	 * @return - the newly created JSON from the http response
	 * @throws JSONException
	 * @throws IOException
	 */
	private static JSONObject responseToJSON() throws JSONException, IOException {
		JSONObject responseJSON = new JSONObject(getISP().getResponseString().toString());
		return responseJSON;
	}
}
