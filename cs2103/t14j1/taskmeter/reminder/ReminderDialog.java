package cs2103.t14j1.taskmeter.reminder;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import cs2103.t14j1.logic.DateFormat;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.taskmeter.RefreshListener;
import cs2103.t14j1.taskmeter.TaskMeter;

/**
 * a concurrent reminder
 * 
 * @author Zhuochun
 *
 */
public class ReminderDialog extends Dialog {

    protected final Shell shell;
    private final Display display;
    private Text txtDisplay;
    private Label lblRemindMe;
    
    private RefreshListener refreshHandler;
    
    private ArrayList<ReminderTask> reminders;
    
    // Exception strings
    private static final String EXCEPTION_NULL_TASK    = "You cannot set a reminder on unexisting task";
    private static final String EXCEPTION_NULL_DATE    = "You cannot set a reminder without a date and time";
    private static final String EXCEPTION_NO_REMINDER  = "You cannot remove a reminder on task that has no reminder";
    private static final String EXCEPTION_BEFORE_NOW   = "The reminder is already in the past";
    private static final String EXCEPTION_HAS_REMINDER = "You have already set a reminder for task \"%1$s\"";
    
    /**
     * Create the dialog.
     * @param parent
     * @param style
     */
    public ReminderDialog(Shell parent) {
        super(parent, SWT.NONE);
        
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
        shell.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                e.doit = false;
                shell.setVisible(false);
                setLabel("Reminder");
                setDisplay("");
            }
        });
        shell.layout();
        
        display   = getParent().getDisplay();
        reminders = new ArrayList<ReminderTask>();
        refreshHandler = null;
        
        createContents();
    }
    
    public void open() {
        shell.open();
    }
    
    public void addReminder(Date date, Task task) {
        if (task == null) {
            throw new NullPointerException(EXCEPTION_NULL_TASK);
        } else if (date == null) {
            throw new NullPointerException(EXCEPTION_NULL_DATE);
        } else if (date.before(DateFormat.getNow())) {
            throw new IllegalArgumentException(EXCEPTION_BEFORE_NOW);
        } else if (task.getReminder() != null) {
            throw new IllegalArgumentException(String.format(EXCEPTION_HAS_REMINDER, task.getName()));
        }
        
        task.setReminder(date);
        
        // check is existing reminder on the same dateTime
        for (ReminderTask t : reminders) {
            // if exists, we update the reminder
            if (t.isSameTime(date)) {
                t.setReminder(task);
                return ;
            }
        }
        // does not exist, add a new reminder
        ReminderTask newTask = new ReminderTask(date, task);
        newTask.setReminder();
        
        // put the reminder into list
        reminders.add(newTask);
    }
    
    public boolean removeReminder(Task task) {
        if (task == null) {
            throw new NullPointerException(EXCEPTION_NULL_TASK);
        } else if (task.getReminder() == null) {
            throw new IllegalArgumentException(EXCEPTION_NO_REMINDER);
        }
        
        for (ReminderTask t : reminders) {
            if (t.hasTask(task)) {
                t.removeTask(task);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shell.setSize(325, 330);
        shell.setText(TaskMeter.getResourceString("ReminderDialog.title"));
        shell.setLayout(new GridLayout(1, false));
        
        lblRemindMe = new Label(shell, SWT.NONE);
        lblRemindMe.setForeground(SWTResourceManager.getColor(165, 42, 42));
        lblRemindMe.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        lblRemindMe.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
        lblRemindMe.setText("Remind:");
        
        txtDisplay = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        txtDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        txtDisplay.setBackground(SWTResourceManager.getColor(245, 255, 250));
        txtDisplay.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        txtDisplay.setText("");
    }
    
    public void addRefreshListener(RefreshListener r) {
        this.refreshHandler = r;
    }
    
    public void removeRefreshListener() {
        this.refreshHandler = null;
    }
    
    public void setDisplay(String str) {
        txtDisplay.setText(str);
    }
    
    public void setLabel(String str) {
        lblRemindMe.setText(str);
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
    
    class ReminderTask {
        protected Date     remindTime;
        protected TaskList tasks;
        protected Runnable runnable;
        
        public ReminderTask(Date date, Task task) {
            tasks = new TaskList("Reminder on " + date.toString());
            tasks.addTask(task);
            remindTime = date;
        }
        
        public void setReminder() {
            runnable = new Runnable() {
                public void run() {
                    setDisplay(getDisplayStr());
                    setLabel(getLabelStr());
                    
                    if (shell.isVisible()) {
                        shell.setFocus();
                    } else {
                        center();
                        shell.open();
                    }
                    
                    for (Task t : tasks) {
                        t.setReminder(null);
                    }
                    
                    refreshHandler.refresh();
                }
            };
            
            display.timerExec(getMilliSecond(), runnable);
        }
        
        public void setReminder(Task task) {
            resetReminder();
            tasks.addTask(task);
            setReminder();
        }
        
        private void resetReminder() {
            display.timerExec(-1, runnable);
        }
        
        public void removeTask(Task task) {
            tasks.removeTask(task);
            task.setReminder(null);
            
            if (tasks.isEmpty()) {
                resetReminder();
            }
        }
        
        private int getMilliSecond() {
            return (int) (remindTime.getTime() - DateFormat.getNow().getTime());
        }
        
        public boolean isSameTime(Date date) {
            return remindTime.compareTo(date) == 0;
        }
        
        public boolean hasTask(Task task) {
            return tasks.hasTask(task);
        }
        
        private String getDisplayStr() {
            StringBuilder str = new StringBuilder();
            
            str.append(tasks.getTask(1));
            
            for (int i = 2; i <= tasks.getSize(); i++) {
                str.append("=========================\n");
                str.append(tasks.getTask(i).toString());
            }
            
            return str.toString();
        }
        
        private String getLabelStr() {
            StringBuilder str = new StringBuilder();
            
            str.append("You have ");
            str.append(tasks.getSize());
            
            if (tasks.getSize() == 1) {
                str.append(" reminder:");
            } else {
                str.append(" reminders:");
            }
            
            return str.toString();
        }
    }
}