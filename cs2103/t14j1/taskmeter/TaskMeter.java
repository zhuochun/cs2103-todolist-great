package cs2103.t14j1.taskmeter;

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

import cs2103.t14j1.logic.DateFormat;
import cs2103.t14j1.storage.FileHandler;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;
import cs2103.t14j1.taskmeter.autocomplete.AutoComplete;

/**
 * TaskMeter Main Graphic User Interface
 * 
 * @author Zhuochun
 * 
 */
public class TaskMeter extends Shell {

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("taskmeter_res");
    private Label statusBar;
    private Table taskTable;
    private Table taskList;
    private Text  smartBar;

    private boolean   isModified;
    private int       mode;
    private int       lastSortColumn;
    
    private TaskLists lists;
    private TaskList  currentList;
    private TaskList  searchResult;
    private AutoComplete autoComplete;
    
    
    private QuickAddDialog quickAddView; 
    
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
    
    private static final int MODE_LIST   = 0;
    private static final int MODE_SEARCH = 1;
    
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
            Process p = Runtime.getRuntime().exec("TaskMeterHotKeys.exe");
            while (!application.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            p.destroy();
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
        
        // global hotkey to focus on smartBar
        display.addFilter(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if ((e.stateMask & SWT.CTRL) == SWT.CTRL && e.keyCode == 'k') { // SmartBar focus
                    if (quickAddView.isActive()) {
                        quickAddView.focusQuickAdd();
                    } else {
                        smartBar.setFocus();
                        smartBar.setSelection(0, smartBar.getText().length());
                    }
                } else if ((e.stateMask & SWT.CTRL) == SWT.CTRL && e.keyCode == 't') { // TaskTable focus
                    taskTable.setFocus();
                    if (taskTable.getSelectionCount() == 0) {
                        taskTable.setSelection(0);
                    }
                } else if ((e.stateMask & SWT.CTRL) == SWT.CTRL && e.keyCode == 'i') { // TaskList focus
                    taskList.setFocus();
                    if (taskList.getSelectionCount() == 0) {
                        taskList.setSelection(0);
                    }
                } else if ((e.stateMask & SWT.CTRL) == SWT.CTRL && e.keyCode == 'm') { // TaskTable focus
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
        
        // initial quick Add View
        quickAddView = new QuickAddDialog(this, autoComplete);
    }

    /**
     * Create contents of the shell.
     */
    protected void createContents() {
        addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e) {
                e.doit = closeTaskMeter();
            }
        });
        
        setText(getResourceString("app.title.full"));
        setSize(750, 500);

        isModified = false;
        mode       = MODE_LIST;

        lists = new TaskLists();
        FileHandler.loadAll(lists);
        
        displayCurrentList(TaskLists.INBOX);
        displayLists();
        
