package objects;

import org.json.JSONObject;

public class ShipmentObject {
	JSONObject shipment;
	JSONObject origin;
	JSONObject destination;
	
	/**
	 * Default constructor. Initialize shipment to a null JSONObject and also
	 * initialize the origin and destination JSONObjects to null
	 */
	public ShipmentObject() {
		shipment = new JSONObject();
		origin = new JSONObject();
		destination = new JSONObject();
	}
	
	public ShipmentObject(int lol) {
		shipment = new JSONObject();
		shipment.put("status", "delivered");
	}
	
	/**
	 * Create an address. Used for the origin and destinations. All fields are required.
	 * @param object - The JSONObject to be manipulated (getOrigin or getDestination)
	 * @param street1 - The first field of one's street address
	 * @param street2 - The second field of one's street address (suite number)
	 * @param city - The city field of one's address
	 * @param state - The abbreviation of one's state
	 * @param zip - The zip code of one's location
	 */
	public void createAddress(JSONObject object, String street1, String street2, String city,
								String state, int zip) {
		object.put("street1", street1);
		object.put("street2", street2);
		object.put("city", city);
		object.put("state", state);
		object.put("zip", zip);
		object.put("country", "US");
	}
	
	/**
	 * Initialize the shipment with a specified id, set the status to "ordered"
	 * and add the origin and destination JSONObjects into the main shipment object.
	 * createAddress() MUST be called before this method.
	 * @param id - The ID of the rate to be shipped
	 */
	public void createShipment(String id) {
		shipment.put("rate_id", id);
		shipment.put("status", "ordered");
		shipment.put("origin_address", origin);
		shipment.put("destination_address", destination);
	}
	
	/** Getters for JSONObject members **/
	public JSONObject getShipment() { return shipment; }
	public JSONObject getOrigin() { return origin; }
	public JSONObject getDestination() { return destination; }
}
