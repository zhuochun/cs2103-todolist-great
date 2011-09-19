package cs2103.t14j1.taskmeter;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * a task detail dialog for editing task details
 * 
 * @author Zhuochun
 *
 */
public class TaskDetailDialog extends Dialog {

	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("taskmeter_res");
	protected Shell shell;
	protected String[] values;
	private Text txtName;
	private Text txtList;
	private DateTime DateStart;
	private DateTime TimeStart;
	private DateTime TimeEnd;
	private Combo cboPriority;
	private Combo cboStatus;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TaskDetailDialog(Shell parent) {
		super(parent, SWT.NONE);
		setText(resourceBundle.getString("app.task.detail.title"));
	}

	public void setTitle(String title) {
		setText(title);
	}
	
	public void setTask(String[] columns) {
		values = columns;
	}
	
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String[] open() {
		createContents();
		center();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return values;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setSize(405, 188);
		shell.setText(getText());
		
		txtName = new Text(shell, SWT.BORDER);
		txtName.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		txtName.setText("");
		txtName.setBounds(10, 10, 380, 25);
		
		DateStart = new DateTime(shell, SWT.BORDER | SWT.DROP_DOWN);
		DateStart.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		DateStart.setBounds(10, 40, 90, 25);
		
		TimeStart = new DateTime(shell, SWT.BORDER | SWT.TIME | SWT.SHORT);
		TimeStart.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		TimeStart.setBounds(105, 40, 80, 25);
		
		Label lblDash = new Label(shell, SWT.NONE);
		lblDash.setAlignment(SWT.CENTER);
		lblDash.setBounds(190, 45, 20, 15);
		lblDash.setText(resourceBundle.getString("TaskDetailDialog.lblDash.text"));
		
		DateTime DateEnd = new DateTime(shell, SWT.BORDER);
		DateEnd.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		DateEnd.setBounds(215, 40, 90, 25);
		
		TimeEnd = new DateTime(shell, SWT.BORDER | SWT.TIME | SWT.SHORT);
		TimeEnd.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		TimeEnd.setBounds(310, 40, 80, 25);
		
		Label lblPriority = new Label(shell, SWT.NONE);
		lblPriority.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblPriority.setBounds(10, 73, 45, 25);
		lblPriority.setText("Priority");
		
		cboPriority = new Combo(shell, SWT.READ_ONLY);
		cboPriority.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		cboPriority.setItems(new String[] {"Important", "Normal", "Low"});
		cboPriority.setBounds(60, 70, 120, 25);
		cboPriority.select(1);
		
		Label lblList = new Label(shell, SWT.NONE);
		lblList.setAlignment(SWT.RIGHT);
		lblList.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblList.setBounds(165, 73, 40, 25);
		lblList.setText(resourceBundle.getString("TaskDetailDialog.lblList.text")); //$NON-NLS-1$
		
		txtList = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		txtList.setEnabled(false);
		txtList.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		txtList.setBounds(215, 70, 175, 25);
		
		Label lblStatus = new Label(shell, SWT.NONE);
		lblStatus.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblStatus.setBounds(10, 104, 70, 22);
		lblStatus.setText("Completed");
		
		cboStatus = new Combo(shell, SWT.READ_ONLY);
		cboStatus.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		cboStatus.setItems(new String[] {"No", "Yes"});
		cboStatus.setBounds(81, 100, 99, 25);
		cboStatus.select(0);
		
		Button btnSave = new Button(shell, SWT.NONE);
		btnSave.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		btnSave.setBounds(235, 120, 75, 30);
		btnSave.setText("Save");
		
		Button btnClose = new Button(shell, SWT.NONE);
		btnClose.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		btnClose.setBounds(315, 120, 75, 30);
		btnClose.setText("Cancel");

	}
	
    private void center() {
		Rectangle parent = getParent().getBounds();
		Rectangle rect = shell.getBounds ();
		int x = parent.x + (parent.width - rect.width) / 2;
		int y = parent.y + (parent.height - rect.height) / 2;
		shell.setLocation (x, y);
    }
}
