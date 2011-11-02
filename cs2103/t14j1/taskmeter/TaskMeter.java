package cs2103.t14j1.taskmeter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.swt.graphics.ImageData;
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
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.wb.swt.SWTResourceManager;

import cs2103.t14j1.logic.ControlGUI;
import cs2103.t14j1.logic.DateFormat;
import cs2103.t14j1.logic.filter.Filter;
import cs2103.t14j1.logic.filter.FilterTask;
import cs2103.t14j1.storage.FileHandler;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;
import cs2103.t14j1.storage.logging.Log;
import cs2103.t14j1.storage.user.User;
import cs2103.t14j1.taskmeter.autocomplete.AutoComplete;
import cs2103.t14j1.taskmeter.quickadd.QuickAddDialog;
import cs2103.t14j1.taskmeter.reminder.ReminderDialog;

/**
 * TaskMeter Main Graphic User Interface
 * 
 * @author Zhuochun
 * 
 */
public class TaskMeter extends Shell {
    private static final Logger LOGGER = Logger.getLogger(TaskMeter.class.getName());
    
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("taskmeter_res");
    private Label statusBar;        // status bar
    private Table taskTable;        // task table lists all the tasks
    private Table taskList;         // task list lists all the lists of tasks
    private Text  smartBar;         // smart bar
    private Image logo;             // logo image
    private Tray  tray;             // system tray
    private TrayItem trayItem;      // tray item
    private ToolTip  remindTray;    // tray reminder
    private Process  globalHotKey;  // global hotkey (F6 in Windows operating System) process

    private boolean   isModified;
    private int       mode;         // MODE_LIST and MODE_SEARCH for different events
    private int       lastSortColumn;
    
    private ControlGUI logic;        // the logic part center
    private TaskLists  lists;        // the lists of all lists and tasks
    private TaskList   currentList;  // stores the current list in display
    private TaskList   searchResult; // stores the search result in display
    
    private ReminderDialog reminder;     // reminder module
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
    private int[] columnWidths = { 25, 195, 75, 220, 115, 75, 85 };
    
