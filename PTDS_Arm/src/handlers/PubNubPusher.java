package handlers;

import java.rmi.UnexpectedException;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import objects.MessageListener;

public class PubNubPusher {
	private static PubNubPusher myInstance = null;
	private final static String CHANNEL_NAME = "PTDS_channel";
	private final static String PUBLISH_KEY = "pub-c-c4775583-2521-4e1f-b853-cdb74ae25cb3";
	private final static String SUBSCRIBE_KEY = "sub-c-9dbe6968-0995-11e6-8c3e-0619f8945a4f";
	
	private static MessageListener listener;
	private Pubnub pubnub;
	Callback callback;
	
	private PubNubPusher() throws UnexpectedException
	{
		// you shall not make 2
		if (myInstance != null)
			throw new UnexpectedException("Ayo biatchm dis is a singleton, dont you be doin no reflection on dis mofo");
		
		// init pubnub helper
		System.out.println("Initiating PubNubPublisher");
		pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
		onConnected();
		
		try {
			  pubnub.subscribe("my_channel", new Callback() {
			      @Override
			      public void connectCallback(String channel, Object message) {
			          pubnub.publish("my_channel", "Hello from the PubNub Java SDK", new Callback() {});
			      }
			 
			      @Override
			      public void disconnectCallback(String channel, Object message) {
			          System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
			                     + " : " + message.getClass() + " : "
			                     + message.toString());
			          onDisconnected("Disconnected from " + channel + " - " + message.toString());
			      }
			 
			      public void reconnectCallback(String channel, Object message) {
			          System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
			                     + " : " + message.getClass() + " : "
			                     + message.toString());
			      }
			 
			      @Override
			      public void successCallback(String channel, Object message) {
			          System.out.println("SUBSCRIBE : " + channel + " : "
			                     + message.getClass() + " : " + message.toString());
			          onMessage(message.toString());
			      }
			 
			      @Override
			      public void errorCallback(String channel, PubnubError error) {
			          System.out.println("SUBSCRIBE : ERROR on channel " + channel
			                     + " : " + error.toString());
			          onError("Couldn't subscribe to " + channel + " -- " + error.toString());
			      }
			    }
			  );
			  
			  callback = new Callback() {
			  public void successCallback(String channel, Object response) {
				    System.out.println(response.toString());
				  }
				  public void errorCallback(String channel, PubnubError error) {
				    System.out.println(error.toString());
				  }
				};
					
			} catch (PubnubException e) {
			  System.out.println(e.toString());
			  onError(e.getErrorResponse());
			}
		
	}
	
	private void onDisconnected(String message) {
		if (listener != null)
			listener.onDisconnected(message);
	}

	private void onMessage(String string) {
		if (listener != null)
			listener.onMessageRecieved(string);		
	}

	private void onConnected() {
		if (listener != null)
			listener.onConnected();		
	}

	private void onError(String string) {
		if (listener != null)
			listener.onError(string);		
	}
	
	public void publish(String message)
	{
		pubnub.publish(CHANNEL_NAME, message , callback);
	}

	public static PubNubPusher getInstance()
	{
		if (myInstance != null)
			return myInstance;
		
		try {
			myInstance = new PubNubPusher();
		} catch (UnexpectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myInstance;
	}
	
	public static PubNubPusher getInstance(MessageListener mylistener)
	{
		listener = mylistener;
		return getInstance();
	}
}
