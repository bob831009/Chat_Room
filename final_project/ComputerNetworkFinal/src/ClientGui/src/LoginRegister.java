import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class LoginRegister {
	
	
	protected Shell shell;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LoginRegister window = new LoginRegister();
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
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
//		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		shell.setSize(450, 280);
		shell.setText("Client");
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 16, SWT.NORMAL));
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
				IdPwdRegister RegisterWindow = new IdPwdRegister();
				RegisterWindow.open();
			}
		});
		btnNewButton_1.setBounds(248, 164, 156, 34);
		btnNewButton_1.setText("registor now");
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 24, SWT.NORMAL));
		btnNewButton.setText("LOGIN");
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
				IdPwdLogin LoginWindow = new IdPwdLogin();
				LoginWindow.open();
			}
		});
		btnNewButton.setBounds(248, 93, 174, 59);
		formToolkit.adapt(btnNewButton, true, true);
		
		Label label = new Label(shell, SWT.NONE);
		label.setAlignment(SWT.CENTER);
//		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
//		label.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		label.setImage(SWTResourceManager.getImage("C:\\Users\\frank\\workspace\\ClientGui\\img\\login_3.png"));
		label.setBounds(24, 43, 206, 141);
		formToolkit.adapt(label, true, true);
		
	}
	
}
