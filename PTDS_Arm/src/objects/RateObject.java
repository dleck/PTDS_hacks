package objects;

import org.json.JSONArray;
import org.json.JSONObject;

public class RateObject {
	// Main rate objects of this class
	JSONObject rate;
	// Description of the rate object
	JSONObject rateItems;
	// Origin of package and destination of package
	JSONObject origin, destination;
	// JSONArray to hold the rateItems in proper format to be read by ShipHawk
	JSONArray array;
	
	/**
	 * Default constructor. Create an empty rate object with all fields set to null
	 */
	public RateObject() {
		rate = new JSONObject();
		rateItems = new JSONObject();
		origin = new JSONObject();
		destination = new JSONObject();
		array = new JSONArray();
	}
	
	/**
	 * Constructor that takes in a string parameter to create a non-empty rate object
	 * @param string - The JSON body of this rate object
	 */
	public RateObject(String string) {
		rate = new JSONObject(string);
		array = rate.getJSONArray("items");
		rateItems = array.getJSONObject(0);
		origin = rate.getJSONObject("origin_address");
		destination = rate.getJSONObject("destination_address");
	}
	
	/**
	 * Create a description for the rate object.
	 * Specifying the length, width, height, weight, and value
	 * @param length - Length of rate
	 * @param width - Width of rate
	 * @param height - Height of rate
	 * @param weight - Weight of rate
	 * @param value - Value/Price/Worth or rate
	 */
	public void createRateItemsArray(int length, int width, int height, int weight, double value) {
		rateItems.put("length", length);
		rateItems.put("width", width);
		rateItems.put("height", height);
		rateItems.put("weight", weight);
		rateItems.put("value", value);
		
		array.put(rateItems);
		rate.put("items", array);
	}
	
	/**
	 * Create the origin details of the rate object then put it into the rate object
	 * @param zip - The zip code in which the package will originate
	 */
	public void createOrigin(int zip) {
		origin.put("zip", zip);
		
		rate.put("origin_address", origin);
	}
	
	/**
	 * Create the destination details of the rate object and put it into the rate object
	 * @param zip - The zip code to which the package will be delivered
	 */
	public void createDestination(int zip) {
		destination.put("zip", zip);
		
		rate.put("destination_address", destination);
	}
	
	/** Getter methods to return JSONObject members and JSONArray members **/
	public JSONObject getRate() { return rate; }
	public JSONObject getRateItems() { return rateItems; }
	public JSONObject getOrigin() { return origin; }
	public JSONObject getDestination() { return destination; }
	public JSONArray getArray() { return array; }
}
