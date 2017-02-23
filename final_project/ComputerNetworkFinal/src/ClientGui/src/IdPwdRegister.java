import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

//class MessageObjects implements Serializable {
//	String message;
//	
//	protected static final long serialVersionUID = 11121222000L;
//	public MessageObjects(String message) {
//		this.message = message;
//	}
//	public String getMessage(){
//		return this.message;
//	}
//}

public class IdPwdRegister {

	static protected Socket mySocket;
	static protected ObjectInputStream in;
	static protected ObjectOutputStream out;
//	static protected BufferedReader in;
//	static protected PrintStream out;
	
	protected Shell shlClient;
	private Text text;
	private Text text_1;
	
	protected String UserID;
	protected String Password;
	

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			IdPwdRegister window = new IdPwdRegister();
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
		createContents();
		shlClient.open();
		shlClient.layout();
		while (!shlClient.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlClient = new Shell();
		shlClient.setSize(450, 282);
		shlClient.setText("Client");
		
		Label lblNewLabel = new Label(shlClient, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 24, SWT.NORMAL));
		lblNewLabel.setBounds(32, 21, 146, 42);
		lblNewLabel.setText("User ID:");
		
		text = new Text(shlClient, SWT.BORDER);
		text.setBounds(32, 69, 157, 28);
		
		Label lblNewLabel_1 = new Label(shlClient, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 24, SWT.NORMAL));
		lblNewLabel_1.setBounds(32, 103, 172, 42);
		lblNewLabel_1.setText("PassWord:");
		
		text_1 = new Text(shlClient, SWT.BORDER | SWT.PASSWORD);
		text_1.setBounds(32, 151, 157, 28);
		
		
		
		Label lblNewLabel_3 = new Label(shlClient, SWT.NONE);
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		lblNewLabel_3.setImage(SWTResourceManager.getImage("C:\\Users\\frank\\workspace\\ClientGui\\img\\mouse_2.png"));
		lblNewLabel_3.setBounds(380, 173, 32, 32);
		
		Button btnNewButton = new Button(shlClient, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserID = text.getText();
				Password = text_1.getText();
				EstablishConnection();
				MessageObjects objWrite = new MessageObjects("Register");
				objWrite.from_client = UserID;
				objWrite.id = UserID;
				objWrite.pw = Password;
				try {
					out.writeObject(objWrite);
					System.out.println("success write obj");
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}	
//				
				try {
					System.out.println("client before read");
					MessageObjects objRead = (MessageObjects)in.readObject();
					int error = objRead.err_signal;
					System.out.println("error:" + error);
//					System.out.println("");
					
					
//					System.out.println("XDD");
//					String error = message.split(">", 2)[1].split(":", 2)[0];
					shlClient.dispose();
					if (error == 1) {
						try {
							mySocket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						LoginRegister StartWindow = new LoginRegister();
						StartWindow.open();
					} else {
						MainPage main = new MainPage(mySocket, in, out, UserID);
						main.open();	
						System.out.println("Client Success Register");
					}
				} catch (ClassNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		});
		btnNewButton.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 16, SWT.NORMAL));
		btnNewButton.setBounds(211, 162, 211, 55);
		btnNewButton.setText("REGISTER");
	}
	protected void EstablishConnection() {
		try {
			String hostname = InetAddress.getLocalHost().getHostAddress();
			int port = 8888;
			mySocket = new Socket(hostname, port);
			in = new ObjectInputStream(mySocket.getInputStream());
			out = new ObjectOutputStream(mySocket.getOutputStream());
//			System.out.print(UserID);
//			System.out.print(Password);
		} catch (IOException e) {
			System.out.println("connection failed");
		}
	}
}
