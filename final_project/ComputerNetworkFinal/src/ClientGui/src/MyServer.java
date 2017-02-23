import java.io.*;
import java.net.*;
import java.util.*;

class OfflineMessageObject{
	String client_id;
	MessageObjects message_obj;
	public OfflineMessageObject(String id, MessageObjects tmp_message_obj){
		client_id = id;
		message_obj = tmp_message_obj;
	}
}

public class MyServer{
	// Map<String, PrintStream> output = new HashMap<String, PrintStream>();
	Map<String, ObjectOutputStream> output_object = new HashMap<String, ObjectOutputStream>();
	Map<String, String> User_Info = new HashMap<String, String>();
	Map<String, Integer> Online_User = new HashMap<String, Integer>();
	Vector<OfflineMessageObject> Offline_Message = new Vector<OfflineMessageObject>();

	public static void main (String args[]){
		new MyServer().go();
	}

	// =========construct link============
	public void go(){
		try{
			ServerSocket sSocket = new ServerSocket(8888);
			while (true){
				Socket cSocket = sSocket.accept();
				// PrintStream client_writer = new PrintStream(cSocket.getOutputStream());
				ObjectOutputStream client_obj_writer = new ObjectOutputStream(cSocket.getOutputStream());
				System.out.println(client_obj_writer);

				Thread t = new Thread(new Process(cSocket, client_obj_writer));
				t.start();
				System.out.println(cSocket.getLocalSocketAddress()+" have "+(t.activeCount()-1)+" Connection!");
			}
		}catch(Exception excep){
			System.out.printf("Linking Construct is broken!\n");
		}
	}
	public class Process implements Runnable{
		ObjectInputStream reader ;
		Socket client_Socket;
		PrintStream writer = null;
		ObjectOutputStream obj_writer = null;
		public Process(Socket cSocket, ObjectOutputStream client_obj_writer){
			try{
				obj_writer = client_obj_writer;
				// writer = client_writer;
				client_Socket = cSocket;
				ObjectInputStream client_reader = new ObjectInputStream(cSocket.getInputStream());
				reader = client_reader;
			}catch(Exception excep){
				System.out.printf("Linking Process is broken!\n");
			}
		}

