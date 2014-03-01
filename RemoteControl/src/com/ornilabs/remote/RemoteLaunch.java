package com.ornilabs.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import com.ornilabs.classes.ExpandableListAdapter;
import com.ornilabs.consts.Constantes;
import com.ornilabs.encryption.EncryptionLayer;
import com.ornilabs.helpers.sockets.Params;
import com.ornilabs.interfaces.ICategory;
import com.ornilabs.interfaces.IChild;
import com.ornilabs.remote.gui.Action;
import com.ornilabs.remote.gui.Client;
import com.ornilabs.server.GroupReceiver;
import com.ornilabs.server.Status;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RemoteLaunch extends Activity {

	public static boolean showNotLinkedClients = true;
	private ExpandableListView list;
	private SharedPreferences shared;
	private ExpandableListAdapter adapter;
	protected ArrayList<ICategory> clientsList;
	protected GroupReceiver groupListener;
	protected HashMap<String, String> appairedClient;
	private ArrayList<IChild> childList;
	private Context context;
	private String uuid;

	private String serverName; 
	private ArrayList<IChild> childListLink;
	private MulticastLock lock;
	private MenuItem showMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//PArams for encryption => attention gère la taille max des messages.
		Params.PACKET_LENGTH = 3000; //1365 for 1024 RSA key. (*4/3)
		Constantes.MESSAGE_LENGTH = 117*8*2; //RSAKEY_LENGTH/8 -11 octets (padding PKCS1)
		Constantes.RSAKEY_LENGTH = 1024*2;

		ProgressDialog mProgressDialog = ProgressDialog.show(RemoteLaunch.this,
				"Loading...", "Application is Loading...");
		shared = PreferenceManager.getDefaultSharedPreferences(this);
		
		this.context = getApplicationContext();

		list = new ExpandableListView(this);
		list.setGroupIndicator(null);
		restoreState();
		list.setChildIndicator(null);
		
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifi != null) {
				lock = wifi
						.createMulticastLock("Log_Tag");
				lock.acquire();
		}
		
		Log.i("lock",""+lock.isHeld());

		list.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				clientsList.get(groupPosition).setUnwrapped(false);
				adapter.notifyDataSetChanged();
			}
		});

		list.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				clientsList.get(groupPosition).setUnwrapped(true);
				adapter.notifyDataSetChanged();
			}
		});

		serverName = getEmail(context) + " (" + getDeviceName() + ")";

		// registerForContextMenu(list);

		restoreState();

		adapter.notifyDataSetChanged();

		try {
			groupListener = new GroupReceiver(5353);
		} catch (IOException e) {
		}
		// si pas wifi activé, plante
		groupListener.start();
		

		//manage wifi connexion
		Timer timer2 = new Timer();
		timer2.schedule(new WifiListener(), 0, 5000);
		
		

		// common
		childList = new ArrayList<IChild>();
		for (Action action : Action.values()) {
			if (action != Action.LINK)
				childList.add(action);
		}

		childListLink = new ArrayList<IChild>();
		childListLink.add(Action.LINK);

		Timer timer = new Timer();
		timer.schedule(new ControlThread(), 0, 5000);

		setContentView(list);
		// setContentView(R.layout.activity_remote_launch);

		mProgressDialog.dismiss();

	}
	
	private class WifiListener extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isConnected(RemoteLaunch.this)) {
				if(groupListener.isReady())groupListener.closeConnection();
			}
			else {
				if(!lock.isHeld()) lock.acquire();
				if(!groupListener.isReady())groupListener.openConnection();
			}
			Log.w("test", ""+groupListener.isReady());
		}
		
	}

	public static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
			networkInfo = connectivityManager.getActiveNetworkInfo();
		}

		return networkInfo != null
				&& networkInfo.getState() == NetworkInfo.State.CONNECTED;
	}

	static String getEmail(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account account = getAccount(accountManager);

		if (account == null) {
			return null;
		} else {
			return account.name;
		}
	}

	private static Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account;
		if (accounts.length > 0) {
			account = accounts[0];
		} else {
			account = null;
		}
		return account;
	}

	public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.principale, menu);
		showMenu = menu.findItem(R.id.action_mask_not_linked_clients);
