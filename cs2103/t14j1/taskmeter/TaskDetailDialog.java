package cs2103.t14j1.taskmeter;

import java.util.Date;
import java.util.MissingResourceException;
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

import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * a task detail dialog for editing task details
 * 
 * @author Zhuochun
 *
 */
public class TaskDetailDialog extends Dialog {

	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("taskmeter_res");
	protected Shell shell;
	private Task task;
	private String result;
	private int mode;
	private Text txtName;
	private Text txtList;
	private DateTime dateStart;
	private DateTime timeStart;
	private DateTime timeEnd;
	private Combo cboPriority;
	private Combo cboStatus;
	private Text txtWhere;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param choice    0 : task detail (default)
	 *                   1 : add new task detail
	 */
	public TaskDetailDialog(Shell parent, int choice) {
		super(parent, SWT.NONE);
		
		mode = choice;
		
		if (mode == 1) {
		    setText(getResourceString("taskDetailDialog.new.title"));
		} else {
		    setText(getResourceString("taskDetailDialog.title"));
		}
		
		task   = new Task();
		result = null;
	}
	
	public void setTask(Task task) {
		this.task = task;
	}
	
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String open() {
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
		
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setSize(405, 293);
		shell.setText(getText());
		
		txtName = new Text(shell, SWT.BORDER);
		txtName.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		txtName.setText(getTaskName());
		txtName.setBounds(10, 10, 380, 25);
		
		dateStart = new DateTime(shell, SWT.BORDER | SWT.DROP_DOWN);
		dateStart.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        toggleEnable(dateStart);
		    }
		});
		dateStart.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseDoubleClick(MouseEvent e) {
		        toggleEnable(dateStart);
		    }
		});
		dateStart.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		dateStart.setBounds(10, 40, 90, 25);
		setDate(dateStart, task.getStartDateTime());
		
		timeStart = new DateTime(shell, SWT.BORDER | SWT.TIME | SWT.SHORT);
		timeStart.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		timeStart.setBounds(105, 40, 80, 25);
		setTime(timeStart, task.getStartDateTime());
		
		Label lblDash = new Label(shell, SWT.NONE);
		lblDash.setAlignment(SWT.CENTER);
		lblDash.setBounds(190, 45, 20, 15);
		lblDash.setText("-");
		
		DateTime dateEnd = new DateTime(shell, SWT.BORDER | SWT.DROP_DOWN);
		dateEnd.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		dateEnd.setBounds(215, 40, 90, 25);
		setDate(dateEnd, task.getEndDateTime());
		
		timeEnd = new DateTime(shell, SWT.BORDER | SWT.TIME | SWT.SHORT);
		timeEnd.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		timeEnd.setBounds(310, 40, 80, 25);
		setTime(timeEnd, task.getEndDateTime());
		
		Button btnAllDay = new Button(shell, SWT.CHECK);
		btnAllDay.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		btnAllDay.setAlignment(SWT.CENTER);
		btnAllDay.setBounds(10, 71, 72, 25);
		btnAllDay.setText("All day");
		
		Label lblDeadline = new Label(shell, SWT.NONE);
		lblDeadline.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblDeadline.setBounds(155, 75, 55, 25);
		lblDeadline.setText("Deadline");
		
		DateTime dateDeadline = new DateTime(shell, SWT.BORDER | SWT.DROP_DOWN);
		dateDeadline.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		dateDeadline.setBounds(215, 72, 90, 24);
		setDate(dateDeadline, task.getDeadline());
		
		DateTime timeDeadline = new DateTime(shell, SWT.BORDER | SWT.TIME | SWT.SHORT);
		timeDeadline.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		timeDeadline.setBounds(310, 71, 80, 24);
		setTime(timeDeadline, task.getDeadline());
		
		Label lblWhere = new Label(shell, SWT.NONE);
		lblWhere.setAlignment(SWT.RIGHT);
		lblWhere.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblWhere.setBounds(10, 102, 45, 25);
		lblWhere.setText(getResourceString("taskDetailDialog.where"));
		
		txtWhere = new Text(shell, SWT.BORDER);
		txtWhere.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		txtWhere.setBounds(65, 100, 325, 25);
		txtWhere.setText(getTaskPlace());
		
		Label lblList = new Label(shell, SWT.NONE);
		lblList.setAlignment(SWT.RIGHT);
		lblList.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblList.setBounds(10, 131, 45, 25);
		lblList.setText(getResourceString("taskDetailDialog.list"));
		
		txtList = new Text(shell, SWT.BORDER);
		//txtList.setEnabled(false);
		txtList.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		txtList.setBounds(65, 128, 325, 25);
		txtList.setText(getTaskList());
		
		Label lblPriority = new Label(shell, SWT.NONE);
		lblPriority.setAlignment(SWT.RIGHT);
		lblPriority.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblPriority.setBounds(10, 162, 45, 25);
		lblPriority.setText(getResourceString("taskDetailDialog.priority"));
		
		cboPriority = new Combo(shell, SWT.READ_ONLY);
		cboPriority.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		cboPriority.setItems(new String[] {" Important", " Normal", " Low"});
		cboPriority.setBounds(65, 159, 170, 25);
		cboPriority.select(getTaskPriority());
		
		Label lblStatus = new Label(shell, SWT.NONE);
		lblStatus.setAlignment(SWT.RIGHT);
		lblStatus.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblStatus.setBounds(10, 193, 45, 22);
		lblStatus.setText(getResourceString("taskDetailDialog.status"));
		
		cboStatus = new Combo(shell, SWT.READ_ONLY);
		cboStatus.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		cboStatus.setItems(new String[] {" Incomplete", " Completed"});
		cboStatus.setBounds(65, 190, 170, 25);
		cboStatus.select(getTaskStatus());
		
		Button btnSave = new Button(shell, SWT.NONE);
		btnSave.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		btnSave.setBounds(234, 225, 75, 30);
		btnSave.setText(getResourceString("button.save"));
		
		Button btnClose = new Button(shell, SWT.NONE);
		btnClose.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		btnClose.setBounds(315, 225, 75, 30);
		btnClose.setText(getResourceString("button.cancel"));

	}

	private void toggleEnable(DateTime dt) {
	    if (dt.isEnabled()) {
	        dt.setEnabled(false);
	    } else {
	        dt.setEnabled(true);
	    }
	}
	
	private int getTaskStatus() {
	    return task.getStatus() ? 1 : 0;
    }

    private int getTaskPriority() {
        return task.getPriority().ordinal();
    }

    private String getTaskList() {
        return task.getList();
    }

    private String getTaskPlace() {
	    return task.getPlace() == null ? "" : task.getPlace();
    }

    private void setDate(DateTime dt, Date date) {
	    if (date != null) {
	        dt.setYear(date.getYear());
	        dt.setMonth(date.getMonth());
	        dt.setDay(date.getDay());
	    } else if (mode == 0) {
	        //dt.setEnabled(false);
	    }
    }
	
	private void setTime(DateTime dt, Date date) {
	    if (date != null) {
	        dt.setHours(date.getHours());
	        dt.setMinutes(date.getMinutes());
	    } else if (mode == 0) {
	        //dt.setEnabled(false);
	    }
	}

    private String getTaskName() {
	    return task.getName() == null ? "" : task.getName();
	}
	
	/**
	 * center the dialog with respect to the application window
	 */
    private void center() {
		Rectangle parent = getParent().getBounds();
		Rectangle rect = shell.getBounds();
		int x = parent.x + (parent.width - rect.width) / 2;
		int y = parent.y + (parent.height - rect.height) / 2;
		shell.setLocation (x, y);
    }
    
    /**
     * Returns a string from the resource bundle. We don't want to crash because
     * of a missing String. Returns the key if not found.
     */
    private static String getResourceString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        } catch (NullPointerException e) {
            return "!" + key + "!";
        }
    }
}
