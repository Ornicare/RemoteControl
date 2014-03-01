package com.ornilabs.server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

import com.ornilabs.helpers.StoppableThread;
import com.ornilabs.helpers.sockets.Params;
import com.ornilabs.helpers.sockets.Receiver;


public class GroupReceiver extends StoppableThread{
	
	Map<String,Status> devices = new HashMap<String,Status>();
	Map<String,Timer> deconnectTasks = new HashMap<String,Timer>();
	Map<String,InetAddress> devicesIps = new HashMap<String,InetAddress>();
	protected MulticastSocket socket;
	protected InetAddress group;
	private Object mutex = new Object();
	private int porToListen;
	private boolean isReady;

	public GroupReceiver(int portToListen) throws IOException {
//		super(portToListen);
		this.porToListen = portToListen;
		Log.i("test", "groupreceiver created");
	}

	protected synchronized void onMessageReceived(String message, InetAddress sender) {
		String[] splitMessage = message.split(":");
		if(splitMessage.length<1) return; 
//		String deviceName = splitMessage[0].trim();
		Log.i("test", message.trim());
		put(message.trim(), Status.Connected, sender);
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
			devices.put(deviceName, Status.Deconnected);
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
	

	@Override
	protected void doUnitOfWork() {
		if(isReady) {
			try {

				DatagramPacket packet;
				byte[] buf = new byte[256];
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String received = new String(packet.getData());
				onMessageReceived(received,packet.getAddress());

			} catch (Exception e) {
				e.printStackTrace();
				closeConnection();
			}
		}
		
	}

	@Override
	protected void cleanup() {
		try {
			socket.leaveGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket.close();
		super.cleanup();
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


	public void closeConnection() {
		Log.e("GM","Close c");
		isReady = false;
		try {
			try {
				socket.leaveGroup(group);
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket.close();
		}
		catch(Exception e) {}
	}
	
	public void openConnection() {
		Log.e("GM","Open c");
		this.isReady = true;
		try {
			socket = new MulticastSocket(porToListen);
			group = InetAddress.getByAddress(Params.Adress);
			socket.joinGroup(group);
		} catch (IOException e) {
			isReady = false;
		}
		
	}

	public boolean isReady() {
		return isReady;
	}

}
