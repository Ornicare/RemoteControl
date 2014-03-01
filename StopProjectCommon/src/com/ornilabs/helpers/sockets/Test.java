package com.ornilabs.helpers.sockets;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {
		new Emitter(5353).sendMessage("test");
	}
}
