import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.ornilabs.helpers.sockets.Emitter;
import com.ornilabs.helpers.sockets.Params;


public class AliveEmitter {

	private Emitter emitter;
	private Timer timer;

	public AliveEmitter(int portToSend, String deviceName, UUID publicToken) throws IOException {
		this.emitter = new Emitter(portToSend);
		timer = new Timer();
		timer.schedule(new AliveTask(deviceName+":"+publicToken.toString()), 0, Params.TIMEOUT/2);
	}
	
	private class AliveTask extends TimerTask {

		private String deviceName;

		public AliveTask(String deviceName) {
			this.deviceName = deviceName;
		}

		@Override
		public void run() {
			emitter.sendMessage(deviceName);
		}
		
	}
	
	public void done() {
		timer.cancel();
	}

}
