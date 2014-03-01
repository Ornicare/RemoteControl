package com.ornilabs.helpers.sockets;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Emitter {

	protected MulticastSocket socket = null;
	private InetAddress sendGroup;
	private byte[] buf;
	private int portToSend;

	public Emitter(int portToSend)
			throws IOException {
		super();
		socket = new MulticastSocket();

		
		sendGroup = InetAddress.getByAddress(Params.Adress);
//		InetAddress group = InetAddress.getByName("224.42.42.44");
		socket.joinGroup(sendGroup);
		this.portToSend = portToSend;

	}

	public void sendMessage(String messageToSend) {
		if (messageToSend.length() > 256)
			throw new IllegalArgumentException(
					"Message should mesure less than 256 char.");
		try {
			buf = messageToSend.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length,
					sendGroup, portToSend);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void stop() {
		socket.close();
	}

}
