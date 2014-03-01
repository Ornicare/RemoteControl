import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import com.ornilabs.consts.Constantes;
import com.ornilabs.helpers.sockets.Params;


public class Launch {
	public static UUID token;
	private static UUID publicToken;

	public static void main(String[] args) throws IOException {
	
//		token = UUID.fromString("fb5875eb-04b4-49df-ac24-5945c50cfd47"); //UUID.randomUUID();
//		publicToken = UUID.fromString("5a711481-148b-44f4-95ae-a86ed617bec9");
		publicToken = UUID.randomUUID();
		token = UUID.randomUUID();
		//PArams for encryption => attention gère la taille max des messages.
		Params.PACKET_LENGTH = 3000; //1365 for 1024 RSA key. (*4/3)
		Constantes.MESSAGE_LENGTH = 117*8*2; //RSAKEY_LENGTH/8 -11 octets (padding PKCS1)
		Constantes.RSAKEY_LENGTH = 1024*2;
		
		CommandReceiver receiver = new CommandReceiver(51425, publicToken+":"+token);

		
		String deviceName = System.getProperty("user.name")+"@"+InetAddress.getLocalHost().getHostName();
		System.out.println(deviceName);
		@SuppressWarnings("unused")
		AliveEmitter aliveEmitter = new AliveEmitter(5353,deviceName,publicToken);
	}
}