//		showMenu.getActionView().clearAnimation();
		updateShowMenu();
		return true;
	}
	
	private void updateShowMenu() {
			
			
//	        Animation rotation = AnimationUtils.loadAnimation(this, R.layout.action_bar_progress);
//	        rotation.setRepeatCount(Animation.INFINITE);
//	        if(RemoteLaunch.showNotLinkedClients) iv.startAnimation(rotation);

		if(showMenu.getActionView()==null)showMenu.setActionView(R.layout.action_bar_progress);
			if(RemoteLaunch.showNotLinkedClients) {
//				LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		        ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
//
		        Animation rotation = AnimationUtils.loadAnimation(this, R.layout.clockwise_refresh);
		        rotation.setRepeatCount(Animation.INFINITE);
//		        iv.startAnimation(rotation);
//				showMenu.setActionView(iv);
//				showMenu.setActionView(R.layout.action_bar_progress);
				if(showMenu.getActionView()!=null)showMenu.getActionView().startAnimation(rotation);
			}
			else {
				if(showMenu.getActionView()!=null)showMenu.getActionView().clearAnimation();
//				showMenu.setActionView(null);
//				showMenu.setActionView(R.layout.action_bar_stop);
			}
				
//	        showMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//				
//				@Override
//				public boolean onMenuItemClick(MenuItem item) {
//					RemoteLaunch.this.onOptionsItemSelected(showMenu);
//					return false;
//				}
//			});
	        showMenu.getActionView().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					RemoteLaunch.this.onOptionsItemSelected(showMenu);
				}
			});
//		}
//		else {
//			showMenu.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
//			if(showMenu.getActionView()!=null)showMenu.getActionView().clearAnimation();
//			showMenu.setActionView(null);
//		}
		
