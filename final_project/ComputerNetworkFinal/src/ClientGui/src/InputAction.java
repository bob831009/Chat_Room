import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class InputAction implements Runnable{
	
	protected static String userID;
	protected ObjectInputStream in;
	protected ObjectOutputStream out;
	static String[] OnlineUser;
	static String dirPath = "C:\\Users\\frank\\Desktop\\CN\\";
	
	public InputAction(String userID, ObjectInputStream in, ObjectOutputStream out) {
		this.userID = userID;
		this.in = in;
		this.out = out;
	}
	public static String[] getOnlineUser() {
		return OnlineUser;
	}
	
	public void run() {
		
		
		while(true) {
			
			System.out.println("Waiting input:");
			MessageObjects objRead;
			try {
				objRead = (MessageObjects)in.readObject();
				String type = objRead.tag;
				
				System.out.println(type);
				
				if (type.equals("Online")) {
					String[] users = objRead.online_id.split(",");
					OnlineUser = users;

					MainPage.updateUser(OnlineUser);
				} else if (type.equals("Logout")) {
					MainPage.userLogout();
					break;
				} else if (type.equals("ReceiveFile")) {	
					String fromClient = objRead.from_client;
					String chatroomName = getChatroomName(objRead.to_client.split(","));
					
					if (!fromClient.equals(userID)) {
						byte[] fileByte = objRead.file_bytearray;
//						Path savePath = Paths.get(dirPath + userID);
						File savePath = new File(dirPath + userID);
						if (!savePath.exists()) 
							savePath.mkdir();
						FileOutputStream fos = new FileOutputStream(dirPath + userID + "\\" + objRead.file_name);
						fos.write(fileByte);
						fos.close();
					}
					int chatroomIndex = MainPage.checkChatroomExist(chatroomName);
					if (chatroomIndex >= 0) {
						MainPage.sendMessageToChatroom(chatroomIndex, fromClient, objRead.message);
						
					} else {
						if (chatroomName.split(",").length > 2)
							MainPage.updateGroup(chatroomName, chatroomName.split(","));
//						System.out.println("user: " + userID + " creating chatroom");
						int newIndex = MainPage.createChatroom(chatroomName);
//						System.out.println("new index : " + newIndex);
						MainPage.sendMessageToChatroom(newIndex, fromClient, objRead.message);
					}
					
				} else if (type.equals("SendFile")) {
					if(objRead.err_signal == 1) {
						System.out.println(objRead.err_message);
					} else {
						
						System.out.println(objRead.message);
					}
					
				} else if (type.equals("Send")) {
					if (objRead.err_signal == 1) {
						System.out.println(objRead.err_message);
					} else {
						System.out.println(objRead.message);
					}
				} else if (type.equals("Receive")) {
					String[] userArray = objRead.to_client.split(",");
					String fromClient = objRead.from_client;
					String content = objRead.message;
					String chatroomName = getChatroomName(userArray);
					
					int chatroomIndex = MainPage.checkChatroomExist(chatroomName);
					System.out.println(chatroomIndex);
					if (chatroomIndex >= 0) {
						MainPage.sendMessageToChatroom(chatroomIndex, fromClient, content);
					} else {
						if (userArray.length > 2)
							MainPage.updateGroup(chatroomName, userArray);
//						System.out.println("user: " + userID + " creating chatroom");
						int newIndex = MainPage.createChatroom(chatroomName);
//						System.out.println("new index : " + newIndex);
						MainPage.sendMessageToChatroom(newIndex, fromClient, content);
					}
		
				} else {
					System.out.println("unexpected message!");
				}
			
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	public static String getChatroomName(String[] userArray) {
		String chatroomName = userID;
		int flag = 0;
		//String chatroomName = userID;
		for (int i = 0; i < userArray.length; i++) {
			if (!userID.equals(userArray[i]) || flag == 1) {
				chatroomName += ("," + userArray[i]);
			} else if (userID.equals(userArray[i]) && flag == 0) {
				flag = 1;
			}
		}
		return chatroomName;
	}
}
