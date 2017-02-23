

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AddGroup {

	protected Shell shlAddGroup;
	private Text text_1;
	private String userID;
	String groupName;
	String[] groupMember;

	public AddGroup(String userID) {
		// TODO Auto-generated constructor stub
		this.userID = userID;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AddGroup window = new AddGroup("userID");
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
		shlAddGroup.open();
		shlAddGroup.layout();
		while (!shlAddGroup.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlAddGroup = new Shell();
		shlAddGroup.setSize(368, 203);
		shlAddGroup.setText("Modify Group");
		
		Label lblNewLabel_1 = new Label(shlAddGroup, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 16, SWT.NORMAL));
		lblNewLabel_1.setBounds(90, 23, 150, 35);
		lblNewLabel_1.setText("Add/Remove:");
		
		text_1 = new Text(shlAddGroup, SWT.BORDER);
		text_1.setBounds(25, 64, 266, 35);
		
		Button btnNewButton = new Button(shlAddGroup, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				groupName = userID + "," + text_1.getText();
				groupMember = groupName.split(",");
				MainPage.updateGroup(groupName, groupMember);
				shlAddGroup.dispose();
//				shlAddGroup.setVisible(false);
			}
		});
		btnNewButton.setFont(SWTResourceManager.getFont(".Helvetica Neue DeskInterface", 16, SWT.NORMAL));
		btnNewButton.setBounds(103, 111, 137, 35);
		btnNewButton.setText("Modify");

	}
}
