
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class MainPage {
	
	protected static Shell shlMainpage;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	static protected Socket mySocket;
	protected static ObjectInputStream in;
	protected static ObjectOutputStream out;
	static List list, list_1;
	private static ArrayList<ChatRoom> chatroomList = new ArrayList<ChatRoom>();
	private static ArrayList<GroupChat> groupList = new ArrayList<GroupChat>();

	static String[] OnlineUser;
	static String userID;
	
	public MainPage(Socket mySocket, ObjectInputStream in, ObjectOutputStream out, String userID) {
		// TODO Auto-generated constructor stub
		MainPage.mySocket = mySocket;
		MainPage.in = in;
		MainPage.out = out;
		MainPage.userID = userID;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainPage window = new MainPage(mySocket, in, out, userID);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
//		new InputAction(in, out).start();
		Thread t = new Thread(new InputAction(userID, in, out));
		
		t.start();
		
		createContents();
		shlMainpage.open();
		shlMainpage.layout();
		while (!shlMainpage.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlMainpage = new Shell();
		shlMainpage.setSize(450, 322);
		shlMainpage.setText(userID);
		
		list = new List(shlMainpage, SWT.BORDER | SWT.V_SCROLL);
		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] selectedIndex = list.getSelectionIndices();
				String chatroomName = userID + "," + OnlineUser[selectedIndex[0]];
				if (checkChatroomExist(chatroomName) < 0)
					createChatroom(chatroomName);
			}
		});
		list.setBounds(56, 66, 90, 182);
		OnlineUser = InputAction.getOnlineUser();
		list.setItems(OnlineUser);
	    formToolkit.adapt(list, true, true);
		
		Label lblNewLabel = formToolkit.createLabel(shlMainpage, "User List:", SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 14, SWT.NORMAL));
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		
		lblNewLabel.setBounds(56, 27, 90, 33);
		
		list_1 = new List(shlMainpage, SWT.BORDER | SWT.V_SCROLL);
		list_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] selectedIndex = list_1.getSelectionIndices();
				String chatroomName = groupList.get(selectedIndex[0]).getGroupName();
				if (checkChatroomExist(chatroomName) < 0)
					createChatroom(chatroomName);
			}
		});
		list_1.setBounds(179, 66, 133, 182);
		formToolkit.adapt(list_1, true, true);
		
		Label lblGroupList = formToolkit.createLabel(shlMainpage, "Group List:", SWT.NONE);
		lblGroupList.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		lblGroupList.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		lblGroupList.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 14, SWT.NORMAL));
		lblGroupList.setBounds(179, 27, 133, 33);
		
		Button btnNewButton = new Button(shlMainpage, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddGroup addgroup = new AddGroup(userID);
				addgroup.open();
			}
		});
		btnNewButton.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 10, SWT.NORMAL));
		btnNewButton.setBounds(329, 215, 99, 33);
		btnNewButton.setText("Modify Group");
		
		Button btnLogout = new Button(shlMainpage, SWT.NONE);
		btnLogout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String message = new String("<Logout>ID:" + userID);
				MessageObjects objWrite = new MessageObjects("Logout");
				objWrite.from_client = userID;
				objWrite.id = userID;
				try {
					out.writeObject(objWrite);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnLogout.setBounds(334, 27, 94, 29);
		formToolkit.adapt(btnLogout, true, true);
		btnLogout.setText("Logout");
	}
	
	public static void updateUser (String[] user) {
		OnlineUser = user;
		System.out.println("in update");

		Display.getDefault().asyncExec(new Runnable() {
			@Override public void run () {
				list.setItems(OnlineUser);
			}
		});
	}
	public static void updateGroup (String groupName, String[] groupMember) {
		ArrayList<String> groupNames = new ArrayList<String>();
		int flag = 0;
		for (int i = 0; i < groupList.size(); i++) {
			GroupChat tempGroup = groupList.get(i);
			if (groupName.equals(tempGroup.getGroupName())) {
				tempGroup.modifyGroupMember(groupMember);
				flag = 1;
				break;
			} 
		}
		if (flag == 0) {
			groupList.add(new GroupChat(groupName, groupMember));
		}
		for (GroupChat temp : groupList) {
			groupNames.add(temp.groupName);
		}
//		for (int i = 0; i < groupNames.size(); i++)
//			System.out.println(groupNames.get(i));
		Display.getDefault().asyncExec(new Runnable() {
			@Override public void run () {
				list_1.setItems((String[]) groupNames.toArray(new String[groupNames.size()]));
			}
		});
	}
	public static int createChatroom(String chatroomName) {

		System.out.println("before creating chatroom: ");
		for (int i = 0; i < chatroomList.size(); i++)
			System.out.println(chatroomList.get(i).getChatroomName());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		ChatRoom newRoom = new ChatRoom(out, chatroomName);	
		chatroomList.add(newRoom);

		Thread t = new Thread(newRoom);
		t.start();
		
		System.out.println("creating chatroom: ");
		for (int i = 0; i < chatroomList.size(); i++)
			System.out.println(chatroomList.get(i).getChatroomName());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chatroomList.indexOf(newRoom);
		
	}
	public static void deleteChatroom(String chatroomName) {
		System.out.println("delete chatroom!");
		
		for (ChatRoom tempRoom : chatroomList) {
			if (tempRoom.getChatroomName().equals(chatroomName)) {
				chatroomList.remove(tempRoom);
				return;
			}
		}
//		groupList.remove(chatroomName);
//		pipesOutput.remove(chatroomName);
//		chatroomList.remove(chatroomName);
	}
	
	public static int checkChatroomExist (String chatroomName) {
		String[] chatroomUsers = chatroomName.split(",");
		Set<String> userSet = new HashSet<String>();
		for (String user : chatroomUsers)
			userSet.add(user);
		for (ChatRoom tempRoom : chatroomList) {
			System.out.println("check: " + tempRoom.getChatroomName());
			String[] tempUsers = tempRoom.getChatroomName().split(",");
			Set<String> tempSet = new HashSet<String>();
			for (String user : tempUsers)
				tempSet.add(user);
			if (userSet.equals(tempSet)) 
				return chatroomList.indexOf(tempRoom);
		}
		return -1;
	}
	
	public static void sendMessageToChatroom (int chatroomIndex, String sendFrom, String content) {
		String messageToChatroom = sendFrom + " : " + content;
		chatroomList.get(chatroomIndex).receiveMessage(messageToChatroom);
	}
	public static void userLogout() {
		try {
			mySocket.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Display.getDefault().asyncExec(new Runnable() {
			@Override public void run() {
				shlMainpage.dispose();
				LoginRegister newLoginRegister = new LoginRegister();
				newLoginRegister.open();
			}
		});
		
	}
}


