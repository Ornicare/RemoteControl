import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;


public class Launch {
	public static UUID token;

	public static void main(String[] args) throws IOException {
		

		token = UUID.fromString("fb5875eb-04b4-49df-ac24-5945c50cfd47"); //UUID.randomUUID();
		
		CommandReceiver receiver = new CommandReceiver(51425, token);
		
		
		String deviceName = System.getProperty("user.name")+"@"+InetAddress.getLocalHost().getHostName();
		System.out.println(deviceName);
		@SuppressWarnings("unused")
		AliveEmitter aliveEmitter = new AliveEmitter(51424,deviceName);
	}
}
