package cs2103.t14j1.taskmeter;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

public class TaskDetailDialog extends Dialog {

	//private static ResourceBundle resourceBundle = ResourceBundle.getBundle("taskmeter_res");
	protected String[] values;
	protected Shell shell;
	private Text txtName;
	private Label lblDeadline;
	private Text txtList;
	private Text txtDuration;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TaskDetailDialog(Shell parent) {
		super(parent, SWT.NONE);
		//setText(resourceBundle.getString("app.task.detail.title"));
	}

	public void setTitle(String title) {
		setText(title);
	}
	
	public void setTask(String[] columns) {
		
	}
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String[] open() {
		createContents();
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
		shell = new Shell(getParent(), SWT.BORDER | SWT.CLOSE);
		shell.setSize(450, 300);
		shell.setText(getText());
		
		Label lblTask = new Label(shell, SWT.NONE);
		lblTask.setBounds(10, 10, 32, 15);
		lblTask.setText("Task");
		
		txtName = new Text(shell, SWT.BORDER);
		txtName.setText("");
		txtName.setBounds(48, 10, 386, 21);
		
		lblDeadline = new Label(shell, SWT.NONE);
		lblDeadline.setBounds(10, 46, 55, 15);
		lblDeadline.setText("Deadline");
		
		DateTime dateTime = new DateTime(shell, SWT.BORDER);
		dateTime.setBounds(81, 47, 80, 24);
		
		Label lblList = new Label(shell, SWT.NONE);
		lblList.setBounds(10, 93, 32, 15);
		lblList.setText("List");
		
		txtList = new Text(shell, SWT.BORDER);
		txtList.setText("List");
		txtList.setBounds(48, 93, 73, 21);
		
		Label lblPriority = new Label(shell, SWT.NONE);
		lblPriority.setBounds(10, 137, 55, 15);
		lblPriority.setText("Priority");
		
		Combo cboPriority = new Combo(shell, SWT.NONE);
		cboPriority.setBounds(73, 137, 88, 23);
		
		Label lblDuration = new Label(shell, SWT.NONE);
		lblDuration.setBounds(209, 46, 55, 15);
		lblDuration.setText("Duration");
		
		txtDuration = new Text(shell, SWT.BORDER);
		txtDuration.setText("Duration");
		txtDuration.setBounds(270, 46, 73, 21);
		
		Label lblStatus = new Label(shell, SWT.NONE);
		lblStatus.setBounds(195, 93, 55, 15);
		lblStatus.setText("Status");
		
		Combo cboStatus = new Combo(shell, SWT.NONE);
		cboStatus.setBounds(270, 107, 88, 23);
		
		Button btnSave = new Button(shell, SWT.NONE);
		btnSave.setBounds(204, 192, 75, 25);
		btnSave.setText("Save");
		
		Button btnClose = new Button(shell, SWT.NONE);
		btnClose.setBounds(285, 192, 75, 25);
		btnClose.setText("Close");

	}
}
