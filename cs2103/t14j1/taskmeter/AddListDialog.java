package cs2103.t14j1.taskmeter;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class AddListDialog extends Dialog {

	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("taskmeter_res");
	protected String result;
	protected Shell shell;
	private   Text text;
	private   boolean button;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public AddListDialog(Shell parent) {
		super(parent, SWT.NONE);
		setText(resourceBundle.getString("app.add.list.title"));
		button = false;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				e.doit = closeAddTask();
			}
		});
		shell.setSize(245, 120);
		shell.setText(getText());
		
		text = new Text(shell, SWT.BORDER);
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				result = text.getText().trim();
			}
		});
		text.setFont(SWTResourceManager.getFont("Segoe UI", 13, SWT.NORMAL));
		text.setBounds(5, 30, 230, 25);
		
		Label lblEnterNewList = new Label(shell, SWT.NONE);
		lblEnterNewList.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		lblEnterNewList.setBounds(5, 5, 230, 20);
		lblEnterNewList.setText("Enter New List Name:");
		
		Button btnAdd = new Button(shell, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				button = true;
				shell.close();
			}
		});
		btnAdd.setBounds(5, 60, 75, 28);
		btnAdd.setText("Add");
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				button = true;
				result = null;
				shell.close();
			}
		});
		btnCancel.setBounds(85, 60, 75, 28);
		btnCancel.setText("Cancel");
		
		shell.setDefaultButton(btnAdd);
	}
	
	private boolean closeAddTask() {
		if (button == false && text.getText().trim().length() > 1) {
			//ask user if they want to save current address book
			MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			box.setText(this.getText());
			box.setMessage("msg.close");
		
			int choice = box.open();
			if(choice == SWT.CANCEL) {
				return false;
			} else if(choice == SWT.YES) {
				result = text.getText().trim();
			} else {
				result = null;
			}
		}

		return true;
	}
	
}