		public void run(){
			try{
				while(true){
					MessageObjects message_obj = (MessageObjects)reader.readObject();
					System.out.println("receive: From:" + message_obj.from_client + ", tag = "+ message_obj.tag);
					HandleMessage(message_obj);
				}
			}catch(Exception excep){
				System.out.println("get err message!\n");
			}
		}
		public void HandleMessage(MessageObjects message_obj){
			String tag = message_obj.tag;
			if(tag.equals("Login")){
				HandleLogin(message_obj);

			}else if(tag.equals("Register")){
				HandleRegister(message_obj);
			
			}else if(tag.equals("Send")){
				HandleSend(message_obj);
			
			}else if(tag.equals("Logout")){
				HandleLogout(message_obj);
			
			}else if(tag.equals("SendFile")){
				HandleSendFile(message_obj);

			}else{
				System.out.println("Get Err Message Tag!");
			}
		}
		public void HandleLogin(MessageObjects message_obj){
			String ID = message_obj.id;
			String PW = message_obj.pw;
			if(User_Info.containsKey(ID) && User_Info.get(ID).equals(PW)){
				if(Online_User.containsKey(ID)){
					MessageObjects Send_Object = new MessageObjects("Login");
					Send_Object.err_signal = 1;
					Send_Object.err_message = "ERROR:This ID has already login!";
					try{
						obj_writer.writeObject(Send_Object);
					}catch(Exception e){
						System.out.println(e);
					}
				}else{
					Online_User.put(ID, 1);
					// output.put(ID, writer);
					output_object.put(ID, obj_writer);
					MessageObjects Send_Object = new MessageObjects("Login");
					Send_Object.message = "Login Success! Welcome!";
					try{
						obj_writer.writeObject(Send_Object);
					}catch(Exception e){
						System.out.println(e);
					}

					// Send Updated Onine Id to each Online Client
					MessageObjects Send_tmp_Object = new MessageObjects("Online");
					String total_Online_id = "";
					for (String online_id : (Set<String>)Online_User.keySet()){
						total_Online_id = total_Online_id + online_id + ",";
					}
					for (String online_id : (Set<String>)Online_User.keySet()){
						ObjectOutputStream tmp_obj_writer = output_object.get(online_id);
						Send_tmp_Object.online_id = total_Online_id;
						try{
							tmp_obj_writer.writeObject(Send_tmp_Object);
						}catch(Exception e){
							System.out.println(e);
						}
					}
					
					for (int index = 0 ; index < Offline_Message.size(); ){
						OfflineMessageObject offmess_obj = Offline_Message.get(index);
						System.out.println(offmess_obj.client_id);
						System.out.println(offmess_obj.message_obj.message);
						if((offmess_obj.client_id).equals(ID)){
							try{
								Send_tmp_Object = offmess_obj.message_obj;
								Send_tmp_Object.tag = "Receive";
								Send_tmp_Object.offline_signal = 1;
								obj_writer.writeObject(Send_tmp_Object);
								obj_writer.flush();
								Offline_Message.remove(index);
								continue;
							}catch(Exception e){
								System.out.println(e);
							}
						}
						index += 1;
					}
				}
			}else{
				MessageObjects Send_Object = new MessageObjects("Login");
				Send_Object.err_signal = 1;
				Send_Object.message = "ERROR:Have no this ID or PassWord wrong!";
				try{
					obj_writer.writeObject(Send_Object);
				}catch(Exception e){
					System.out.println(e);
				}
			}
			try{
				obj_writer.flush();
			}catch(Exception e){
				System.out.println(e);
			}
		}
		public void HandleRegister(MessageObjects message_obj){
			String ID = message_obj.id;
			String PW = message_obj.pw;

			if(User_Info.containsKey(ID)){
				MessageObjects Send_Object = new MessageObjects("Register");
				Send_Object.err_signal = 1;
				Send_Object.err_message = "ERROR:Already Have this ID!";
				try{
					obj_writer.writeObject(Send_Object);
				}catch(Exception e){
					System.out.println(e);
				}
			}else{
				User_Info.put(ID, PW);
				Online_User.put(ID, 1);
				// output.put(ID, writer);
				output_object.put(ID, obj_writer);
				MessageObjects Send_Object = new MessageObjects("Register");
				Send_Object.message = "Register Success! Welcome!" ;
				try{
					obj_writer.writeObject(Send_Object);
				}catch(Exception e){
					System.out.println(e);
				}
				
				// Send Updated Onine Id to each Online Client
				MessageObjects Send_tmp_Object = new MessageObjects("Online");
				String total_Online_id = "";
				for (String online_id : (Set<String>)Online_User.keySet()){
					total_Online_id = total_Online_id + online_id + ",";
				}
				for (String online_id : (Set<String>)Online_User.keySet()){
					ObjectOutputStream tmp_obj_writer = output_object.get(online_id);
					Send_tmp_Object.online_id = total_Online_id;
					try{
						tmp_obj_writer.writeObject(Send_tmp_Object);
					}catch(Exception e){
						System.out.println(e);
					}
				}
			}
			try{
				obj_writer.flush();
			}catch(Exception e){
				System.out.println(e);
			}
		}
		public void HandleLogout(MessageObjects message_obj){
			String ID = message_obj.id;

			if(User_Info.containsKey(ID)){
				if(!Online_User.containsKey(ID)){
					MessageObjects Send_Object = new MessageObjects("Logout");
					Send_Object.err_signal = 1;
					Send_Object.message = "ERROR:This ID isn`t Online!";
					try{
						obj_writer.writeObject(Send_Object);
					}catch(Exception e){
						System.out.println(e);
					}
					
				}else{
					Online_User.remove(ID);
					// output.remove(ID);
					output_object.remove(ID);
					MessageObjects Send_Object = new MessageObjects("Logout");
					Send_Object.message = "Logout Success! Goodbye!";
					try{
						obj_writer.writeObject(Send_Object);
					}catch(Exception e){
						System.out.println(e);
					}

					// Send Updated Onine Id to each Online Client
					MessageObjects Send_tmp_Object = new MessageObjects("Online");
					String total_Online_id = "";
					for (String online_id : (Set<String>)Online_User.keySet()){
						total_Online_id = total_Online_id + online_id + ",";
					}
					for (String online_id : (Set<String>)Online_User.keySet()){
						ObjectOutputStream tmp_obj_writer = output_object.get(online_id);
						Send_tmp_Object.online_id = total_Online_id;
						try{
							tmp_obj_writer.writeObject(Send_tmp_Object);
						}catch(Exception e){
							System.out.println(e);
						}
					}
				}
			}else{
				MessageObjects Send_Object = new MessageObjects("Logout");
				Send_Object.err_signal = 1;
				Send_Object.err_message = "ERROR:Have No this ID!";
				try{
					obj_writer.writeObject(Send_Object);
				}catch(Exception e){
					System.out.println(e);
				}
			}
			try{
				obj_writer.flush();
			}catch(Exception e){
				System.out.println(e);
			}
		}
		public void HandleSend(MessageObjects message_obj){
			String To_Client = message_obj.to_client;
			String from_client = message_obj.from_client;
			String real_message = message_obj.message;

			if(To_Client.length() > 0){
				String[] Clients = To_Client.split(",");
				for (String client_id : Clients){
					if(Online_User.containsKey(client_id)){
						ObjectOutputStream tmp_writer = output_object.get(client_id);
						MessageObjects Send_Object = message_obj;
						Send_Object.tag = "Receive";
						try{
							tmp_writer.writeObject(Send_Object);
							tmp_writer.flush();
						}catch(Exception e){
							System.out.println(e);
						}
					}else if(User_Info.containsKey(client_id)){
						MessageObjects Send_Object = new MessageObjects("Send");
						Send_Object.err_signal = 1;
						Send_Object.err_message = "ERROR:The id="+client_id+" doesn`t Online!";
						try{
							obj_writer.writeObject(Send_Object);
						}catch(Exception e){
							System.out.println(e);
						}
						// Handle_Offline_message
						OfflineMessageObject tmp_offline_message = new OfflineMessageObject(client_id, message_obj);
						Offline_Message.add(tmp_offline_message);
					}else{
						MessageObjects Send_Object = new MessageObjects("Send");
						Send_Object.err_signal = 1;
						Send_Object.err_message = "ERROR:The id="+client_id+" doesn`t Exist!";
						try{
							obj_writer.writeObject(Send_Object);
						}catch(Exception e){
							System.out.println(e);
						}
					}
				}
				MessageObjects Send_Object = new MessageObjects("Send");
				Send_Object.message = "Sending Message have finished!";
				try{
					obj_writer.writeObject(Send_Object);
				}catch(Exception e){
					System.out.println(e);
				}
				
			}else{
				MessageObjects Send_Object = new MessageObjects("Send");
				Send_Object.err_signal = 1;
				Send_Object.err_message = "ERROR:error sending tag format!";
				try{
					obj_writer.writeObject(Send_Object);
				}catch(Exception e){
					System.out.println(e);
				}
				
			}
			try{
				obj_writer.flush();
			}catch(Exception e){
				System.out.println(e);
			}
		}
		public void HandleSendFile(MessageObjects message_obj){
			FileInputStream fis = null;
    		BufferedInputStream bis = null;
			String To_Client = message_obj.to_client;
			String real_path = message_obj.file_path;

			if(To_Client.length() > 0){
				String[] Clients = To_Client.split(",");
				for (String client_id : Clients){
					if(Online_User.containsKey(client_id)){
						ObjectOutputStream tmp_writer = output_object.get(client_id);
						// File myfile = new File(real_path);
						// byte[] mybytearray  = new byte [(int)myfile.length()];
						// try{
						// 	fis = new FileInputStream(myfile);
      //     					fis.read(mybytearray);
      //     					fis.close();
      //     					for (int i = 0; i < mybytearray.length; i++){
      //       					System.out.print((char) mybytearray[i]);
      //    					}
      //    					System.out.print("\n");	
						// }catch(Exception e){
						// 	System.out.println(e);
						// }
						// System.out.println(myfile.getName());
						// System.out.println((int)myfile.length());

						MessageObjects Send_Object = message_obj;
						Send_Object.tag = "ReceiveFile";


						try{
							tmp_writer.writeObject(Send_Object);
							tmp_writer.flush();
						}catch(Exception e){
							System.out.println(e);
						}

					}else{
						MessageObjects Send_Object = new MessageObjects("SendFile");
						Send_Object.err_signal = 1;
						Send_Object.err_message = "ERROR:The id="+client_id+" doesn`t Online!";
						try{
							obj_writer.writeObject(Send_Object);
						}catch(Exception e){
							System.out.println(e);
						}
					}
				}
				MessageObjects Send_Object = new MessageObjects("SendFile");
				Send_Object.message = "Sending Message have finished!";
				try{
					obj_writer.writeObject(Send_Object);
				}catch(Exception e){
					System.out.println(e);
				}
			}else{
				MessageObjects Send_Object = new MessageObjects("SendFile");
				Send_Object.err_signal = 1;
				Send_Object.err_message = "ERROR:error sending tag format!";
				try{
					obj_writer.writeObject(Send_Object);
				}catch(Exception e){
					System.out.println(e);
				}
			}
			try{
				obj_writer.flush();
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}
}