    // 2 different modes
    private static final int MODE_LIST   = 0;
    private static final int MODE_SEARCH = 1;
    
    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String args[]) {
        try {
            Log.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        User.initial();
        
        final Display display = Display.getDefault();
        try {
            TaskMeter application = new TaskMeter(display);
            application.open();
            application.layout();
            while (!application.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the shell and all respected components
     * 
     * @param display
     */
    public TaskMeter(final Display display) {
        super(display, SWT.SHELL_TRIM | SWT.BORDER | SWT.APPLICATION_MODAL);
        
        // set grid layout
        GridLayout gridLayout = new GridLayout(11, false);
        gridLayout.horizontalSpacing = 3;
        setLayout(gridLayout);
        
        // hotkey in whole application
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
        
        // setup up auto save
        Runnable autoSave = new Runnable() {
            public void run() {
                saveTaskMeter();
                display.timerExec(User.autoSaveTime, this);
            }
        };
        display.timerExec(User.autoSaveTime, autoSave);

        // set application logo
        setLogoImage(display);
        setImage(logo);
        // set application tray
        setTray(display);
        // set minimum size
        setText(getResourceString("app.title.full"));
        setMinimumSize(new Point(700, 500));
        setSize(995, 600);
        
        // load global hotkey process
        try {
            globalHotKey = Runtime.getRuntime().exec("resource/TaskMeterHotKey.exe");
            assert(globalHotKey != null);
        } catch (IOException e) {
            displayError(getResourceString("error.global.hotkey"));
            LOGGER.warning("Global Hotkey Not Loaded");
        }

        // create each components
        createMenuBar();
        createSmartBar();
        createTaskList();
        createTaskTable();
        createBottomButtons();
        createStatusBar();
        createContents();
    }
    
    private void setLogoImage(Display display) {
        try {
            InputStream stream = TaskMeter.class.getResourceAsStream("taskMeter.gif");
            ImageData source = new ImageData(stream);
            ImageData mask = source.getTransparencyMask();
            logo = new Image(display, source, mask);
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "setLogoImage", e);
        }
    }
    
    private void openTrayReminder(String title, String msg) {
        if (remindTray == null) { // in case tray is not supported
            return ;
        }
        
        remindTray.setText(title);
        remindTray.setMessage(msg);
        remindTray.setVisible(true);
        remindTray.setAutoHide(true);
        remindTray.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                openTaskMeter();
            }
        });
    }
    
    private void setTray(Display display) {
        tray = display.getSystemTray();
        
        if (tray != null) {
            remindTray = new ToolTip(this, SWT.BALLOON | SWT.ICON_INFORMATION);
            remindTray.setVisible(false);
            
            trayItem = new TrayItem(tray, SWT.NONE);
            trayItem.setImage(logo);
            trayItem.setToolTip(remindTray);
            trayItem.setToolTipText(getResourceString("app.title"));
            
            trayItem.addListener(SWT.Selection, new Listener() {
              public void handleEvent(Event event) {
                  openTaskMeter();
              }
            });
            
            final Menu menu = new Menu(this, SWT.POP_UP);
            MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
            menuItem.setText("Open (Press F6)");
            menuItem.setEnabled(false);
            
            menuItem = new MenuItem(menu, SWT.PUSH);
            menuItem.setText("Save");
            menuItem.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    saveTaskMeter();
                    openTrayReminder(getResourceString("msg.save.title"), getResourceString("msg.saved"));
                }
            });

            menuItem = new MenuItem(menu, SWT.PUSH);
            menuItem.setText("Exit");
            menuItem.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    if (closeTaskMeter()) {
                        System.exit(0);
                    }
                }
            });
            
            trayItem.addListener(SWT.MenuDetect, new Listener() {
                public void handleEvent(Event event) {
                    menu.setVisible(true);
                }
            });
        } else {
            LOGGER.warning("Minimize to Tray is Not Supported");
        }
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
        
        // initial lists from files
        lists = new TaskLists();
        TaskList reminderList = new TaskList("Reminder List");
        FileHandler.loadAll(lists, reminderList);
        
        // initial variables
        isModified = false;
        mode       = MODE_LIST;
        
        // initial modules
        FilterTask.setFilter(Filter.FILTER_ALL);
        logic        = new ControlGUI(lists);
        autoComplete = new AutoComplete(lists);
        setEventListener();
        smartBar.setFocus();
        
        // initial quick Add View
        quickAddView = new QuickAddDialog(this, logic, autoComplete);
        // initial reminder
        reminder = new ReminderDialog(this);
        reminder.addRefreshListener(new RefreshListener() {
            public void refresh() {
                displayTasks();
            }
            public void trayRemind(String title, String msg) {
                openTrayReminder(title, msg);
            }
        });
        
        // load reminders into application
        for (Task t : reminderList) {
            try {
                Date date = t.getReminder();
                t.setReminder(null);
                reminder.addReminder(date, t);
            } catch (IllegalArgumentException e) {
                // reminder already passed, so will be ignored
            }
        }
        
        // display tasks and lists
        displayCurrentList(TaskLists.INBOX);
        displayLists();
    }
    
    private void setEventListener() {
        logic.setEventListener(new EventListener() {
            public String getMsg(String m) {
                return getResourceString(m);
            }

            public TaskLists getLists() {
                return lists;
            }

            public Task getTask(int index) {
                return getIndexedTask(index);
            }

            public ReminderDialog getReminder() {
                return reminder;
            }

            public String getEditList(String oldName) {
                return editList(oldName);
            }

            public void setStatus(String msg) {
                setStatusBar(msg);
            }

            public void setModified() {
                isModified = true;
            }

            public void displayList(String listname) {
                displayNewList(listname);
            }

            public void refreshAll() {
                refreshDisplay();
            }

            public void refreshLists() {
                displayLists();
            }

            public void refreshTasks() {
                displayTasks();
            }

            public void setSearch(TaskList result) {
                searchResult = result;
                displaySearchResult();
            }

            public void switchToTask(String list) {
                switchTask(list);
            }

            public void switchToList(String list) {
                switchList(list);
            }

            public void editIdxTask(int idx) {
                editTask(idx);
            }
        });
    }

    /**
	 * create smartBar text input box
	 */
    private void createSmartBar() {
        smartBar = new Text(this, SWT.BORDER);
        smartBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Space to confirm completion
                if (e.character == ' ' && smartBar.getSelectionCount() != 0) {
                    smartBar.setSelection(smartBar.getText().length());
                    smartBar.setFocus();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                // Only perform auto completion when user input characters
                if (User.performAutoComplete && (e.character > 'A' && e.character < 'z')) {
                    String txt = smartBar.getText();
                    if (autoComplete.setInput(txt, smartBar.getSelectionCount() != 0)) {
                        smartBar.setText(autoComplete.getCompletedStr());
                        smartBar.setSelection(autoComplete.getStartIdx(), autoComplete.getEndIdx());
                    }
                    smartBar.setFocus();
                } else if (e.character == SWT.BS && smartBar.getText().isEmpty()) {
                    autoComplete.reset();
                }
            }
        });
        smartBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 11, 1));
        smartBar.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.CR) {              // Enter to execute Command
                    executeCommand(smartBar.getText());
                    autoComplete.reset();
                    smartBar.setSelection(0, smartBar.getText().length());
                    smartBar.setFocus();
                } else if (e.keyCode == SWT.TAB) {      // Tab to complete words
                    e.doit = false;
                    String txt = smartBar.getText();
                    if (autoComplete.setInput(txt, smartBar.getSelectionCount() != 0)) {
                        smartBar.setText(autoComplete.getCompletedStr());
                        smartBar.setSelection(autoComplete.getStartIdx(), autoComplete.getEndIdx());
                    }
                    smartBar.setFocus();
                } else if (e.keyCode == SWT.ESC) { // ESC to confirm completion
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
    
    private void executeCommand(String input) {
        logic.setUserInput(input.trim());
        logic.executeCommand();
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
        
        final MenuItem mntmClearCompleted = new MenuItem(menuUser, SWT.NONE);
        mntmClearCompleted.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                logic.clearCompleted(currentList);
                displayTasks();
            }
        });
        mntmClearCompleted.setText(getResourceString("clearCompleted"));
    
        final MenuItem mntmClearTrash = new MenuItem(menuUser, SWT.NONE);
        mntmClearTrash.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                logic.clearTrash();
                displayTasks();
            }
        });
        mntmClearTrash.setText(getResourceString("clearTrash"));
    
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
                if (closeTaskMeter()) {
                    System.exit(0);
                }
            }
        });
        mntmExit.setText(getResourceString("exit"));
        
        menuUser.addMenuListener(new MenuAdapter() {
            public void menuShown(MenuEvent e) {
                mntmClearCompleted.setEnabled(mode == MODE_LIST && !currentList.getName().equals(TaskLists.TRASH));
                mntmClearTrash.setEnabled(!lists.getList(TaskLists.TRASH).isEmpty());
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
        mntmUndo.setAccelerator(SWT.MOD1 + 'Z');
        mntmUndo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (logic.hasUndo()) {
                    logic.undo();
                }
            }
        });
        mntmUndo.setText(getResourceString("undo"));
    
        final MenuItem mntmRedo = new MenuItem(menuEdit, SWT.NONE);
        mntmRedo.setAccelerator(SWT.MOD1 + 'Y');
        mntmRedo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (logic.hasRedo()) {
                    logic.redo();
                }
            }
        });
        mntmRedo.setText(getResourceString("redo"));
        
        new MenuItem(menuEdit, SWT.SEPARATOR);
        
        final MenuItem mntmRemind = new MenuItem(menuEdit, SWT.NONE);
        mntmRemind.setAccelerator(SWT.MOD1 + 'R');
        mntmRemind.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isTasksFocus()) {
                    int index = getSelectedIdx();
                    if (getIndexedTask(index).getReminder() == null)
                        logic.addReminder(getSelectedIdx(), User.defaultRemind);
                    else
                        logic.removeReminder(index);
                }
            }
        });
        mntmRemind.setText(getResourceString("remind"));
    
        new MenuItem(menuEdit, SWT.SEPARATOR);
    
        final MenuItem mntmEditTask = new MenuItem(menuEdit, SWT.NONE);
        mntmEditTask.setAccelerator(SWT.MOD1 + 'E');
        mntmEditTask.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isTasksFocus()) {
                    logic.editTask(getSelectedIdx());
                } else if (isListsFocus()) {
                    logic.editList(getSelectedListName(), null);
                }
            }
        });
        mntmEditTask.setText(getResourceString("edit"));
        
        final MenuItem mntmDeleteTask = new MenuItem(menuEdit, SWT.NONE);
        mntmDeleteTask.setAccelerator(SWT.DEL);
        mntmDeleteTask.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isTasksFocus()) {
                    logic.deleteTask(getSelectedIdx());
                } else if (isListsFocus()) {
                    logic.deleteList(getSelectedListName());
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
                    logic.toggleStatus(getSelectedIdx(), null);
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
                    logic.togglePriority(getSelectedIdx(), Priority.IMPORTANT);
            }
        });
        mntmMarkPriority1.setText(getResourceString("togglePriority1"));
        
        final MenuItem mntmMarkPriority2 = new MenuItem(menuEdit, SWT.NONE);
        mntmMarkPriority2.setAccelerator(SWT.MOD1 + '2');
        mntmMarkPriority2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0)
                    logic.togglePriority(getSelectedIdx(), Priority.NORMAL);
            }
        });
        mntmMarkPriority2.setText(getResourceString("togglePriority2"));
        
        final MenuItem mntmMarkPriority3 = new MenuItem(menuEdit, SWT.NONE);
        mntmMarkPriority3.setAccelerator(SWT.MOD1 + '3');
        mntmMarkPriority3.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0)
                    logic.togglePriority(getSelectedIdx(), Priority.LOW);
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
                mntmUndo.setEnabled(logic.hasUndo());
                mntmRedo.setEnabled(logic.hasRedo());
                mntmRemind.setEnabled(isTasksFocus());
                mntmEditTask.setEnabled(isTasksFocus() || isListsFocus());
                mntmDeleteTask.setEnabled(isTasksFocus() || isListsFocus());
                mntmMarkCompleted.setEnabled(isTasksFocus());
                mntmMarkPriority1.setEnabled(isTasksFocus());
                mntmMarkPriority2.setEnabled(isTasksFocus());
                mntmMarkPriority3.setEnabled(isTasksFocus());
            }
        });
    }
    
    private boolean isTasksFocus() {
        return taskTable.getSelectionCount() != 0 && taskTable.isFocusControl();
    }
    
    private boolean isListsFocus() {
        return taskList.getSelectionCount() != 0 && taskList.isFocusControl();
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
    
        final MenuItem mntmUserSettings = new MenuItem(menuSetting, SWT.NONE);
        mntmUserSettings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openUserSetting();
            }
        });
        mntmUserSettings.setText(getResourceString("setting"));
        
        final MenuItem mntmAutoComplete = new MenuItem(menuSetting, SWT.CHECK);
        mntmAutoComplete.setAccelerator(SWT.MOD1 + '0');
        mntmAutoComplete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                User.performAutoComplete = User.performAutoComplete ? false : true;
            }
        });
        mntmAutoComplete.setSelection(User.performAutoComplete);
        mntmAutoComplete.setText(getResourceString("autocomplete"));
        
        menuSetting.addMenuListener(new MenuAdapter() {
            public void menuShown(MenuEvent e) {
                mntmAutoComplete.setSelection(User.performAutoComplete);
            }
        });
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
                switchList(getSelectedListName());
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
                logic.editTask(getSelectedIdx());
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
                FilterTask.setFilter(Filter.FILTER_ALL);
                displayTasks();
                setStatusBar(String.format(getResourceString("msg.FILTER"), getResourceString("filter.all")));
            }
        });
        btnAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnAll.setText(getResourceString("filter.all"));

        Button btnImportant = new Button(this, SWT.NONE);
        btnImportant.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FilterTask.setFilter(Filter.FILTER_IMPORTANT);
                displayTasks();
                setStatusBar(String.format(getResourceString("msg.FILTER"), getResourceString("filter.important")));
            }
        });
        btnImportant.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnImportant.setText(getResourceString("filter.important"));

        Button btnCompleted = new Button(this, SWT.NONE);
        btnCompleted.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FilterTask.setFilter(Filter.FILTER_COMPLETED);
                displayTasks();
                setStatusBar(String.format(getResourceString("msg.FILTER"), getResourceString("filter.completed")));
            }
        });
        btnCompleted.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnCompleted.setText(getResourceString("filter.completed"));

        Button btnOverdue = new Button(this, SWT.NONE);
        btnOverdue.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FilterTask.setFilter(Filter.FILTER_OVERDUE);
                displayTasks();
                setStatusBar(String.format(getResourceString("msg.FILTER"), getResourceString("filter.overdue")));
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
                FilterTask.setFilter(Filter.FILTER_TODAY);
                displayTasks();
                setStatusBar(String.format(getResourceString("msg.FILTER"), getResourceString("filter.today")));
            }
        });
        btnToday.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnToday.setText(getResourceString("filter.today"));

        Button btnTomorrow = new Button(this, SWT.NONE);
        btnTomorrow.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FilterTask.setFilter(Filter.FILTER_TOMORROW);
                displayTasks();
                setStatusBar(String.format(getResourceString("msg.FILTER"), getResourceString("filter.tomorrow")));
            }
        });
        btnTomorrow.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnTomorrow.setText(getResourceString("filter.tomorrow"));

        Button btnNextDays = new Button(this, SWT.NONE);
        btnNextDays.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FilterTask.setFilter(Filter.FILTER_NEXT_DAYS);
                displayTasks();
                setStatusBar(String.format(getResourceString("msg.FILTER"), getResourceString("filter.custom")));
            }
        });
        btnNextDays.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnNextDays.setText(getResourceString("filter.custom"));

        Button btnWithoutDate = new Button(this, SWT.NONE);
        btnWithoutDate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FilterTask.setFilter(Filter.FILTER_WITHOUT_DATE);
                displayTasks();
                setStatusBar(String.format(getResourceString("msg.FILTER"), getResourceString("filter.nodate")));
            }
        });
        btnWithoutDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnWithoutDate.setText(getResourceString("filter.nodate"));
        
        setTabList(new Control[]{smartBar, taskList, taskTable, btnAll, btnImportant, btnCompleted,
                btnOverdue, btnToday, btnTomorrow, btnNextDays, btnWithoutDate});
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
        } else if (!lists.hasList(currentList.getName())) {
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
                if (FilterTask.filter(task)) {
                    displayNewTask(idx, task);
                }
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
                if (FilterTask.filter(tlist.getTask(i))) {
                    displayNewTask(i, tlist.getTask(i));
                }
            }
        } catch (NullPointerException e) {
            // do nothing
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
            
            setStatusBar(String.format(getResourceString("msg.SEARCH_RESULT"), searchResult.getSize()));
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
                task.getStartEndDateStr() == null ? "" : task.getStartEndDateStr(),
                task.getDeadlineStr() == null ? "" : task.getDeadlineStr(),
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
                task.getStartEndDateStr() == null ? "" : task.getStartEndDateStr(),
                task.getDeadlineStr() == null ? "" : task.getDeadlineStr(),
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
        
        logic.addList(newList);
    }
    
    /**
     * open addListDialog to get new list name
     */
    private String editList(String oldName) {
        AddListDialog dialog = new AddListDialog(this);
        
        dialog.setListName(oldName);
        
        return dialog.open();
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
                logic.addList(name);
            } else {
                setStatusBar(getResourceString("msg.switch.null.list"));
                return ;
            }
        }
        
        displayCurrentList(name);
        displayLists();

        FilterTask.setFilter(Filter.FILTER_ALL);

        setStatusBar(String.format(getResourceString("msg.SWITCH_LIST"), name));
    }

    /**
     * open addTaskDialog to add a task
     */
    private void addTask() {
        TaskDetailDialog dialog = new TaskDetailDialog(this, TaskDetailDialog.ADD_TASK);
        
        Task newTask = new Task();
        newTask.setList(currentList.getName());
        
        dialog.setTask(newTask);
        
        if (dialog.open()) {
            logic.addTask(newTask);
        }
    }
    
    private void switchTask(String listname) {
        switchList(listname);
        taskTable.setSelection(taskTable.getItemCount()-1);
    }

    private void editTask(int index) {
        String feedback = null;
        
        TaskDetailDialog dialog = new TaskDetailDialog(this, TaskDetailDialog.EDIT_TASK);

        try {
            Task task = getIndexedTask(index);
            
            dialog.setTask(task);
            
            if (dialog.open()) {
                isModified = true;
                refreshTask(index, task);
                feedback = String.format(getResourceString("msg.EDIT_SUCCESS"), "Task", task.getName());
            } else {
                feedback = String.format(getResourceString("msg.VIEW_TASK"), task.getName());
            }
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
    
    private String getSelectedListName() {
        TableItem[] items = taskList.getSelection();
        return items[0].getText();
    }
    
    private void openTaskMeter() {
        this.setMinimized(false);
        this.forceActive();
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

    private void openUserSetting() {
        UserSettingDialog dialog = new UserSettingDialog(this);
        boolean refresh = dialog.open();
        
        if (refresh) {
            refreshDisplay();
        }
    }

    private boolean saveTaskMeter() {
        if (isModified) {
            FileHandler.saveAll(lists);
            isModified = false;
        }
        
        User.save();
        return true;
    }

    /**
     * ask whether to save the changes before exit
     * 
     * @return
     */
    private boolean closeTaskMeter() {
        if (isModified) {
            // ask user if they want to save taskMeter changes
            MessageBox box = new MessageBox(this, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
            box.setText(this.getText());
            box.setMessage(getResourceString("msg.close"));
    
            int choice = box.open();
            if (choice == SWT.CANCEL) {
                return false;
            } else if (choice == SWT.YES) {
                if (!saveTaskMeter()) {
                    return false;
                }
            }
        }
        
        // Dispose Components
        trayItem.dispose();
        tray.dispose();

        if (globalHotKey != null) {
            globalHotKey.destroy();
        }
    
        return true;
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
        
        if (task.getReminder() != null) {
            item.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
        }
    }
    
    private void displayError(String msg) {
        MessageBox box = new MessageBox(this, SWT.ICON_ERROR);
        box.setText("Error");
        box.setMessage(msg);
        box.open();
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
}