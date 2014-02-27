import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.ornilabs.helpers.sockets.Params;
import com.ornilabs.helpers.sockets.Receiver;


public class GroupReceiver extends Receiver{
	
	Map<String,Status> devices = new HashMap<String,Status>();
	Map<String,Timer> deconnectTasks = new HashMap<String,Timer>();
	Map<String,InetAddress> devicesIps = new HashMap<String,InetAddress>();
	private Object mutex = new Object();

	public GroupReceiver(int portToListen) throws IOException {
		super(portToListen);
	}

	@Override
	protected synchronized void onMessageReceived(String message, InetAddress sender) {
		String[] splitMessage = message.split(":");
		if(splitMessage.length<1) return; 
		String deviceName = splitMessage[0].trim();
		put(deviceName, Status.Connected, sender);
	}
	
	public void put(String deviceName, Status status, InetAddress sender) {
		synchronized(mutex) {
			devices.put(deviceName, status);
			Timer timer = new Timer();
			if(deconnectTasks.containsKey(deviceName)) {
				deconnectTasks.get(deviceName).cancel();
				deconnectTasks.remove(deviceName);
			}
			TimerTask deconnectTask = new DeconnectTask(deviceName, timer);
			timer.schedule(deconnectTask, Params.TIMEOUT);
			deconnectTasks.put(deviceName, timer);
			devicesIps.put(deviceName, sender);
		}
	}
	
	public void deconnect(String deviceName) {
		synchronized(mutex) {
			devices.remove(deviceName);
			deconnectTasks.remove(deviceName);
			devicesIps.remove(deviceName);
		}
	}
	
	public Status get(String deviceName) {
		synchronized(mutex) {
			if(!devices.containsKey(deviceName)) {
				devices.put(deviceName, Status.Deconnected);
			}
			return devices.get(deviceName);
		}
	}
	
	public InetAddress getIp(String deviceName) {
		synchronized(mutex) {
			if(!devicesIps.containsKey(deviceName)) {
				return null;
			}
			return devicesIps.get(deviceName);
		}
	}
	

	public Map<String, Status> getConnectedClients() {
		synchronized(mutex) {
			return new HashMap<String,Status>(devices);
		}
	}
	
	
	private class DeconnectTask extends TimerTask {

		private String deviceName;
		private Timer timer;

		public DeconnectTask(String deviceName, Timer timer) {
			this.deviceName = deviceName;
			this.timer = timer;
		}

		@Override
		public void run() {
			deconnect(deviceName);
			timer.cancel();
		}
		
	}

}