//		if(RemoteLaunch.showNotLinkedClients) {
//			
// 			
//		}
//		else {
////			showMenu.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
//			if(showMenu.getActionView()!=null)showMenu.getActionView().clearAnimation();
////			showMenu.setActionView(null);
//		}
		
	}
	



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		 case R.id.action_mask_not_linked_clients:
			 if(RemoteLaunch.showNotLinkedClients) {
				 RemoteLaunch.showNotLinkedClients = false;
				 Toast.makeText(context, "Now hiding not linked clients.", Toast.LENGTH_SHORT).show();
				 ArrayList<ICategory> newDataList = new ArrayList<ICategory>(clientsList);
				 ArrayList<ICategory> copyList = (new ArrayList<ICategory>(clientsList));
				 for(int i = copyList.size()-1;i>=0;i--) {
					 if(((Client) copyList.get(i)).getState()==Status.Connected) {
						 newDataList.remove(i);
						 Log.i("dsqds", "Client removed");
					 }
				 }
				 clientsList = newDataList;
				 adapter = new ExpandableListAdapterExtend(this, clientsList, list);
				 list.setAdapter(adapter);
				 adapter.notifyDataSetChanged();
				 
			 }
			 else {
				 RemoteLaunch.showNotLinkedClients = true;
				 Toast.makeText(context, "Now showing not linked clients.", Toast.LENGTH_SHORT).show();
			 }

			 updateShowMenu();
			 runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					 adapter.notifyDataSetChanged();
				}
			});
			 
		 return true;
		case R.id.action_reploy:
			for (int i = 0; i < clientsList.size(); i++) {
				if (clientsList.get(i).isUnwrapped()) {
					clientsList.get(i).setUnwrapped(false);
					list.collapseGroup(i);
				}
			}
			// saveState();
			 Toast.makeText(context, "Retract all clients.", Toast.LENGTH_SHORT).show();
			adapter.notifyDataSetChanged();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onPause() {
		super.onPause();
		saveState();
	}

	public void onResume() {
		super.onResume();
		restoreState();
	}

	public void restoreState() {

		// restore from save : server uuid /[client, cleint uuid]
		String s = null;
		try {
			s = shared.getString("main", null);
			this.uuid = shared.getString("uuid", UUID.randomUUID().toString());
		} catch (Throwable e) {
			e.printStackTrace();

		}

		try {
			if (s != null) {
				deserialize(s);
			} else {
				appairedClient = new HashMap<String, String>();
			}
		} catch (Exception e) {
			// In case of corrupted data, recreate them.
			appairedClient = new HashMap<String, String>();
		}

		clientsList = new ArrayList<ICategory>();

		for (String clientName : appairedClient.keySet()) {
			clientsList.add(new Client(clientName, childList));
		}

		adapter = new ExpandableListAdapterExtend(this, clientsList, list);
		list.setAdapter(adapter);
		
		saveState();

		// save state of collapse
		// list.setOnGroupCollapseListener(new OnGroupCollapseListener() {
		//
		// @Override
		// public void onGroupCollapse(int groupPosition) {
		// categoryManager.getCategoriesList().get(groupPosition)
		// .setUnwrapped(false);
		// //saveState();
		// adapter.notifyDataSetChanged();
		// }
		// });
		//
		// list.setOnGroupExpandListener(new OnGroupExpandListener() {
		//
		// @Override
		// public void onGroupExpand(int groupPosition) {
		// categoryManager.getCategoriesList().get(groupPosition)
		// .setUnwrapped(true);
		// //saveState();
		// adapter.notifyDataSetChanged();
		// }
		// });

		// for (int i = 0; i < categoryManager.getCategoriesList().size(); i++)
		// {
		// if (categoryManager.getCategoriesList().get(i).isUnwrapped())
		// list.expandGroup(i);
		// }
	}

	private class ControlThread extends TimerTask {
		@Override
		public void run() {
			try {
				Map<String, Status> clients = groupListener.getConnectedClients();
				for (String deviceNameS : clients.keySet()) {
					String[] splitMessage = deviceNameS.split(":");
					if(splitMessage.length<2) return;
					try {
						UUID.fromString(splitMessage[1]);
					}
					catch(Exception e) {
						//not a client
						return;
					}
					if(deviceNameS.split("@").length<2) return;
					
					String deviceName = splitMessage[0].trim();
					String deviceUUID = splitMessage[1].trim();
					boolean contains = false;
					for (ICategory c : clientsList) {
						if (c.getText().equals(deviceName)
								&& c.getUUID().equals(deviceUUID)) {
							contains = true;
							((Client) c).setState(clients.get(deviceNameS));
							if (appairedClient.containsKey(deviceNameS)
									&& ((Client) c).getState() == Status.Connected) {
								((Client) c).setState(Status.ConnectedAndLinked);
							}

						}

					}
					if (!contains) {
						if (appairedClient.containsKey(deviceNameS)) {
							Client c = new Client(deviceNameS.trim(), childList);
							c.setState(Status.ConnectedAndLinked);
							clientsList.add(c);
						} else if(RemoteLaunch.showNotLinkedClients){
							Client c = new Client(deviceNameS.trim(), childListLink);
							c.setState(Status.Connected);
							clientsList.add(c);
						}
					}
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
			} catch(Exception e) {
				Log.e("Error in control thread", "Error in control thread");
			}
			
		}

	}

	public void sendCommandToDevice(String command, String clientName)
			throws IOException {
		new MyAsyncTaskSend().execute(command, clientName);
	}

	public void linkWithDevice(String deviceName) throws IOException {
			new MyAsyncLink().execute(deviceName);
	}

	private abstract class ExtendeAsyncTask<E, F, G> extends AsyncTask<E, F, G> {
		protected void showToast(final String message) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	private class MyAsyncTaskSend extends ExtendeAsyncTask<String, Void, Void> {

		ProgressDialog mProgressDialog;

		@Override
		protected void onPostExecute(Void result) {
			try {
				//crashe if rotate
				if(mProgressDialog.isShowing())mProgressDialog.dismiss();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(RemoteLaunch.this, "Executing command", "Starting execution procedure...");
		}

		@Override
		protected Void doInBackground(String... params) {
			
			if (!appairedClient.containsKey(params[1])) {
				showToast("Device " + params[1] + " not linked ! Abort.");
				return null;
			}
			EncryptionLayer eL = new EncryptionLayer();
			
			modifyDialogMessage("Client appaired, sending message");

			Socket canal = null;
			String command = params[0];
			try {
				Log.e("ds", params[1] + " " + groupListener.getIp(params[1]));
				InetAddress ip = groupListener.getIp(params[1]);
				if (ip == null) {
					showToast("Device not connected ! Abort.");
					return null;
				}
				canal = new Socket(groupListener.getIp(params[1]), 51425);
			} catch (IOException e1) {
				showToast("Device not connected ! Abort.");
				for(ICategory c : clientsList) {
					if((c.getText()+":"+c.getUUID()).equals(params[1])) ((Client) c).setState(com.ornilabs.server.Status.Deconnected);
				}
				e1.printStackTrace();
			}
			try {
				// Log.e("ds","ici1"+appairedClient.get(params[1]));
				PrintWriter out = new PrintWriter(canal.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(
						canal.getInputStream()));
				

				modifyDialogMessage("Initiate encryption layer...");
				out.println(eL.getPublicKey());
				out.flush();
				String clientPublicKey = in.readLine();

				
				out.println(eL.encodeString("Command:" 
						+ appairedClient.get(params[1])+ ":"+ command,clientPublicKey));
				out.flush();
				Log.e("ds", "ici"+("Command:" 
						+ appairedClient.get(params[1])+ ":"+ command).length());
				modifyDialogMessage("Message send, waiting for answer..."+eL.encodeString("Command:" 
						+ appairedClient.get(params[1])+ ":"+ command,clientPublicKey));

				try {
					// wait for client answer
					String response = eL.decodeString(in.readLine());

					Log.i("result", response);
					if (response.startsWith("denied")) {
						// error with appairage, redo it
						showToast("Client not appaired !");
						return null;
					}

					showToast("Command " + command
							+ " successfully send and received ! Response : "
							+ response);

				} catch (Exception e) {
					// Client deconnected
					showToast("Connection with client send error.");
				}

			} catch (Exception e) {
				// deconnected
				// Send message aborted
			}
			return null;
		}

		private void modifyDialogMessage(final String message) {
			if (mProgressDialog == null)
				return;
			runOnUiThread(new Runnable() {

				public void run() {
					mProgressDialog.setMessage(message);
				}
			});
		}
	}

	// private class ExtendedProgressDialog {
	//
	// protected ProgressDialog progressD;
	//
	// public ExtendedProgressDialog(Activity a, String s, String s2) {
	// this.progressD = ProgressDialog.show(a, s, s2);
	// }
	//
	// protected void modifyMessage(final String message) {
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// progressD.setMessage(message);
	// }
	// });
	// }
	//
	//
	//
	// }

	// private ExtendedDialogInterface showProgressDialog(Context context2,
	// String title, String message) {
	// InstanceHandler h = new InstanceHandler(context2, title, message);
	// Class<?>[] interfaces = ProgressDialog.class.getInterfaces();
	// interfaces = Arrays.copyOf(interfaces, interfaces.length + 1);
	// interfaces[interfaces.length-1] = ExtendedDialogInterface.class;
	// return (ExtendedDialogInterface)
	// Proxy.newProxyInstance(RemoteLaunch.class.getClassLoader(), interfaces,
	// h);
	//
	// }
	//
	// public class InstanceHandler implements InvocationHandler{
	//
	// private ProgressDialog realObject = null;
	//
	// public InstanceHandler(ProgressDialog realObject) {
	// super();
	// this.realObject = realObject;
	// }
	//
	//
	// public InstanceHandler(final Context context2, final String title, final
	// String message) {
	// // runOnUiThread(new Runnable() {
	// // @Override
	// // public void run() {
	// realObject = ProgressDialog.show(context2, title, message);
	// // }
	// // });
	// }
	//
	//
	// public Object invoke(Object pseudoObject, Method m, Object[] args)
	// throws Throwable {
	// if(m.getName()=="modifyMessage") modifyMessage((String) args[0]);
	// if(args==null) {
	// return m.invoke(realObject);
	// }
	// else {
	// return m.invoke(realObject, args);
	// }
	//
	// }
	//
	// protected void modifyMessage(final String message) {
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// realObject.setMessage(message);
	// }
	// });
	// }
	// }
	//
	// private interface ExtendedDialogInterface {
	// void modifyMessage(final String message);
	// }
	//



	private class MyAsyncLink extends AsyncTask<String, Void, Void> {


		ProgressDialog mProgressDialog;
		
		
		@Override
		protected void onPostExecute(Void result) {
			try {

				if(mProgressDialog!=null && mProgressDialog.isShowing())mProgressDialog.dismiss();
			}
			catch(Exception e) {
				e.printStackTrace();
			}

			
		}

		@Override
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(RemoteLaunch.this, "Linking...", "Starting link procedure...");
			}
			catch(Throwable e) {
				e.printStackTrace();
			}
			
		}

		private void modifyDialogMessage(final String message) {
			if (mProgressDialog == null)
				return;
			runOnUiThread(new Runnable() {

				public void run() {
					mProgressDialog.setMessage(message);
				}
			});
		}

		@Override
		protected Void doInBackground(String... params) {
			Socket canal = null;
			try {
				canal = new Socket(groupListener.getIp(params[0]), 51425);
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
			
			
			EncryptionLayer eL = new EncryptionLayer();
			
			
			try {
				PrintWriter out = new PrintWriter(canal.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(
						canal.getInputStream()));
				
				modifyDialogMessage("Initiate encryption layer...");
				out.println(eL.getPublicKey());
				out.flush();
				String clientPublicKey = in.readLine();
				
				
				modifyDialogMessage("Link established, sending message");
				// Send serverUUID
				System.out.println("Server name : " + uuid);
				
				String message = uuid + ":" + serverName;
				String encodedMessage = eL.encodeString(message,clientPublicKey);
				
				out.println(encodedMessage);
				out.flush();
				modifyDialogMessage("Message send, waiting for answer...");

				// wait for client answer
				String response = eL.decodeString(in.readLine());

				Log.i("result", response);
				if (response.startsWith("denied")) {
					// client denied
					System.out.println("Client denied authorization !");
					return null;
				}
				appairedClient.put(params[0], response);
				
				
				runOnUiThread(new Runnable() {
					public void run() {
						saveState();
					}
				});
				
				
				for (ICategory c : clientsList) {
					if ((c.getText() + ":" + c.getUUID()).equals(params[0]))
						((Client) c).changeChildList(childList);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public void saveState() {
		SharedPreferences.Editor editor = null;
		editor = shared.edit();
		try {
			String s = serialize();
			editor.putString("main", s);
			editor.putString("uuid", uuid);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		adapter.notifyDataSetChanged();
		editor.commit();
	}

	private String serialize() {
		String retour = "";
		for (String cat : appairedClient.keySet()) {
			retour += "\n"+cat + "\n" + appairedClient.get(cat);
		}
		return retour.trim();
	}

	private void deserialize(String s) {
		appairedClient = new HashMap<String, String>();
		String[] splittedString = s.split("\n");
		for (int i = 0; i < splittedString.length; i += 2) {
			appairedClient.put(splittedString[i], splittedString[i + 1]);
		}
	}

}
