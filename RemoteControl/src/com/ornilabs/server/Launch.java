package com.ornilabs.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.os.AsyncTask;


public class Launch {
	private static GroupReceiver groupListener;
	private static int sendPort = 51425;
	private static String name = "MySuperTemporaryName";
	private static Map<String, String> appairedClient;

	public static void main(String[] args) throws IOException {
//		Emitter emitter = new Emitter(51425);
//		emitter.sendMessage("Je suis un troll.");
		groupListener = new GroupReceiver(51424);
		groupListener.start();
		
		appairedClient = new HashMap<String, String>();

		//En pratique on saura que le device est connecté. Ici on attend un peu. (pas de pb, le client est dans la hashmap)
		Timer timer = new Timer();
		timer.schedule(new TempClass(), 10000);
		
		
		while(true) {
			Map<String, Status> clients = groupListener.getConnectedClients();
			System.out.println("---------------------------------------------------------\nClients");
			for(String deviceName :  clients.keySet()) {
				System.out.println(deviceName+" : "+clients.get(deviceName));
			}
			if(clients.size()==0) System.out.println("none");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	private static void sendCommandToDevice(String command) throws IOException {
		
		Socket canal = new Socket(groupListener.getIp("Antoine@Chronophage"), sendPort );
		try {
			PrintWriter out = new PrintWriter(canal.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					canal.getInputStream()));
			
			
			if(!appairedClient.containsKey("Antoine@Chronophage")) {
				System.out.println("Device "+name+" not linked ! Abort.");
				return;
			}
			out.println("Command:"+appairedClient.get("Antoine@Chronophage")+":"+command);
			out.flush();
			
			//wait for client answer
			String response = in.readLine();
			if(response.startsWith("denied")) {
				//error with appairage, redo it
				return;
			}
			System.out.println("Command "+command+" successfully send and received ! Response : "+response);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void linkWithDevice() throws IOException {
		Socket canal = new Socket(groupListener.getIp("Antoine@Chronophage"), sendPort );
		try {
			PrintWriter out = new PrintWriter(canal.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					canal.getInputStream()));
			
			//Send serverUUID
			System.out.println("Server name : "+name );
			out.println(name);
			out.flush();
			
			//wait for client answer
			String response = in.readLine();
			if(response.startsWith("denied")) {
				//client denied
				System.out.println("Client denied authorization !");
				return;
			}
			appairedClient.put("Antoine@Chronophage", response);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static class TempClass extends TimerTask {

		@Override
		public void run() {
			try {
				linkWithDevice();
				//temp, execute command directly
				sendCommandToDevice("shutdown -s -t 00");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