        autoComplete = new AutoComplete(lists);
        smartBar.setFocus();
    }

    /**
	 * create smartBar text input box
	 */
    private void createSmartBar() {
        smartBar = new Text(this, SWT.BORDER);
        smartBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 11, 1));
        smartBar.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.CR) { // Enter to execute Command
                    setStatusBar(smartBar.getText());
                    smartBar.setSelection(0, smartBar.getText().length());
                    smartBar.setFocus();
                } else if (e.keyCode == SWT.TAB) { // Tab to complete words
                    e.doit = false;
                    String txt = smartBar.getText();
                    if (autoComplete.setInput(txt)) {
                        smartBar.setText(autoComplete.getCompletedStr());
                        smartBar.setSelection(autoComplete.getStartIdx(), autoComplete.getEndIdx());
                    }
                    smartBar.setFocus();
                } else if (e.keyCode == SWT.ESC) { // ESC to confirm complete selection
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
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0)
                    editTask();
            }
        });
        mntmEditTask.setText(getResourceString("edit"));
        
        final MenuItem mntmDeleteTask = new MenuItem(menuEdit, SWT.NONE);
        mntmDeleteTask.setAccelerator(SWT.DEL);
        mntmDeleteTask.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0)
                    deleteTask();
            }
        });
        mntmDeleteTask.setText(getResourceString("delete"));
    
        final MenuItem mntmMarkCompleted = new MenuItem(menuEdit, SWT.NONE);
        mntmMarkCompleted.setAccelerator(SWT.MOD1 + 'D');
        mntmMarkCompleted.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0)
                    toggleTaskStatus();
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
                    togglePriority(Priority.IMPORTANT);
            }
        });
        mntmMarkPriority1.setText(getResourceString("togglePriority1"));
        
        final MenuItem mntmMarkPriority2 = new MenuItem(menuEdit, SWT.NONE);
        mntmMarkPriority2.setAccelerator(SWT.MOD1 + '2');
        mntmMarkPriority2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0)
                    togglePriority(Priority.NORMAL);
            }
        });
        mntmMarkPriority2.setText(getResourceString("togglePriority2"));
        
        final MenuItem mntmMarkPriority3 = new MenuItem(menuEdit, SWT.NONE);
        mntmMarkPriority3.setAccelerator(SWT.MOD1 + '3');
        mntmMarkPriority3.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (taskTable.isFocusControl() && taskTable.getSelectionCount() != 0)
                    togglePriority(Priority.LOW);
            }
        });
        mntmMarkPriority3.setText(getResourceString("togglePriority3"));
        
        new MenuItem(menuEdit, SWT.SEPARATOR);
    
        final MenuItem mntmSearch = new MenuItem(menuEdit, SWT.NONE);
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
    
        MenuItem mntmStatistics = new MenuItem(menuHelp, SWT.NONE);
        mntmStatistics.setText(getResourceString("statistics"));
    
        new MenuItem(menuHelp, SWT.SEPARATOR);
    
        MenuItem mntmHelp = new MenuItem(menuHelp, SWT.NONE);
        mntmHelp.setText(getResourceString("help"));
    
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
        mntmAbout.setText(getResourceString("about"));
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
     * 
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
                editTask();
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
                displayCurrentList(null);
            }
        });
        btnAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnAll.setText(getResourceString("filter.all"));

        Button btnImportant = new Button(this, SWT.NONE);
        btnImportant.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnImportant.setText(getResourceString("filter.important"));

        Button btnCompleted = new Button(this, SWT.NONE);
        btnCompleted.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnCompleted.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnCompleted.setText(getResourceString("filter.completed"));

        Button btnOverdue = new Button(this, SWT.NONE);
        btnOverdue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnOverdue.setText(getResourceString("filter.overdue"));
        
        Label lblSps = new Label(this, SWT.NONE);
        lblSps.setText("        ");
        lblSps.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Button btnToday = new Button(this, SWT.NONE);
        btnToday.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnToday.setText(getResourceString("filter.today"));

        Button btnTomorrow = new Button(this, SWT.NONE);
        btnTomorrow.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnTomorrow.setText(getResourceString("filter.tomorrow"));

        Button btnNextDays = new Button(this, SWT.NONE);
        btnNextDays.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        btnNextDays.setText(getResourceString("filter.custom"));

        Button btnWithoutDate = new Button(this, SWT.NONE);
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

    private void displayLists() {
        taskList.removeAll();
        
        displayNewList(TaskLists.INBOX);
    
        try {
            for (Entry<String, TaskList> list : lists) {
                if (list.getKey().equalsIgnoreCase(TaskLists.INBOX)
                        || list.getKey().equalsIgnoreCase(TaskLists.TRASH)) {
                    continue;
                }

                displayNewList(list.getKey());
            }
        } catch (NullPointerException e) {
            // do nothing
        }
    }

    /**
     * display the new list name in taskList table
     * 
     * @param name
     * @return
     */
    private TableItem displayNewList(String name) {
        TableItem item = new TableItem(taskList, SWT.NONE);
        item.setText(name);
        
        highlightList(item);
        return item;
    }

    private void displayCurrentList(String name) {
        if (name != null) {
            currentList = lists.getList(name);
        }
        
        displayTasks(currentList);
    }

    private void displayTasks(TaskList tlist) {
        taskTable.removeAll(); // remove all items for redraw
        
        try {
            int idx = 1;
            for (Task task : tlist) {
                idx = displayNewTask(idx, task);
            }
        } catch (NullPointerException e) {
            // do nothing
        }
    }
    
    private void displayTasksUp(TaskList tlist) {
        taskTable.removeAll(); // remove all items for redraw
        
        for (int i = tlist.getSize(); i > 0; i--) {
            displayNewTask(i, tlist.getTask(i));
        }
    }

    /**
     * @param idx
     * @param task
     * @return
     */
    private int displayNewTask(int idx, Task task) {
        TableItem tableItem;
        tableItem = new TableItem(taskTable, SWT.NONE);
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
    
    private void sortList(int columnIdx) {
        if (mode == MODE_LIST) {
            
            currentList.sort(columnIdx);
            
            if (lastSortColumn != columnIdx) {
                taskTable.setSortColumn(taskTable.getColumn(columnIdx));
                taskTable.setSortDirection(SWT.DOWN);
                
                displayTasks(currentList);
                
                lastSortColumn = columnIdx;
            } else {
                taskTable.setSortDirection(SWT.UP);
                
                displayTasksUp(currentList);
                
                lastSortColumn = -1;
            }
            
        } else if (mode == MODE_SEARCH) {
            
        }
        
    }

    /**
     * ask the user to enter new list name and add it
     */
    private void addList() {
        AddListDialog dialog = new AddListDialog(this);
        
        String newList = dialog.open();
        
        if (newList != null && newList.length() > 1) {
            if (!lists.hasList(newList)) {
                String feedback = String.format(ADD_FAIL, LIST, newList);
                
                if (lists.addList(newList)) {
                    feedback = String.format(ADD_SUCCESS, LIST, newList);
                    displayNewList(newList);
                    isModified = true;
                }
                
                setStatusBar(feedback);
            } else {
                setStatusBar(String.format(LIST_EXIST, newList));
            }
        }
    }

    private void addTask() {
        TaskDetailDialog dialog = new TaskDetailDialog(this, TaskDetailDialog.ADD_TASK);
        
        Task newTask = new Task();
        newTask.setList(currentList.getName());
        
        dialog.setTask(newTask);
        
        String feedback = dialog.open();
        if (feedback != null) {
            if (currentList.addTask(newTask)) {
                feedback = String.format(ADD_SUCCESS, TASK, newTask.getName());
                
                displayNewTask(taskTable.getItemCount()+1, newTask);
                isModified = true;
            } else {
                feedback = String.format(ADD_FAIL, TASK, newTask.getName());
            }
            
            setStatusBar(feedback);
        }
    }

    /**
     * 
     * @param tableItem
     */
    private void editTask() {
        int index = getSelectedIdx();

        TaskDetailDialog dialog = new TaskDetailDialog(this, TaskDetailDialog.EDIT_TASK);

        Task task = currentList.getTask(index);
        dialog.setTask(task);

        String feedback = dialog.open();
        if (feedback != null) {
            refreshTask(index, task);
            isModified = true;
            setStatusBar(feedback);
        }
    }
    
    private void deleteTask() {
        int index = getSelectedIdx();
        
        if (mode == MODE_LIST) {
            Task delTask = currentList.removeTask(index);
            
            String feedback = String.format(DELETE_FAIL, TASK);
            if (delTask != null) {
                feedback = String.format(DELETE_SUCCESS, TASK, delTask.getName());
                isModified = true;
            }
            
            displayTasks(currentList);
            setStatusBar(feedback);
        } else if (mode == MODE_SEARCH) {
            
        }
    }
    
    private void toggleTaskStatus() {
        int index = getSelectedIdx();
        
        if (mode == MODE_LIST) {
            String feedback;
            Task task = currentList.getTask(index);
            
            if (task.isCompleted()) {
                task.setStatus(Task.INCOMPLETE);
                feedback = String.format(TOGGLE, task.getName(), task.getStatusStr());
            } else {
                task.setStatus(Task.COMPLETED);
                feedback = String.format(TOGGLE, task.getName(), task.getStatusStr());
            }
            
            isModified = true;
            refreshTask(index, task);
            setStatusBar(feedback);
        } else if (mode == MODE_SEARCH) {
            
        }
    }
    
    private void togglePriority(Priority newPriority) {
        int index = getSelectedIdx();
        
        if (mode == MODE_LIST) {
            Task task = currentList.getTask(index);
            
            task.setPriority(newPriority);
            String feedback = String.format(TOGGLE, task.getName(), task.getPriorityStr());
            
            isModified = true;
            refreshTask(index, task);
            setStatusBar(feedback);
        } else if (mode == MODE_SEARCH) {
            
        }
    }

    private void switchList(String listname) {
        // check if the list is not shown
        if (!currentList.getName().equals(listname)) {
            displayCurrentList(listname);
        }
        
        // re-highlight the lists
        TableItem[] lists = taskList.getItems();
        for (TableItem list : lists) {
            highlightList(list);
        }
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
        if (currentList.getName().equals(l.getText())) {
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
        
        setStatusBar(text);
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
    private static final String LIST           = "List";
    private static final String TASK           = "Task";
    private static final String TOGGLE         = "Task \"%1$s\" is marked as %2$s";
    private static final String LIST_EXIST     = "List \"%1$s\" already exists";
    private static final String ADD_SUCCESS    = "%1$s \"%2$s\" is successfully added";
    private static final String ADD_FAIL       = "%1$s \"%2$s\" fail to add";
    private static final String DELETE_SUCCESS = "%1$s \"%2$s\" is successfully deleted";
    private static final String DELETE_FAIL    = "%1$s fail to delete";
}