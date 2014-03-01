package com.ornilabs.helpers.sockets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.ornilabs.helpers.StoppableThread;

public abstract class Receiver extends StoppableThread {
	protected MulticastSocket socket;
	protected InetAddress group;

	public Receiver(int portToListen) throws IOException {
		socket = new MulticastSocket(portToListen);
		group = InetAddress.getByAddress(Params.Adress);
		socket.joinGroup(group);
	}

	public static void main(String[] args) throws IOException {

	}

	@Override
	protected void doUnitOfWork() {
		try {

			DatagramPacket packet;
			byte[] buf = new byte[256];
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			String received = new String(packet.getData());
			onMessageReceived(received,packet.getAddress());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected abstract void onMessageReceived(String message, InetAddress sender);

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
}
