package objects;

public interface MessageListener {
	void onConnected();
	void onDisconnected(String message);
	void onMessageRecieved(String string);
	void onError(String string);
}
