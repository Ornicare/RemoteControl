package com.ornilabs.helpers.sockets;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Emitter {

	protected DatagramSocket socket = null;
	private InetAddress sendGroup;
	private byte[] buf;
	private int portToSend;

	public Emitter(int portToSend)
			throws IOException {
		super();
		socket = new DatagramSocket();

		
		sendGroup = InetAddress.getByAddress(Params.Adress);
//		InetAddress group = InetAddress.getByName("224.42.42.44");
//		socket.joinGroup(sendGroup);
		this.portToSend = portToSend;

	}

	public void sendMessage(String messageToSend) {
		if (messageToSend.getBytes().length > Params.PACKET_LENGTH)
			throw new IllegalArgumentException(
					"Message should mesure less than "+Params.PACKET_LENGTH+" char.");
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
