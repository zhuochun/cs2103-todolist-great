package cs2103.t14j1.taskmeter;

import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import cs2103.t14j1.logic.ControlGUI;
import cs2103.t14j1.logic.DateFormat;
import cs2103.t14j1.storage.FileHandler;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;
import cs2103.t14j1.storage.When;
import cs2103.t14j1.taskmeter.autocomplete.AutoComplete;

/**
 * TaskMeter Main Graphic User Interface
 * 
 * @author Zhuochun
 * 
 */
public class TaskMeter extends Shell {

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("taskmeter_res");
    private Label statusBar;        // status bar
    private Table taskTable;        // task table lists all the tasks
    private Table taskList;         // task list lists all the lists of tasks
    private Text  smartBar;         // smart bar

    private boolean   isModified;
    private int       mode;         // MODE_LIST and MODE_SEARCH for different events
    private int       filter;       // Filter tasks display according to different filters
    private int       lastSortColumn;
    
    private ControlGUI logic;        // the logic part center
    private TaskLists  lists;        // the lists of all lists and tasks
    private TaskList   currentList;  // stores the current list in display
    private TaskList   searchResult; // stores the search result in display
    
    private AutoComplete   autoComplete; // auto complete module for smartBar
    private QuickAddDialog quickAddView; // quick Add view, Ctrl + M to toggle between two views
    
    private String[] columnNames = {
            getResourceString("table.id"),
            getResourceString("table.name"),
            getResourceString("table.priority"),
            getResourceString("table.date"),
            getResourceString("table.deadline"),
            getResourceString("table.duration"),
            getResourceString("table.status")
    };
    private int[] columnWidths = { 25, 200, 70, 120, 70, 70, 75 };
    
    // 2 different modes
    private static final int MODE_LIST   = 0;
    private static final int MODE_SEARCH = 1;
    
