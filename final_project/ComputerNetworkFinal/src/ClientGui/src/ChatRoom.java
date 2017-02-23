

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ChatRoom implements Runnable{

	protected Shell shlUser;
	private Text text;
	private static Text txtAgjiagdsa;
	String path_selected = new String();
	protected ObjectOutputStream out;
	static String RoomName;
	String[] users;
	/**
	 * Launch the application.
	 * @param string 
	 * @param out2 
	 * @param in2 
	 * @param args
	 */
	public ChatRoom(ObjectOutputStream out, String users) {
		this.out = out;
		this.users = users.split(" ");
		RoomName = users;
	}
	public void run() {
		open();
	}

	/**
	 * Open the window.
	 * @param  
	 */
	public void open() {
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override public void run() {
				System.out.println("Open new chatroom: " + RoomName);
				Display display = Display.getDefault();
				createContents(RoomName);
				shlUser.open();
				shlUser.layout();
				while (!shlUser.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
				MainPage.deleteChatroom(RoomName);
			}
		});
	}
	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents(String RoomName) {
		shlUser = new Shell();
		shlUser.setSize(600, 450);
		shlUser.setText(RoomName);
		
		Button btnNewButton = new Button(shlUser, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!text.getText().isEmpty()) {
					String content = text.getText();
					
					MessageObjects objWrite = new MessageObjects("Send");
					objWrite.from_client = RoomName.split(",")[0];
					objWrite.to_client = RoomName;
					objWrite.message = content;
					try {
						out.writeObject(objWrite);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				text.setText("");
			}
		});
		btnNewButton.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 16, SWT.NORMAL));
		btnNewButton.setBounds(465, 286, 107, 36);
		btnNewButton.setText("send");
		
		Button btnNewButton_1 = new Button(shlUser, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				SetPath setpath = new SetPath();
				FileDialog file = new FileDialog(shlUser, SWT.SAVE);
				file.setText("select file");
			    file.setFilterPath("c:\\");
			    path_selected = file.open();
			    if (path_selected == null) {
			    	System.out.println("no selected");
			    	return;
			    }
			    File files = new File(path_selected);
			    byte[] fileByte = new byte[(int)files.length()];
			    
			    try {
			    	FileInputStream fis = new FileInputStream(files);
					fis.read(fileByte);
					fis.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			    
			    MessageObjects objWrite = new MessageObjects("SendFile");
			    objWrite.from_client = RoomName.split(",")[0];
			    objWrite.to_client = RoomName;
			    objWrite.file_name = files.getName();
			    objWrite.file_path = path_selected;
			    objWrite.file_bytearray = fileByte;
			    objWrite.message = "send a file " + files.getName();
			    try {
					out.writeObject(objWrite);
					System.out.println("path:" + path_selected);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("setting path");
			}
		});
		btnNewButton_1.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 14, SWT.NORMAL));
		btnNewButton_1.setBounds(465, 341, 107, 36);
		btnNewButton_1.setText("UPLOAD");
		
		text = new Text(shlUser, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text.setBounds(10, 284, 434, 93);
		
		txtAgjiagdsa = new Text(shlUser, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
//		txtAgjiagdsa.setText();
		txtAgjiagdsa.setBounds(10, 10, 562, 245);
	}
	public String getChatroomName() {
		return RoomName;
	}
	public void receiveMessage(String message) {
		System.out.println(RoomName + " chatroom receive message");
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override public void run () {
				txtAgjiagdsa.append(message + "\n"); // setText(txtAgjiagdsa.getText() + "<html>fnord<br />foo</html>" + message);
			}
		});	
	}
}
