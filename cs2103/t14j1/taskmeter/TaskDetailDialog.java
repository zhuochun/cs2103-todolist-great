package cs2103.t14j1.taskmeter;

import java.util.Calendar;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;

/**
 * a task detail dialog for editing task details
 * 
 * @author Zhuochun
 * 
 */
public class TaskDetailDialog extends Dialog {

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("taskmeter_res");
    protected Shell    shell;
    private Task       task;
    private int        mode;
    private String     result;
    private Text       txtName;
    private Text       txtList;
    private Text       txtWhere;
    private DateTime   dateStart;
    private DateTime   timeStart;
    private DateTime   dateEnd;
    private DateTime   timeEnd;
    private DateTime   dateDeadline;
    private DateTime   timeDeadline;
    private Button     btnNoDate;
    private Button     btnAllDay;
    private Button     btnNoDeadline;
    private Combo      cboPriority;
    private Combo      cboStatus;

    public static final int EDIT_TASK = 0;
    public static final int ADD_TASK  = 1;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param choice
     *            0 : edit task/task detail (default) 1 : add new task detail
     */
    public TaskDetailDialog(Shell parent, int choice) {
        super(parent, SWT.NONE);

        mode = choice;

        if (mode == ADD_TASK) {
            setText(getResourceString("taskDetailDialog.new.title"));
        } else {
            setText(getResourceString("taskDetailDialog.title"));
        }

        task = new Task();
        result = null;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    /**
     * Open the dialog.
     * 
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
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        shell.setSize(405, 320);
        shell.setText(getText());

        txtName = new Text(shell, SWT.BORDER);
        txtName.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        txtName.setText(getTaskName());
        txtName.setBounds(10, 10, 380, 25);

        createDateTime();

        Label lblWhere = new Label(shell, SWT.NONE);
        lblWhere.setAlignment(SWT.RIGHT);
        lblWhere.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        lblWhere.setBounds(10, 131, 50, 25);
        lblWhere.setText(getResourceString("taskDetailDialog.where"));

        txtWhere = new Text(shell, SWT.BORDER);
        txtWhere.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        txtWhere.setBounds(66, 129, 324, 25);
        txtWhere.setText(getTaskPlace());

        Label lblList = new Label(shell, SWT.NONE);
        lblList.setAlignment(SWT.RIGHT);
        lblList.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        lblList.setBounds(10, 160, 50, 25);
        lblList.setText(getResourceString("taskDetailDialog.list"));

        txtList = new Text(shell, SWT.BORDER);
        txtList.setEnabled(false);
        txtList.setEditable(false);
        txtList.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        txtList.setBounds(66, 157, 324, 25);
        txtList.setText(getTaskList());

        createDropdowns();

        Button btnSave = new Button(shell, SWT.NONE);
        btnSave.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (saveTaskDetail()) {
                    result = "saved";
                    shell.close();
                }
            }
        });
        btnSave.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        btnSave.setBounds(230, 254, 80, 30);
        btnSave.setText(getResourceString("button.save"));

        Button btnClose = new Button(shell, SWT.NONE);
        btnClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                result = null;
                shell.close();
            }
        });
        btnClose.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        btnClose.setBounds(315, 254, 75, 30);
        btnClose.setText(getResourceString("button.cancel"));

    }

    private void createDateTime() {
        dateStart = new DateTime(shell, SWT.BORDER | SWT.DROP_DOWN);
        dateStart.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        dateStart.setBounds(10, 40, 90, 25);

        timeStart = new DateTime(shell, SWT.BORDER | SWT.TIME | SWT.SHORT);
        timeStart.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        timeStart.setBounds(105, 40, 80, 25);

        Label lblDash = new Label(shell, SWT.NONE);
        lblDash.setAlignment(SWT.CENTER);
        lblDash.setBounds(190, 45, 20, 15);
        lblDash.setText("-");

        dateEnd = new DateTime(shell, SWT.BORDER | SWT.DROP_DOWN);
        dateEnd.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        dateEnd.setBounds(215, 40, 90, 25);

        timeEnd = new DateTime(shell, SWT.BORDER | SWT.TIME | SWT.SHORT);
        timeEnd.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        timeEnd.setBounds(310, 40, 80, 25);

        btnNoDate = new Button(shell, SWT.CHECK);
        btnNoDate.setAlignment(SWT.CENTER);
        btnNoDate.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        btnNoDate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                toggleEnable(dateStart);
                toggleEnable(dateEnd);
                // toggle time as well
                if (!dateStart.isEnabled()) {
                    timeStart.setEnabled(false);
                    timeEnd.setEnabled(false);
                    btnAllDay.setSelection(true);
                    btnAllDay.setEnabled(false);
                } else {
                    btnAllDay.setEnabled(true);
                }
            }
        });
        btnNoDate.setBounds(10, 70, 97, 23);
        btnNoDate.setText("Without Date");

        btnAllDay = new Button(shell, SWT.CHECK);
        btnAllDay.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                toggleEnable(timeStart);
                toggleEnable(timeEnd);
            }
        });
        btnAllDay.setAlignment(SWT.CENTER);
        btnAllDay.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        btnAllDay.setBounds(120, 70, 60, 23);
        btnAllDay.setText("All day");

        setStartEndDateTime();

        Label lblDeadline = new Label(shell, SWT.NONE);
        lblDeadline.setAlignment(SWT.RIGHT);
        lblDeadline.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        lblDeadline.setBounds(10, 101, 50, 25);
        lblDeadline.setText("Deadline");

        btnNoDeadline = new Button(shell, SWT.CHECK);
        btnNoDeadline.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                toggleEnable(dateDeadline);
                toggleEnable(timeDeadline);
            }
        });
        btnNoDeadline.setAlignment(SWT.CENTER);
        btnNoDeadline.setBounds(255, 99, 90, 23);
        btnNoDeadline.setText("No Deadline");

        dateDeadline = new DateTime(shell, SWT.BORDER | SWT.DROP_DOWN);
        dateDeadline.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        dateDeadline.setBounds(66, 99, 90, 24);

        timeDeadline = new DateTime(shell, SWT.BORDER | SWT.TIME | SWT.SHORT);
        timeDeadline.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        timeDeadline.setBounds(162, 99, 80, 24);

        setDeadline();
    }

    private void createDropdowns() {
        Label lblPriority = new Label(shell, SWT.NONE);
        lblPriority.setAlignment(SWT.RIGHT);
        lblPriority.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        lblPriority.setBounds(10, 191, 50, 25);
        lblPriority.setText(getResourceString("taskDetailDialog.priority"));

        cboPriority = new Combo(shell, SWT.READ_ONLY);
        cboPriority.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        cboPriority.setItems(new String[] { " Important", " Normal", " Low" });
        cboPriority.setBounds(66, 188, 170, 25);
        cboPriority.select(getTaskPriority());

        Label lblStatus = new Label(shell, SWT.NONE);
        lblStatus.setAlignment(SWT.RIGHT);
        lblStatus.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        lblStatus.setBounds(10, 222, 50, 22);
        lblStatus.setText(getResourceString("taskDetailDialog.status"));

        cboStatus = new Combo(shell, SWT.READ_ONLY);
        cboStatus.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        cboStatus.setItems(new String[] { " Incomplete", " Completed" });
        cboStatus.setBounds(66, 219, 170, 25);
        cboStatus.select(getTaskStatus());
    }

    private void toggleEnable(DateTime dt) {
        if (dt.isEnabled()) {
            dt.setEnabled(false);
        } else {
            dt.setEnabled(true);
        }
    }

    private String getTaskName() {
        return task.getName() == null ? "" : task.getName();
    }

    private String getTaskPlace() {
        return task.getPlace() == null ? "" : task.getPlace();
    }

    private String getTaskList() {
        return task.getList();
    }

    private int getTaskPriority() {
        return task.getPriority().ordinal();
    }

    private int getTaskStatus() {
        return task.getStatus() ? 1 : 0;
    }

    private void setStartEndDateTime() {
        boolean hasDate = task.getStartDate() != null && task.getEndDate() != null;
        boolean hasTime = task.getStartTime() != null && task.getEndTime() != null;

        if (hasDate) {
            setDate(dateStart, task.getStartDateTime());
            setDate(dateEnd, task.getEndDateTime());
            btnNoDate.setSelection(false);

            if (hasTime) {
                setTime(timeStart, task.getStartDateTime());
                setTime(timeEnd, task.getEndDateTime());
                btnAllDay.setSelection(false);
            } else if (mode == EDIT_TASK) {
                timeStart.setEnabled(false);
                timeEnd.setEnabled(false);
                btnAllDay.setSelection(true);
            }
        } else if (mode == EDIT_TASK) {
            dateStart.setEnabled(false);
            dateEnd.setEnabled(false);
            btnNoDate.setSelection(true);

            timeStart.setEnabled(false);
            timeEnd.setEnabled(false);
            btnAllDay.setSelection(true);
            btnAllDay.setEnabled(false);
        }
    }

    private void setDeadline() {
        boolean hasDeadline = task.getDeadline() != null;

        if (hasDeadline) {
            setDate(dateDeadline, task.getDeadline());
            setTime(timeDeadline, task.getDeadline());
            btnNoDeadline.setSelection(false);
        } else if (mode == EDIT_TASK) {
            dateDeadline.setEnabled(false);
            timeDeadline.setEnabled(false);
            btnNoDeadline.setSelection(true);
        }
    }

    private void setDate(DateTime dt, Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            dt.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }
    }

    private void setTime(DateTime dt, Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            dt.setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        }
    }

    private boolean saveTaskDetail() {
        String newName = getText(txtName);
        if (newName == null) {
            displayError("Task Name Cannot be Empty");
            return false;
        }

        String newPlace = getText(txtWhere);
        // String newList = getText(txtWhere);
        Priority newPriority = Priority.valueOf(cboPriority.getText().trim().toUpperCase());

        Calendar newStartDT = null;
        Calendar newEndDT = null;
        Long newDuration = null;
        if (!btnNoDate.getSelection()) { // if no date is not selected
            newStartDT = Calendar.getInstance();
            newEndDT = Calendar.getInstance();
            getDateSet(dateStart, newStartDT);
            getDateSet(dateEnd, newEndDT);

            if (!btnAllDay.getSelection()) { // if all day is not selected
                getTimeSet(timeStart, newStartDT);
                getTimeSet(timeEnd, newEndDT);

                newDuration = (newEndDT.getTimeInMillis() - newStartDT.getTimeInMillis()) / 1000;
            }

            if (newEndDT.compareTo(newStartDT) < 0) {
                displayError("End Date and Time should come after Start Date and Time");
                return false;
            }
        }

        Calendar newDeadlineDT = null;
        if (!btnNoDeadline.getSelection()) { // if no deadline is not selected
            newDeadlineDT = Calendar.getInstance();

            getDateSet(dateDeadline, newDeadlineDT);
            getTimeSet(timeDeadline, newDeadlineDT);
        }

        Boolean newStatus = Task.INCOMPLETE;
        newStatus = cboStatus.getText().equals(" Completed") ? Task.COMPLETED : Task.INCOMPLETE;

        // set new values
        task.setName(newName);
        task.setPlace(newPlace);
        task.setPriority(newPriority);
        task.setStartDateTime(newStartDT == null ? null : newStartDT.getTime());
        task.setEndDateTime(newEndDT == null ? null : newEndDT.getTime());
        task.setDuration(newDuration);
        task.setDeadline(newDeadlineDT == null ? null : newDeadlineDT.getTime());
        task.setStatus(newStatus);

        return true;
    }

    private void getDateSet(DateTime date, Calendar ndate) {
        ndate.set(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0);
    }

    private void getTimeSet(DateTime time, Calendar ndate) {
        ndate.set(Calendar.HOUR_OF_DAY, time.getHours());
        ndate.set(Calendar.MINUTE, time.getMinutes());
        ndate.set(Calendar.SECOND, 0);
    }

    private String getText(Text s) {
        String str = s.getText().trim();
        str = (str == "") ? null : str;
        return str;
    }

    private void displayError(String msg) {
        MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
        box.setText("Error");
        box.setMessage(msg);
        box.open();
    }

    /**
     * center the dialog with respect to the application window
     */
    private void center() {
        Rectangle parent = getParent().getBounds();
        Rectangle rect = shell.getBounds();
        int x = parent.x + (parent.width - rect.width) / 2;
        int y = parent.y + (parent.height - rect.height) / 2;
        shell.setLocation(x, y);
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