    // 8 different filters
    private static final int FILTER_ALL          = 0;
    private static final int FILTER_IMPORTANT    = 1;
    private static final int FILTER_COMPLETED    = 2;
    private static final int FILTER_OVERDUE      = 3;
    private static final int FILTER_TODAY        = 4;
    private static final int FILTER_TOMORROW     = 5;
    private static final int FILTER_NEXT_DAYS    = 6;
    private static final int FILTER_WITHOUT_DATE = 7;
    
    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String args[]) {
        final Display display = Display.getDefault();

        try {
            TaskMeter application = new TaskMeter(display);
            application.open();
            application.layout();
            //Process p = Runtime.getRuntime().exec("TaskMeterHotKeys.exe");
            while (!application.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            //p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the shell and all respected components
     * 
     * @param display
     */
    public TaskMeter(Display display) {
        super(display, SWT.SHELL_TRIM | SWT.BORDER | SWT.APPLICATION_MODAL);
        
        // set grid layout
        GridLayout gridLayout = new GridLayout(11, false);
        gridLayout.horizontalSpacing = 3;
        setLayout(gridLayout);
        
        // global hotkey in whole application
        display.addFilter(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if ((e.stateMask & SWT.CTRL) == SWT.CTRL && e.keyCode == 'k') { // SmartBar focus : Ctrl + k
                    if (quickAddView.isActive()) {
                        quickAddView.focusQuickAdd();
                    } else {
                        smartBar.setFocus();
                        smartBar.setSelection(0, smartBar.getText().length());
                    }
                } else if ((e.stateMask & SWT.CTRL) == SWT.CTRL && e.keyCode == 't') { // TaskTable focus : Ctrl + t
                    taskTable.setFocus();
                    if (taskTable.getSelectionCount() == 0) {
                        taskTable.setSelection(0);
                    }
                } else if ((e.stateMask & SWT.CTRL) == SWT.CTRL && e.keyCode == 'i') { // TaskList focus : Ctrl + i
                    taskList.setFocus();
                    if (taskList.getSelectionCount() == 0) {
                        taskList.setSelection(0);
                    }
                } else if ((e.stateMask & SWT.CTRL) == SWT.CTRL && e.keyCode == 'm') { // Toggle Views : Ctrl + m
                    if (quickAddView.isActive()) {
                        quickAddView.close();
                    } else {
                        openQuickAddView();
                    }
                }
            }
        });

        // set application logo
        setImage(new Image(display, "taskMeter.png"));
        setMinimumSize(new Point(750, 500));

        // create each components
        createMenuBar();
        createSmartBar();
        createTaskList();
        createTaskTable();
        createBottomButtons();
        createStatusBar();
        createContents();
    }

    /**
     * Create contents of the shell.
     */
    protected void createContents() {
        addShellListener(new ShellAdapter() { // close taskMeter check
            public void shellClosed(ShellEvent e) {
                e.doit = closeTaskMeter();
            }
        });
        
        // set application title and size
        setText(getResourceString("app.title.full"));
        setSize(750, 500);

        // initial variables
        isModified = false;
        mode       = MODE_LIST;
        filter     = FILTER_ALL;
        
        // initial lists from files
        lists = new TaskLists();
        FileHandler.loadAll(lists);
        
        // display tasks and lists
        displayCurrentList(TaskLists.INBOX);
        displayLists();
        
        // initial modules
        logic        = new ControlGUI(lists);
        autoComplete = new AutoComplete(lists);
        smartBar.setFocus();
        
        // initial quick Add View
        quickAddView = new QuickAddDialog(this, logic, autoComplete);
    }

    /**
	 * create smartBar text input box
	 */
    private void createSmartBar() {
        smartBar = new Text(this, SWT.BORDER);
        smartBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 11, 1));
        smartBar.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.CR) {              // Enter to execute Command
                    executeCommand(smartBar.getText());
                    smartBar.setSelection(0, smartBar.getText().length());
                    smartBar.setFocus();
                } else if (e.keyCode == SWT.TAB) {      // Tab to complete words
                    e.doit = false;
                    String txt = smartBar.getText();
                    if (autoComplete.setInput(txt)) {
                        smartBar.setText(autoComplete.getCompletedStr());
                        smartBar.setSelection(autoComplete.getStartIdx(), autoComplete.getEndIdx());
                    }
                    smartBar.setFocus();
                } else if (e.keyCode == SWT.ESC
                        || e.keyCode == SWT.TRAVERSE_TAB_NEXT) { // ESC, S-Tab to confirm completion
                    if (smartBar.getSelectionCount() != 0) {
                        e.doit = false;
                        smartBar.setSelection(smartBar.getText().length());
                        smartBar.setFocus();
                    }
                }
            }
        });
        smartBar.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
    }
    
    /**
     * Creates the menu at the top of the shell where most of the programs
     * functionality is accessed.
     */
    private Menu createMenuBar() {
        Menu menuBar = new Menu(this, SWT.BAR);
        setMenuBar(menuBar);

        createUserMenu(menuBar);
        createEditMenu(menuBar);
        createWindowMenu(menuBar);
        createSettingMenu(menuBar);
        createHelpMenu(menuBar);

        return menuBar;
    }

    /**
     * @param menuBar
     */
    private void createUserMenu(Menu menuBar) {
        MenuItem mntmUser = new MenuItem(menuBar, SWT.CASCADE);
        mntmUser.setText(getResourceString("menu.user"));
    
        Menu menuUser = new Menu(mntmUser);
        mntmUser.setMenu(menuUser);
    
        MenuItem mntmAddList = new MenuItem(menuUser, SWT.NONE);
        mntmAddList.setAccelerator(SWT.MOD1 + 'L');
        mntmAddList.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addList();
            }
        });
        mntmAddList.setText(getResourceString("newList"));
    
        MenuItem mntmAddTask = new MenuItem(menuUser, SWT.NONE);
        mntmAddTask.setAccelerator(SWT.MOD1 + 'N');
        mntmAddTask.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addTask();
            }
        });
        mntmAddTask.setText(getResourceString("newTask"));
    
        new MenuItem(menuUser, SWT.SEPARATOR);
    
        final MenuItem mntmSave = new MenuItem(menuUser, SWT.NONE);
        mntmSave.setAccelerator(SWT.MOD1 + 'S');
        mntmSave.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isModified) {
                    saveTaskMeter();
                    setStatusBar(getResourceString("msg.saved"));
                }
            }
        });
        mntmSave.setText(getResourceString("save"));
    
        new MenuItem(menuUser, SWT.SEPARATOR);
    
        MenuItem mntmExit = new MenuItem(menuUser, SWT.NONE);
        mntmExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                System.exit(0);
            }
        });
        mntmExit.setText(getResourceString("exit"));
        
        menuUser.addMenuListener(new MenuAdapter() {
            public void menuShown(MenuEvent e) {
                mntmSave.setEnabled(isModified == true);
            }
        });
    }

    /**
     * @param menuBar
     */
    private void createEditMenu(Menu menuBar) {
        MenuItem mntmEdit = new MenuItem(menuBar, SWT.CASCADE);
        mntmEdit.setText(getResourceString("menu.edit"));
    
        Menu menuEdit = new Menu(mntmEdit);
        mntmEdit.setMenu(menuEdit);
    
        final MenuItem mntmUndo = new MenuItem(menuEdit, SWT.NONE);
        mntmUndo.setText(getResourceString("undo"));
    
        final MenuItem mntmRedo = new MenuItem(menuEdit, SWT.NONE);
        mntmRedo.setText(getResourceString("redo"));
    
        new MenuItem(menuEdit, SWT.SEPARATOR);
    
        final MenuItem mntmEditTask = new MenuItem(menuEdit, SWT.NONE);
        mntmEditTask.setAccelerator(SWT.MOD1 + 'E');
        mntmEditTask.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0) {
                    editTask(getSelectedIdx());
                }
            }
        });
        mntmEditTask.setText(getResourceString("edit"));
        
        final MenuItem mntmDeleteTask = new MenuItem(menuEdit, SWT.NONE);
        mntmDeleteTask.setAccelerator(SWT.DEL);
        mntmDeleteTask.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0) {
                    deleteTask(getSelectedIdx());
                }
            }
        });
        mntmDeleteTask.setText(getResourceString("delete"));
    
        final MenuItem mntmMarkCompleted = new MenuItem(menuEdit, SWT.NONE);
        mntmMarkCompleted.setAccelerator(SWT.MOD1 + 'D');
        mntmMarkCompleted.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0) {
                    toggleStatus(getSelectedIdx());
                }
            }
        });
        mntmMarkCompleted.setText(getResourceString("toggleStatus"));
    
        new MenuItem(menuEdit, SWT.SEPARATOR);
        
        final MenuItem mntmMarkPriority1 = new MenuItem(menuEdit, SWT.NONE);
        mntmMarkPriority1.setAccelerator(SWT.MOD1 + '1');
        mntmMarkPriority1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0)
                    togglePriority(getSelectedIdx(), Priority.IMPORTANT);
            }
        });
        mntmMarkPriority1.setText(getResourceString("togglePriority1"));
        
        final MenuItem mntmMarkPriority2 = new MenuItem(menuEdit, SWT.NONE);
        mntmMarkPriority2.setAccelerator(SWT.MOD1 + '2');
        mntmMarkPriority2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0)
                    togglePriority(getSelectedIdx(), Priority.NORMAL);
            }
        });
        mntmMarkPriority2.setText(getResourceString("togglePriority2"));
        
        final MenuItem mntmMarkPriority3 = new MenuItem(menuEdit, SWT.NONE);
        mntmMarkPriority3.setAccelerator(SWT.MOD1 + '3');
        mntmMarkPriority3.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0)
                    togglePriority(getSelectedIdx(), Priority.LOW);
            }
        });
        mntmMarkPriority3.setText(getResourceString("togglePriority3"));
        
        new MenuItem(menuEdit, SWT.SEPARATOR);
    
        final MenuItem mntmSearch = new MenuItem(menuEdit, SWT.NONE);
        mntmSearch.setAccelerator(SWT.MOD1 + 'F');
        mntmSearch.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doSearch();
            }
        });
        mntmSearch.setText(getResourceString("find"));
        
        menuEdit.addMenuListener(new MenuAdapter() {
            public void menuShown(MenuEvent e) {
                mntmEditTask.setEnabled(taskTable.getSelectionCount() != 0);
                mntmDeleteTask.setEnabled(taskTable.getSelectionCount() != 0);
                mntmMarkCompleted.setEnabled(taskTable.getSelectionCount() != 0);
                mntmMarkPriority1.setEnabled(taskTable.getSelectionCount() != 0);
                mntmMarkPriority2.setEnabled(taskTable.getSelectionCount() != 0);
                mntmMarkPriority3.setEnabled(taskTable.getSelectionCount() != 0);
            }
        });
    }

    /**
     * @param menuBar
     */
    private void createWindowMenu(Menu menuBar) {
        MenuItem mntmWindow = new MenuItem(menuBar, SWT.CASCADE);
        mntmWindow.setText(getResourceString("menu.window"));
    
        Menu menuWindow = new Menu(mntmWindow);
        mntmWindow.setMenu(menuWindow);
        
        final MenuItem mntmListView = new MenuItem(menuWindow, SWT.RADIO);
        mntmListView.setSelection(true);
        mntmListView.setText(getResourceString("window.list"));
    
        final MenuItem mntmQuickAddView = new MenuItem(menuWindow, SWT.RADIO);
        mntmQuickAddView.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                mntmQuickAddView.setSelection(false);
                mntmListView.setSelection(true);
                openQuickAddView();
            }
        });
        mntmQuickAddView.setText(getResourceString("window.quickAdd"));
    
        /*
        final MenuItem mntmCalendarView = new MenuItem(menuWindow, SWT.RADIO);
        mntmCalendarView.setText(getResourceString("calendar"));
        */
    }

    /**
     * @param menuBar
     */
    private void createSettingMenu(Menu menuBar) {
        MenuItem mntmSettings = new MenuItem(menuBar, SWT.CASCADE);
        mntmSettings.setText(getResourceString("menu.setting"));
    
        Menu menuSetting = new Menu(mntmSettings);
        mntmSettings.setMenu(menuSetting);
    
        MenuItem mntmUserSettings = new MenuItem(menuSetting, SWT.NONE);
        mntmUserSettings.setText(getResourceString("setting"));
    }

    /**
     * @param menuBar
     */
    private void createHelpMenu(Menu menuBar) {
        MenuItem mntmHelps = new MenuItem(menuBar, SWT.CASCADE);
        mntmHelps.setText(getResourceString("menu.help"));
    
        Menu menuHelp = new Menu(mntmHelps);
        mntmHelps.setMenu(menuHelp);
    
        /*
        MenuItem mntmStatistics = new MenuItem(menuHelp, SWT.NONE);
        mntmStatistics.setText(getResourceString("help.statistics"));
    
        new MenuItem(menuHelp, SWT.SEPARATOR);
        */
        
        MenuItem mntmTip = new MenuItem(menuHelp, SWT.NONE);
        mntmTip.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showTips();
            }
        });
        mntmTip.setText(getResourceString("help.tip"));
    
        MenuItem mntmHelp = new MenuItem(menuHelp, SWT.NONE);
        mntmHelp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showTips();
            }
        });
        mntmHelp.setAccelerator(SWT.F1);
        mntmHelp.setText(getResourceString("help.help"));
    
        new MenuItem(menuHelp, SWT.SEPARATOR);
    
        MenuItem mntmAbout = new MenuItem(menuHelp, SWT.NONE);
        mntmAbout.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                MessageBox box = new MessageBox(getShell(), SWT.NONE);
                box.setText(getResourceString("msg.about"));
                box.setMessage("Current Version: " + getResourceString("app.version"));
                box.open();
            }
        });
        mntmAbout.setText(getResourceString("help.about"));
    }

    /**
     * create the task list viewer that shows all the lists
     */
    private void createTaskList() {
        taskList = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.VIRTUAL);
        taskList.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) { // switch lists
                TableItem[] items = taskList.getSelection();
                String listname = items[0].getText();
                switchList(listname);
            }
        });
        GridData gd_taskList = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_taskList.verticalSpan = 4;
        gd_taskList.widthHint = 150;
        gd_taskList.horizontalSpan = 2;
        taskList.setLayoutData(gd_taskList);
        taskList.setHeaderVisible(true);
        taskList.setLinesVisible(true);

        TableColumn tblclmnLists = new TableColumn(taskList, SWT.CENTER);
        tblclmnLists.setWidth(300);
        tblclmnLists.setText(getResourceString("list"));
    }

    /**
     * create the table for displaying tasks
     */
    private void createTaskTable() {
        taskTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
        taskTable.setSelection(0);
        taskTable.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        GridData gd_taskTable = new GridData(SWT.FILL, SWT.FILL, true, true, 9, 4);
        gd_taskTable.heightHint = 289;
        taskTable.setLayoutData(gd_taskTable);
        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                editTask(getSelectedIdx());
            }
        });
        taskTable.setHeaderVisible(true);
        taskTable.setLinesVisible(true);
        
        for (int i = 0; i < columnNames.length; i++) {
            TableColumn column = new TableColumn(taskTable, SWT.CENTER);
            final int columnIndex = i;
            column.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    sortList(columnIndex);
                }
            });
            //column.setMoveable(true);
            column.setWidth(columnWidths[i]);
            column.setText(columnNames[i]);
        }
    }
    
    private void createBottomButtons() {
        Button btnTrashBox = new Button(this, SWT.NONE);
        btnTrashBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                switchList(TaskLists.TRASH);
            }
        });
        btnTrashBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        btnTrashBox.setText(getResourceString("button.trashbox"));

        Button btnAll = new Button(this, SWT.NONE);
        btnAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                filter = FILTER_ALL;
                displayTasks();
                setStatusBar(String.format(FILTER, getResourceString("filter.all")));
            }
        });
        btnAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnAll.setText(getResourceString("filter.all"));

        Button btnImportant = new Button(this, SWT.NONE);
        btnImportant.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                filter = FILTER_IMPORTANT;
                displayTasks();
                setStatusBar(String.format(FILTER, getResourceString("filter.important")));
            }
        });
        btnImportant.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnImportant.setText(getResourceString("filter.important"));

        Button btnCompleted = new Button(this, SWT.NONE);
        btnCompleted.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                filter = FILTER_COMPLETED;
                displayTasks();
                setStatusBar(String.format(FILTER, getResourceString("filter.completed")));
            }
        });
        btnCompleted.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnCompleted.setText(getResourceString("filter.completed"));

        Button btnOverdue = new Button(this, SWT.NONE);
        btnOverdue.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                filter = FILTER_OVERDUE;
                displayTasks();
                setStatusBar(String.format(FILTER, getResourceString("filter.overdue")));
            }
        });
        btnOverdue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnOverdue.setText(getResourceString("filter.overdue"));
        
        Label lblSps = new Label(this, SWT.NONE);
        lblSps.setText("          ");
        lblSps.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Button btnToday = new Button(this, SWT.NONE);
        btnToday.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                filter = FILTER_TODAY;
                displayTasks();
                setStatusBar(String.format(FILTER, getResourceString("filter.today")));
            }
        });
        btnToday.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnToday.setText(getResourceString("filter.today"));

        Button btnTomorrow = new Button(this, SWT.NONE);
        btnTomorrow.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                filter = FILTER_TOMORROW;
                displayTasks();
                setStatusBar(String.format(FILTER, getResourceString("filter.tomorrow")));
            }
        });
        btnTomorrow.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnTomorrow.setText(getResourceString("filter.tomorrow"));

        Button btnNextDays = new Button(this, SWT.NONE);
        btnNextDays.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                filter = FILTER_NEXT_DAYS;
                displayTasks();
                setStatusBar(String.format(FILTER, getResourceString("filter.custom")));
            }
        });
        btnNextDays.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnNextDays.setText(getResourceString("filter.custom"));

        Button btnWithoutDate = new Button(this, SWT.NONE);
        btnWithoutDate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                filter = FILTER_WITHOUT_DATE;
                displayTasks();
                setStatusBar(String.format(FILTER, getResourceString("filter.nodate")));
            }
        });
        btnWithoutDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnWithoutDate.setText(getResourceString("filter.nodate"));
        
        setTabList(new Control[]{smartBar, taskList, taskTable, btnAll, btnImportant, btnCompleted, btnOverdue, btnToday, btnTomorrow, btnNextDays, btnWithoutDate});
    }
    
    private void createStatusBar() {
        Label statusBarSeperator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        statusBarSeperator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 11, 1));
    
        statusBar = new Label(this, SWT.NONE);
        statusBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 11, 1));
        statusBar.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
    
        setStatusBar(getResourceString("msg.welcome"));
    }
    
    /**
     * execute input command from user
     * 
     * @param input         user's input string
     */
    private void executeCommand(String input) {
        logic.setUserInput(input.trim());
        
        try {
            switch (logic.getCommand()) {
                case ADD_TASK:
                    addTask(logic.addTask());
                    break;
                case DELETE_TASK:
                    deleteTask(logic.getTaskIdx());
                    break;
                case MOVE_TASK:
                    // TODO
                    break;
                case EDIT_TASK:
                    editTask(logic.getTaskIdx());
                    break;
                case MARK_COMPLETE:
                    toggleStatus(logic.getTaskIdx());
                    break;
                case MARK_PRIORITY:
                    togglePriority(logic.getTaskIdx(), logic.getNewTaskPriority());
                    break;
                case ADD_LIST:
                    addList(logic.getListName());
                    break;
                case EDIT_LIST:
                    editList(logic.extractNewListName(), null);
                    break;
                case RENAME_LIST:
                    editList(logic.extractNewListName(), logic.extractNewListName());
                    break;
                case DELETE_LIST:
                    deleteList(logic.getListName());
                    break;
                case SWITCH_LIST:
                    switchList(logic.getListName());
                    break;
                case SEARCH:
                    searchResult = logic.getSearchResult();
                    displaySearchResult();
                    break;
                default:
                    setStatusBar(getResourceString("msg.invalid.command"));
                    break;
            }
        } catch (Exception e) {
            setStatusBar(getResourceString("error.logic.command"));
        }
    }

    /**
     * refresh all lists and tasks in display
     */
    private void refreshDisplay() {
        displayLists();
        displayTasks();
    }

    /**
     * display all lists in taskList panel
     */
    private void displayLists() {
        taskList.removeAll();
        
        displayNewList(TaskLists.INBOX); // make sure INBOX is always the first one in list
    
        try {
            for (Entry<String, TaskList> list : lists) {
                if (list.getKey().equals(TaskLists.INBOX) || list.getKey().equals(TaskLists.TRASH)) {
                    continue;
                }

                displayNewList(list.getKey());
            }
        } catch (NullPointerException e) {
            // do nothing
        }
        
        if (mode == MODE_SEARCH) {
            displayNewList(searchResult.getName());
        }
    }

    /**
     * display a list in taskList table
     */
    private TableItem displayNewList(String name) {
        TableItem item = new TableItem(taskList, SWT.NONE);
        item.setText(name);
        highlightList(item);
        return item;
    }
    
    /**
     * display the task in the list, set currentList to the list
     */
    private void displayCurrentList(String name) {
        if (name != null && !name.trim().isEmpty()) { // null means currentList didn't change
            currentList = lists.getList(name);
        } else if (currentList == null) {
            currentList = lists.getList(TaskLists.INBOX);
        }
        
        mode = MODE_LIST;
        displayTasks(currentList);
    }

    private void displayTasks() {
        if (mode == MODE_LIST) {
            displayCurrentList(null);
        } else { // mode == MODE_SEARCH
            displayTasks(searchResult);
        }
    }

    /**
     * display all tasks in a TaskList
     */
    private void displayTasks(TaskList tlist) {
        taskTable.removeAll(); // remove all items for redraw
        
        try {
            int idx = 1;
            for (Task task : tlist) {
                filterTask(idx, task);
                idx++;
            }
        } catch (NullPointerException e) {
            // do nothing
        }
    }

    /**
     * display all tasks in a TaskList in opposite direction
     */
    private void displayTasksUp(TaskList tlist) {
        taskTable.removeAll(); // remove all items for redraw
        
        try {
            for (int i = tlist.getSize(); i > 0; i--) {
                filterTask(i, tlist.getTask(i));
            }
        } catch (NullPointerException e) {
            // do nothing
        }
    }
    
    /**
     * filter tasks according to different filters
     */
    private void filterTask(int idx, Task task) {
        if (filter == FILTER_ALL) {
            displayNewTask(idx, task);
        } else if (filter == FILTER_IMPORTANT && task.getPriority() == Priority.IMPORTANT) {
            displayNewTask(idx, task);
        } else if (filter == FILTER_COMPLETED && task.isCompleted()) {
            displayNewTask(idx, task);
        } else if (filter == FILTER_OVERDUE && !task.isCompleted()
                && task.compareDeadline(DateFormat.getNow()) < 0) {
            displayNewTask(idx, task);
        } else if (filter == FILTER_TODAY) {
            Calendar now = Calendar.getInstance();
            When.clear(now, When.CLEAR_BELOW_HOUR);
            
            Date todayStart = now.getTime();
            Date todayEnd   = DateFormat.getDateAfter(todayStart, 1);
            
            if (task.isWithinPeriod(todayStart, todayEnd)) {
                displayNewTask(idx, task);
            }
        } else if (filter == FILTER_TOMORROW) {
            Calendar now = Calendar.getInstance();
            When.clear(now, When.CLEAR_BELOW_HOUR);
            
            Date tmlStart = DateFormat.getDateAfter(now.getTime(), 1);
            Date tmlEnd   = DateFormat.getDateAfter(tmlStart, 1);
            
            if (task.isWithinPeriod(tmlStart, tmlEnd)) {
                displayNewTask(idx, task);
            }
        } else if (filter == FILTER_NEXT_DAYS) {
            Calendar now = Calendar.getInstance();
            When.clear(now, When.CLEAR_BELOW_HOUR);
            
            Date weekStart = now.getTime();
            Date weekEnd   = DateFormat.getDateAfter(weekStart, 7);
            
            if (task.isWithinPeriod(weekStart, weekEnd)) {
                displayNewTask(idx, task);
            }
        } else if (filter == FILTER_WITHOUT_DATE && task.getStartDateTime() == null) {
            displayNewTask(idx, task);
        }
    }

    /**
     * display all tasks in the search Result
     */
    private void displaySearchResult() {
        if (searchResult == null) {
            displayCurrentList(null);
        } else {
            mode = MODE_SEARCH;
            
            displayLists();
            displayTasks(searchResult);
            
            setStatusBar(String.format(SEARCH_RESULT, searchResult.getSize()));
        }
    }
    
    /**
     * display a task in TaskTable
     * 
     * @param idx           index of the task to be displayed
     * @param task          the task
     * @return the next index
     */
    private int displayNewTask(int idx, Task task) {
        TableItem tableItem = new TableItem(taskTable, SWT.NONE);
        tableItem.setText(new String[] {
                Integer.toString(idx),
                task.getName(),
                task.getPriority().toString().toLowerCase(),
                task.getStartEndDate() == null ? "" : task.getStartEndDate(),
                task.getDeadlineShort() == null ? "" : task.getDeadlineShort(),
                task.getDurationStr() == null ? "" : task.getDurationStr(),
                task.getStatusStr()
                });
        highlightTask(tableItem, task);
        return idx + 1;
    }

    /**
     * refresh a specific task contents
     * 
     * @param idx           the index of task in taskTable
     * @param task          the task
     * @return the index of the task
     */
    private int refreshTask(int idx, Task task) {
        TableItem item = taskTable.getItem(idx-1);
        item.setText(new String[] {
                Integer.toString(idx),
                task.getName(),
                task.getPriority().toString().toLowerCase(),
                task.getStartEndDate() == null ? "" : task.getStartEndDate(),
                task.getDeadlineShort() == null ? "" : task.getDeadlineShort(),
                task.getDurationStr() == null ? "" : task.getDurationStr(),
                task.getStatusStr()
                });
        highlightTask(item, task);
        return idx;
    }
    
    /**
     * sort the list according to different columns
     * 
     * @param columnIdx
     */
    private void sortList(int columnIdx) {
        // change table column display
        if (lastSortColumn != columnIdx) {
            taskTable.setSortColumn(taskTable.getColumn(columnIdx));
            taskTable.setSortDirection(SWT.DOWN);
            lastSortColumn = columnIdx;
        } else {
            taskTable.setSortDirection(SWT.UP);
            lastSortColumn = -1;
        }
        
        // perform sorting and refresh taskTable
        if (mode == MODE_LIST) {
            currentList.sort(columnIdx);
            
            if (lastSortColumn == columnIdx) {
                displayTasks(currentList);
            } else {
                displayTasksUp(currentList);
            }
        } else if (mode == MODE_SEARCH) {
            searchResult.sort(columnIdx);
            
            if (lastSortColumn == columnIdx) {
                displayTasks(searchResult);
            } else {
                displayTasksUp(searchResult);
            }
        }
    }

    /**
     * open addListDialog to get new list name
     */
    private void addList() {
        AddListDialog dialog = new AddListDialog(this);
        
        String newList = dialog.open();
        
        addList(newList);
    }
    
    /**
     * add the newList into lists and display result
     * 
     * @param newList
     */
    private void addList(String newList) {
        String feedback;
        
        try {
            if (lists.addList(newList)) {
                feedback = String.format(ADD_SUCCESS, LIST, newList);
                displayNewList(newList);
                isModified = true;
            } else {
                feedback = String.format(LIST_EXIST, newList);
            }
        } catch (NullPointerException e) {
            feedback = e.getMessage();
        }
        
        setStatusBar(feedback);
    }
    
    /**
     * edit list name to a new list name
     * 
     * @param oldName
     * @param newName
     */
    private void editList(String oldName, String newName) {
        String feedback = null;
        
        if (newName == null) {
            AddListDialog dialog = new AddListDialog(this);
            dialog.setListName(oldName);
            newName = dialog.open();
        }
        
        try {
            boolean success = logic.renameList(oldName, newName);
            
            if (success) {
                isModified = true;
                refreshDisplay();
                feedback = String.format(RENAME_LIST, oldName, newName);
            } else {
                feedback = String.format(RENAME_LIST_FAIL, oldName);
            }
        } catch (NullPointerException e) {
            feedback = e.getMessage();
        }
        
        setStatusBar(feedback);
    }
    
    private void deleteList(String name) {
        String feedback = null;
        
        try {
            boolean success = logic.deleteList(name);
            
            if (success) {
                isModified = true;
                refreshDisplay();
                feedback = String.format(DELETE_SUCCESS, LIST, name);
            } else {
                feedback = String.format(DELETE_FAIL, LIST);
            }
        } catch (Exception e) {
            feedback = e.getMessage();
        }
        
        setStatusBar(feedback);
    }

    private void switchList(String name) {
        if (name == null || name.trim().isEmpty()) {
            displayError(getResourceString("list.null"));
        } else if (mode == MODE_SEARCH && name.equals(searchResult.getName())) {
            return ;
        } else if (!lists.hasList(name)) {
            // if the list does not exists, ask whether to add it
            MessageBox box = new MessageBox(this, SWT.ICON_WARNING | SWT.YES | SWT.NO);
            box.setText(getResourceString("list.add"));
            box.setMessage(getResourceString("msg.new.list"));
    
            int choice = box.open();
            if (choice == SWT.YES) {
                addList(name);
            } else {
                setStatusBar(getResourceString("msg.switch.null.list"));
                return ;
            }
        }
        
        displayCurrentList(name);
        displayLists();

        filter = FILTER_ALL;

        setStatusBar(String.format(SWITCH_LIST, name));
    }

    /**
     * open addTaskDialog to add a task
     */
    private void addTask() {
        TaskDetailDialog dialog = new TaskDetailDialog(this, TaskDetailDialog.ADD_TASK);
        
        Task newTask = new Task();
        newTask.setList(currentList.getName());
        
        dialog.setTask(newTask);
        
        if (dialog.open() && currentList.addTask(newTask)) {
            addTask(newTask);
        }
    }
    
    /**
     * set statusBar for addTask result
     * 
     * @param newTask           new task just added
     */
    private void addTask(Task newTask) {
        String feedback;
        
        if (newTask != null) {
            feedback = String.format(ADD_TASK_SUCCESS, newTask.getName(), newTask.getList());
            isModified = true;

            if (mode == MODE_LIST && newTask.getList().equals(currentList.getName())) {
                displayNewTask(taskTable.getItemCount()+1, newTask);
            }
        } else {
            feedback = String.format(ADD_FAIL, TASK);
        }

        displayLists();
        
        switchTask(newTask.getList());
        
        setStatusBar(feedback);
    }
    
    private void switchTask(String listname) {
        if (!listname.equals(currentList.getName())) {
            switchList(listname);
        }
        
        taskTable.setSelection(taskTable.getItemCount()-1);
    }

    private void editTask(int index) {
        String feedback = null;
        
        TaskDetailDialog dialog = new TaskDetailDialog(this, TaskDetailDialog.EDIT_TASK);

        try {
            Task task = getIndexedTask(index);
            
            dialog.setTask(task);
            
            if (dialog.open()) {
                refreshTask(index, task);
                isModified = true;
                feedback = String.format(EDIT_SUCCESS, TASK, task.getName());
            } else {
                feedback = String.format(VIEW_TASK, task.getName());
            }
        } catch (IndexOutOfBoundsException e) {
            feedback = e.getMessage();
        }
        
        setStatusBar(feedback);
    }
    
    private void deleteTask(int index) {
        String feedback = null;
        
        try {
            Task delTask = getIndexedTask(index);
            
            boolean success = false;
            
            // move task to trash, if task is in trash, delete it
            if (delTask.getList().equals(TaskLists.TRASH)) {
                success = lists.removeTask(delTask.getList(), delTask);
            } else {
                success = lists.moveTask(TaskLists.TRASH, delTask);
            }
            
            if (success) {
                feedback = String.format(DELETE_SUCCESS, TASK, delTask.getName());
                isModified = true;
                displayTasks();
            } else {
                feedback = String.format(DELETE_FAIL, TASK);
            }
        } catch (IndexOutOfBoundsException e) {
            feedback = e.getMessage();
        } catch (Exception e) {
            feedback = e.getMessage();
        }
        
        setStatusBar(feedback);
    }
    
    private void toggleStatus(int index) {
        String feedback = null;
        
        try {
            Task task = getIndexedTask(index);
            
            if (task.isCompleted()) {
                task.setStatus(Task.INCOMPLETE);
                feedback = String.format(TOGGLE, task.getName(), task.getStatusStr());
            } else {
                task.setStatus(Task.COMPLETED);
                feedback = String.format(TOGGLE, task.getName(), task.getStatusStr());
            }
            
            isModified = true;
            refreshTask(index, task);
        } catch (IndexOutOfBoundsException e) {
            feedback = e.getMessage();
        }
        
        setStatusBar(feedback);
    }
    
    private void togglePriority(int index, Priority newPriority) {
        String feedback = null;
        
        try {
            Task task = getIndexedTask(index);
            
            task.setPriority(newPriority);
            feedback = String.format(TOGGLE, task.getName(), task.getPriorityStr());
            
            isModified = true;
            refreshTask(index, task);
        } catch (IndexOutOfBoundsException e) {
            feedback = e.getMessage();
        }
        
        setStatusBar(feedback);
    }

    /**
     * open searchDialog that get user's search
     */
    private void doSearch() {
        smartBar.setText("/type to search");
        smartBar.setSelection(1, smartBar.getText().length());
        smartBar.setFocus();
    }

    /**
     * open tipsDialog that show helps
     */
    public void showTips() {
        TipsDialog dialog = new TipsDialog(this);
        dialog.open();
    }

    private int getSelectedIdx() {
        TableItem[] items = taskTable.getSelection();
        
        try {
            int index = Integer.parseInt(items[0].getText());
            
            return index;
        } catch (Exception e) {
            return -1;
        }
    }
    
    private Task getIndexedTask(int index) {
        if (mode == MODE_LIST) {
            return currentList.getTask(index);
        } else { // mode == MODE_SEARCH
            return searchResult.getTask(index);
        }
    }
    
    /**
     * 
     * @return
     */
    private boolean saveTaskMeter() {
        FileHandler.saveAll(lists);
        isModified = false;
        return true;
    }

    /**
     * ask whether to save the changes before exit
     * 
     * @return
     */
    private boolean closeTaskMeter() {
        if (isModified) {
            // ask user if they want to save current address book
            MessageBox box = new MessageBox(this, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
            box.setText(this.getText());
            box.setMessage(getResourceString("msg.close"));
    
            int choice = box.open();
            if (choice == SWT.CANCEL) {
                return false;
            } else if (choice == SWT.YES) {
                if (!saveTaskMeter())
                    return false;
            }
        }
    
        return true;
    }

    private void displayError(String msg) {
        MessageBox box = new MessageBox(this, SWT.ICON_ERROR);
        box.setText("Error");
        box.setMessage(msg);
        box.open();
    }

    private void setStatusBar(String msg) {
        statusBar.setText(msg);
    }

    private void highlightList(TableItem l) {
        if (mode == MODE_LIST && currentList.getName().equals(l.getText())) {
            l.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
        } else if (mode == MODE_SEARCH && searchResult.getName().equals(l.getText())) {
            l.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
        } else {
            l.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        }
    }

    /**
     * highlight task which is completed, important, or missed deadline
     * 
     * @param item
     * @param task
     */
    private void highlightTask(TableItem item, Task task) {
        if (task.isCompleted()) {
            item.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
            item.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
        } else if (task.getDeadline() != null && task.getDeadline().before(DateFormat.getNow())) {
            item.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
            item.setBackground(SWTResourceManager.getColor(221, 160, 221));
        } else if (task.getPriority() == Priority.IMPORTANT) {
            item.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
            item.setBackground(SWTResourceManager.getColor(255, 218, 185));
        } else {
            item.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
            item.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        }
    }
    
    /**
     * change to and open quick add view
     */
    private void openQuickAddView() {
        this.setVisible(false);
        
        String text = quickAddView.open();
        
        this.setVisible(true);
        this.setActive();
        smartBar.setText(text);
        
        if (quickAddView.isModified()) {
            isModified = true;
            refreshDisplay();
        }
    }

    /**
     * Returns a string from the resource bundle. We don't want to crash because
     * of a missing String. Returns the key if not found.
     */
    public static String getResourceString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        } catch (NullPointerException e) {
            return "!" + key + "!";
        }
    }
    
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
    
    /* messages */
    private static final String LIST            = "List";
    private static final String TASK            = "Task";
    private static final String TOGGLE          = "Task \"%1$s\" is marked as %2$s";
    private static final String LIST_EXIST      = "List \"%1$s\" already exists";
    private static final String ADD_SUCCESS     = "%1$s \"%2$s\" is successfully added";
    private static final String ADD_TASK_SUCCESS= "Task \"%1$s\" is successfully added to List \"%2$s\"";
    private static final String ADD_FAIL        = "New %1$s fail to be added";
    private static final String EDIT_SUCCESS    = "%1$s \"%2$s\" is successfully edited";
    private static final String DELETE_SUCCESS  = "%1$s \"%2$s\" is successfully deleted";
    private static final String DELETE_FAIL     = "%1$s fail to delete";
    private static final String SEARCH_RESULT   = "%1$d task(s) have been found";
    private static final String SWITCH_LIST     = "Current list : %1$s";
    private static final String VIEW_TASK       = "View task : %1$s";
    private static final String RENAME_LIST     = "List \"%1$s\" is renamed to \"%2$s\"";
    private static final String RENAME_LIST_FAIL= "Rename List \"%1$s\" is failed";
    private static final String FILTER          = "Filter tasks : %1$s";
}
