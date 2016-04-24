package handlers;

import java.io.IOException;

import org.json.JSONObject;

import objects.MessageListener;

public class DeliveryHandler implements Runnable, MessageListener{
	private static final String DELIVERY_NOTIFICATION = "computer:delivered";
	ConnectionHandler chandler;
	String trackerURL = "https://sandbox.shiphawk.com/api/v4/shipments/";
	JSONObject tracker;
	InputStreamParser isp;
	
	public DeliveryHandler(String shipmentID) throws IOException {
		trackerURL = trackerURL + shipmentID + "/tracking";
		chandler = new ConnectionHandler(trackerURL, "GET", 1);
	}
	
	@Override
	public void run() {
		boolean ping = true;
		while (ping) {
			try {
				isp = new InputStreamParser(chandler.getConnection());
				if (isp.getResponseString().toString().equals("")) continue;
				System.out.println(isp.getResponseString().toString());
				tracker = new JSONObject(isp.getResponseString().toString());
				//System.out.println(tracker.get("status"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (tracker.get("status").toString().equals("delivered"))
			{
				ping = false;
				// SEND TO PORNHUB
				PubNubPusher.getInstance(this).publish(DELIVERY_NOTIFICATION);
			}
		}
	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		System.out.println("Connected!");
	}

	@Override
	public void onDisconnected(String message) {
		// TODO Auto-generated method stub
		System.out.println(message);

	}

	@Override
	public void onMessageRecieved(String string) {
		// TODO Auto-generated method stub
		System.out.println("Recieved: " + string);

	}

	@Override
	public void onError(String string) {
		// TODO Auto-generated method stub
		System.out.println("Error: " + string);

	}

}
