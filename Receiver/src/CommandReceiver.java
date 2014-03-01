import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import javax.swing.JOptionPane;

import com.ornilabs.helpers.CommandHelper;
import com.ornilabs.helpers.StoppableThread;


public class CommandReceiver extends StoppableThread{

	private ServerSocket socketserver;
	private String clientUUID;

	public CommandReceiver(int portToListen, String string) throws IOException {
		socketserver = new ServerSocket(portToListen);
		this.clientUUID = string;
		start();
	}
	
	class RecevoirClients extends StoppableThread {
		private Socket socket;
		private PrintWriter out;
		private BufferedReader in;

		public RecevoirClients(Socket sender) {
			this.socket = sender;
			start();
		}

		public void doUnitOfWork() {
			try {
				out = new PrintWriter(socket.getOutputStream());
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				System.out.println("Waiting for first message.");
				//get first line
				String first_message = in.readLine();
				System.out.println("First message : "+first_message);
				if(first_message.startsWith("Command:"+clientUUID+":")) {
					//Process command
					String command = first_message.substring(new String("Command:"+clientUUID+":").length());
					System.out.println("Command : "+command);
					String s = CommandHelper.executeCommand(command);
					out.println("execute : "+s.replace("\n", "|"));
					out.flush();
					return;
				}
				else if(first_message.startsWith("Command:")) {
					System.out.println("Wrong UUID !");
					out.println("denied");
					out.flush();
					return;
				}
				
				//get server token
				String serverName = first_message.split(":")[1];
				System.out.println("Server has name : "+serverName);
				


				int choice = JOptionPane.showOptionDialog(null,"Do you allow "+serverName+" to send commands ?","Choose an option", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, 
				                               new String[]{"Yes","No"},
				                               "No");
				if(choice == 0 ){
					out.println(clientUUID.toString());
				}else{
					out.println("denied");
				}
				out.flush();


			} catch (IOException e) {
				e.printStackTrace();
			}
			
			done();
		}
	}

	@Override
	protected void doUnitOfWork() {
		try {
			Socket sender = socketserver.accept();
			System.out.println("Start connection with remote command.");
			new RecevoirClients(sender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
