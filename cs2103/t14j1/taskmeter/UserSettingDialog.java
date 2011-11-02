package cs2103.t14j1.taskmeter;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import cs2103.t14j1.logic.DateFormat;
import cs2103.t14j1.storage.user.User;
import cs2103.t14j1.taskmeter.reminder.Reminder;

public class UserSettingDialog extends Dialog {

    protected Shell shlUserSettings;
    private boolean result;
    

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public UserSettingDialog(Shell parent) {
        super(parent, SWT.NONE);
        result = false;
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public boolean open() {
        createContents();
        center();
        shlUserSettings.open();
        shlUserSettings.layout();
        Display display = getParent().getDisplay();
        while (!shlUserSettings.isDisposed()) {
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
        shlUserSettings = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        shlUserSettings.setSize(455, 350);
        shlUserSettings.setText("User Settings");

        sortMethod();
        
        Label label0 = new Label(shlUserSettings, SWT.SEPARATOR | SWT.HORIZONTAL);
        label0.setBounds(5, 75, 435, 2);
        
        autoSave();;
        
        Label label1 = new Label(shlUserSettings, SWT.SEPARATOR | SWT.HORIZONTAL);
        label1.setBounds(5, 118, 435, 2);
        
        autoComplete();
        
        Label label2 = new Label(shlUserSettings, SWT.SEPARATOR | SWT.HORIZONTAL);
        label2.setBounds(5, 155, 435, 2);
        
        defaultReminder();
        
        Label label3 = new Label(shlUserSettings, SWT.SEPARATOR | SWT.HORIZONTAL);
        label3.setBounds(5, 195, 435, 2);
        
        displayMethod();
    }

    private void defaultReminder() {
        final String[] reminderOptions = {"Start Time", "End Time", "Deadline"};
        final Reminder[] reminders = {Reminder.START, Reminder.END, Reminder.DEADLINE};
        
        final Label lblDefaultReminder = new Label(shlUserSettings, SWT.NONE);
        lblDefaultReminder.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        lblDefaultReminder.setBounds(10, 165, 195, 20);
        lblDefaultReminder.setText("Default Reminder Parameter");
        
        final Combo cbReminder = new Combo(shlUserSettings, SWT.READ_ONLY);
        cbReminder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.defaultRemind = reminders[cbReminder.getSelectionIndex()];
            }
        });
        cbReminder.setItems(reminderOptions);
        cbReminder.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        cbReminder.setBounds(215, 162, 180, 28);
        cbReminder.select(User.defaultRemind.ordinal());
    }

    private void displayMethod() {
        final Button btnDisplayDurationIn = new Button(shlUserSettings, SWT.CHECK);
        btnDisplayDurationIn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.useAbbreviate = btnDisplayDurationIn.getSelection();
                result = true;
            }
        });
        btnDisplayDurationIn.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        btnDisplayDurationIn.setBounds(10, 205, 225, 20);
        btnDisplayDurationIn.setText("Display Duration in Abbreviate");
        btnDisplayDurationIn.setSelection(User.useAbbreviate);
        
        Label lbldDay = new Label(shlUserSettings, SWT.NONE);
        lbldDay.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
        lbldDay.setBounds(240, 208, 194, 20);
        lbldDay.setText("(D - Day, H - Hour, M - Minute)");
        
        final Button btnDisplayDeadlineLike = new Button(shlUserSettings, SWT.CHECK);
        btnDisplayDeadlineLike.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.useDurationLike = btnDisplayDeadlineLike.getSelection();
                result = true;
            }
        });
        btnDisplayDeadlineLike.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        btnDisplayDeadlineLike.setBounds(10, 230, 225, 20);
        btnDisplayDeadlineLike.setText("Display Deadline Like Duration");
        btnDisplayDeadlineLike.setSelection(User.useDurationLike);
        
        Label lblegHour = new Label(shlUserSettings, SWT.NONE);
        lblegHour.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
        lblegHour.setBounds(240, 233, 98, 15);
        lblegHour.setText("(eg. 1 Hour Ago)");
        
        Label lblChooseDateAnd = new Label(shlUserSettings, SWT.NONE);
        lblChooseDateAnd.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        lblChooseDateAnd.setBounds(10, 260, 404, 20);
        lblChooseDateAnd.setText("Choose Date and Time Display:");
        
        final Date now = DateFormat.getNow();
        
        final Combo cbDate = new Combo(shlUserSettings, SWT.READ_ONLY);
        cbDate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.userDateFormat = cbDate.getSelectionIndex();
                result = true;
            }
        });
        String[] dates = new String[User.dateFormats.length];
        for (int i = 0; i < dates.length; i++) {
            dates[i] = DateFormat.dateToStr(now, User.dateFormats[i]);
        }
        cbDate.setItems(dates);
        cbDate.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        cbDate.setBounds(10, 285, 185, 28);
        cbDate.select(User.userDateFormat);
        
        final Combo cbGap = new Combo(shlUserSettings, SWT.READ_ONLY);
        cbGap.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.gapBetweenDateTime = cbGap.getSelectionIndex();
                result = true;
            }
        });
        cbGap.setItems(User.gapBetween);
        cbGap.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        cbGap.setBounds(205, 285, 45, 28);
        cbGap.select(User.gapBetweenDateTime);
        
        final Combo cbTime = new Combo(shlUserSettings, SWT.READ_ONLY);
        cbTime.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.userTimeFormat = cbTime.getSelectionIndex();
                result = true;
            }
        });
        String[] times = new String[User.timeFormats.length];
        for (int i = 0; i < times.length; i++) {
            times[i] = DateFormat.dateToStr(now, User.timeFormats[i]);
        }
        cbTime.setItems(times);
        cbTime.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        cbTime.setBounds(260, 285, 181, 28);
        cbTime.select(User.userTimeFormat);
    }
    
    private final String[] autoSaveOptions = {"2", "5 (Default)", "10", "15", "20"};
    private final int[]    autoSaveTime    = {120000, 300000, 600000, 900000, 1200000};

    private void autoSave() {
        Label lblAutoSaveTasks = new Label(shlUserSettings, SWT.NONE);
        lblAutoSaveTasks.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        lblAutoSaveTasks.setBounds(10, 85, 185, 20);
        lblAutoSaveTasks.setText("Auto Save TaskMeter Every");
        
        final Combo cbAutoSave = new Combo(shlUserSettings, SWT.READ_ONLY);
        cbAutoSave.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.autoSaveTime = autoSaveTime[cbAutoSave.getSelectionIndex()];
            }
        });
        cbAutoSave.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        cbAutoSave.setItems(autoSaveOptions);
        cbAutoSave.setBounds(201, 82, 110, 28);
        cbAutoSave.select(getAutoSaveSelection());
        
        Label lblMinutes = new Label(shlUserSettings, SWT.NONE);
        lblMinutes.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        lblMinutes.setBounds(325, 85, 60, 20);
        lblMinutes.setText("Minutes");
    }

    private void autoComplete() {
        final Button btnPerformAutoComplete = new Button(shlUserSettings, SWT.CHECK);
        btnPerformAutoComplete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.performAutoComplete = btnPerformAutoComplete.getSelection();
            }
        });
        btnPerformAutoComplete.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        btnPerformAutoComplete.setBounds(10, 126, 431, 20);
        btnPerformAutoComplete.setSelection(User.performAutoComplete);
        btnPerformAutoComplete.setText("Perform Auto Complete in SmartBar");
    }
    
    private int getAutoSaveSelection() {
        for (int i = 0; i < autoSaveTime.length; i++) {
            if (autoSaveTime[i] == User.autoSaveTime)
                return i;
        }
        return 0;
    }

    private void sortMethod() {
        final String[] SortMethods = { "Priority", "Start Date", "Deadline", "Duration", "Status" };
        
        Label lblSort = new Label(shlUserSettings, SWT.NONE);
        lblSort.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        lblSort.setBounds(10, 10, 205, 20);
        lblSort.setText("Default Tasks Sorting Method:");
        
        Label lblDefaultDeadlineStart = new Label(shlUserSettings, SWT.NONE);
        lblDefaultDeadlineStart.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
        lblDefaultDeadlineStart.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        lblDefaultDeadlineStart.setBounds(215, 11, 220, 20);
        lblDefaultDeadlineStart.setText("(Default: Deadline, Start Date, Priority)");

        Label lblFirst = new Label(shlUserSettings, SWT.NONE);
        lblFirst.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        lblFirst.setBounds(10, 44, 32, 20);
        lblFirst.setText("First");

        final Combo cbFirst = new Combo(shlUserSettings, SWT.READ_ONLY);
        cbFirst.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.sortingMethod[0] = cbFirst.getSelectionIndex() + 2;
            }
        });
        cbFirst.setItems(SortMethods);
        cbFirst.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        cbFirst.setBounds(48, 40, 96, 20);
        cbFirst.select(User.sortingMethod[0] - 2);

        Label lblSecond = new Label(shlUserSettings, SWT.NONE);
        lblSecond.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        lblSecond.setBounds(150, 44, 55, 20);
        lblSecond.setText("Second");

        final Combo cbSecond = new Combo(shlUserSettings, SWT.READ_ONLY);
        cbSecond.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.sortingMethod[1] = cbSecond.getSelectionIndex() + 2;
            }
        });
        cbSecond.setItems(SortMethods);
        cbSecond.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        cbSecond.setBounds(205, 40, 96, 20);
        cbSecond.select(User.sortingMethod[1] - 2);

        Label lblLast = new Label(shlUserSettings, SWT.NONE);
        lblLast.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        lblLast.setBounds(307, 44, 32, 20);
        lblLast.setText("Last");

        final Combo cbLast = new Combo(shlUserSettings, SWT.READ_ONLY);
        cbLast.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.sortingMethod[2] = cbLast.getSelectionIndex() + 2;
            }
        });
        cbLast.setItems(SortMethods);
        cbLast.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        cbLast.setBounds(345, 40, 96, 20);
        cbLast.select(User.sortingMethod[2] - 2);
    }
    
    private void center() {
        Rectangle parent = getParent().getBounds();
        Rectangle rect = shlUserSettings.getBounds();
        int x = parent.x + (parent.width - rect.width) / 2;
        int y = parent.y + (parent.height - rect.height) / 2;
        shlUserSettings.setLocation(x, y);
    }
}
