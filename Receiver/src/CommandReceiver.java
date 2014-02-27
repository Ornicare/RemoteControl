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
	private UUID clientUUID;

	public CommandReceiver(int portToListen, UUID clientUUID) throws IOException {
		socketserver = new ServerSocket(portToListen);
		this.clientUUID = clientUUID;
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
				
				//get first line
				String first_message = in.readLine();
				if(first_message.startsWith("Command:"+clientUUID+":")) {
					//Process command
					String command = first_message.substring(new String("Command:"+clientUUID+":").length());
					System.out.println("Command : "+command);
					CommandHelper.executeCommand(command);
					out.println("succes");
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
				System.out.println("Server has name : "+first_message);
				


				int choice = JOptionPane.showOptionDialog(null,"Do you allow "+first_message+" to send commands ?","Choose an option", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, 
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
			System.out.println("ffddfsfsdfdsfds");
			new RecevoirClients(sender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
