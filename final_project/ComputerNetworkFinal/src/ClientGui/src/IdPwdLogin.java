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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

//class MessageObjects implements Serializable {
//	String message;
//	public MessageObjects(String message) {
//		this.message = message;
//	}
//	public String getMessage(){
//		return this.message;
//	}
//}

public class IdPwdLogin {

	static protected Socket mySocket;
	static protected ObjectInputStream in;
	static protected ObjectOutputStream out;
	
	protected String UserID;
	protected String Password;
	
	protected Shell shlClient;
	private Label lblNewLabel_1;
	private Text text;
	private Text text_1;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			IdPwdLogin window = new IdPwdLogin();
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
//		shlClient.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		shlClient.setSize(450, 280);
		shlClient.setText("Client");
		
		lblNewLabel_1 = new Label(shlClient, SWT.NONE);
//		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		lblNewLabel_1.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 24, SWT.NORMAL));
		lblNewLabel_1.setBounds(32, 21, 146, 42);
		lblNewLabel_1.setText("User ID:");
		
		
		text = new Text(shlClient, SWT.BORDER);
		text.setBounds(32, 69, 157, 28);
		
		Label lblNewLabel_2 = new Label(shlClient, SWT.NONE);
//		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		lblNewLabel_2.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 24, SWT.NORMAL));
		lblNewLabel_2.setBounds(32, 103, 172, 42);
		lblNewLabel_2.setText("PassWord:");
		
		text_1 = new Text(shlClient, SWT.BORDER | SWT.PASSWORD);
		text_1.setBounds(32, 151, 157, 28);
		
		Label lblNewLabel = new Label(shlClient, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		lblNewLabel.setImage(SWTResourceManager.getImage("C:\\Users\\frank\\workspace\\ClientGui\\img\\mouse_2.png"));
		lblNewLabel.setBounds(380, 155, 30, 40);

		Button btnNewButton = new Button(shlClient, SWT.NONE);
		btnNewButton.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		btnNewButton.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 16, SWT.NORMAL));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserID = text.getText();
				Password = text_1.getText();
				EstablishConnection();
				MessageObjects objWrite = new MessageObjects("Login");
				objWrite.from_client = UserID;
				objWrite.id = UserID;
				objWrite.pw = Password;
				try {
					out.writeObject(objWrite);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				try {
					MessageObjects objRead = (MessageObjects)in.readObject();
					int error = objRead.err_signal;
					shlClient.setVisible(false);
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
						System.out.println("Entering Main Window");
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
		btnNewButton.setBounds(223, 147, 191, 55);
		btnNewButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnNewButton.setText("LOGIN");
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
