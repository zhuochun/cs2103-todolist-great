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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
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

import cs2103.t14j1.storage.FileHandler;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;

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

    private boolean  isModified;
    private TaskLists lists;
    private TaskList  currentList;
    private TaskList  searchResult;

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
    public TaskMeter(Display display) {
        super(display, SWT.CLOSE | SWT.MIN | SWT.TITLE);
        
        // global hotkey to focus on smartBar
        display.addFilter(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'k')) {
                    smartBar.setFocus();
                }
            }
        });

        setImage(new Image(display, "taskMeter.png"));
        setMinimumSize(new Point(750, 500));
        setLayout(null);

        createMenuBar();
        createSmartBar();
        createTaskList();
        createTaskTable();
        createFilterButtons();
        createStatusBar();

        createContents();
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

        lists = new TaskLists();
        FileHandler.loadAll(lists);
        
        displayCurrentList(TaskLists.INBOX);
        displayLists();
    }

    /**
	 * create smartBar text input box
	 */
    private void createSmartBar() {
        smartBar = new Text(this, SWT.BORDER);
        smartBar.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
        smartBar.setBounds(5, 5, 735, 30);
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
	 * create the task list viewer that shows all the lists
	 */
    private void createTaskList() {
        taskList = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) { // switch lists
                // get clicked list name
                TableItem[] items = taskList.getSelection();
                String listname = items[0].getText();

                // check if the list is not shown
                if (!currentList.getName().equals(listname)) {
                    displayCurrentList(listname);
                }
                
                // re-highlight the list
                TableItem[] lists = taskList.getItems();
                for (TableItem list : lists) {
                    highlightList(list);
                }
            }
        });
        taskList.setBounds(5, 40, 150, 350);
        taskList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        taskList.setHeaderVisible(true);
        taskList.setLinesVisible(true);

        TableColumn tblclmnLists = new TableColumn(taskList, SWT.CENTER);
        tblclmnLists.setResizable(false); tblclmnLists.setWidth(146);
        tblclmnLists.setText(getResourceString("list"));

        Button btnAddANew = new Button(this, SWT.NONE);
        btnAddANew.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addNewList();
            }
        });
        btnAddANew.setBounds(4, 395, 151, 25);
        btnAddANew.setText(getResourceString("list.add"));
    }

    /**
     * ask the user to enter new list name and add it
     */
    private void addNewList() {
        AddListDialog dialog = new AddListDialog(this);
        
        String newList = dialog.open();
        
        if (newList != null && newList.length() > 1) {
            isModified = true;

            String feedback = lists.add(newList);

            displayNewList(newList);
            setStatusBar(feedback);
        }
    }

    /**
     * display the new list name in taskList table
     * 
     * @param name
     * @return
     */
    private TableItem displayNewList(String name) {
        TableItem table = new TableItem(taskList, SWT.NONE);
        table.setText(name);
        return table;
    }

    /**
	 * 
	 */
    private void createTaskTable() {
        taskTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                editTask();
            }
        });
        taskTable.setBounds(160, 40, 579, 350);
        taskTable.setHeaderVisible(true);
        taskTable.setLinesVisible(true);

        TableColumn tblclmnId = new TableColumn(taskTable, SWT.CENTER);
        tblclmnId.setWidth(28);
        tblclmnId.setText(getResourceString("table.id"));

        TableColumn tblclmnName = new TableColumn(taskTable, SWT.CENTER);
        tblclmnName.setWidth(200);
        tblclmnName.setText(getResourceString("table.name"));

        TableColumn tblclmnPriority = new TableColumn(taskTable, SWT.CENTER);
        tblclmnPriority.setMoveable(true);
        tblclmnPriority.setWidth(68);
        tblclmnPriority.setText(getResourceString("table.priority"));

        TableColumn tblclmnDate = new TableColumn(taskTable, SWT.CENTER);
        tblclmnDate.setMoveable(true);
        tblclmnDate.setWidth(105);
        tblclmnDate.setText(getResourceString("table.date"));

        TableColumn tblclmnDuration = new TableColumn(taskTable, SWT.CENTER);
        tblclmnDuration.setMoveable(true);
        tblclmnDuration.setWidth(100);
        tblclmnDuration.setText(getResourceString("table.duration"));

        TableColumn tblclmnCompleted = new TableColumn(taskTable, SWT.CENTER);
        tblclmnCompleted.setMoveable(true);
        tblclmnCompleted.setWidth(73);
        tblclmnCompleted.setText(getResourceString("table.completed"));
    }

    private void displayCurrentList(String name) {
        currentList = lists.getList(name);
        displayTasks(currentList);
    }
    
    private void displayTasks(TaskList tlist) {
        taskTable.removeAll(); // remove all items for redraw
        
        int idx = 1;
        
        TableItem tableItem;
        for (Task task : tlist) {
            tableItem = new TableItem(taskTable, SWT.NONE);
            tableItem.setText(new String[] {
                    Integer.toString(idx++),
                    task.getName(),
                    task.getPriority().toString().toLowerCase(),
                    task.getDate(),
                    task.getDurationStr(),
                    task.getStatusStr()
                    });
        }
        
    }

    private void displayLists() {
        taskList.removeAll();
        
        TableItem item = displayNewList(TaskLists.INBOX);
        highlightList(item);

        for (Entry<String, TaskList> list : lists) {
            if (list.getKey().equalsIgnoreCase(TaskLists.INBOX)
                    || list.getKey().equalsIgnoreCase(TaskLists.TRASH)) {
                continue;
            }

            displayNewList(list.getKey());
            highlightList(item);
        }

        displayNewList(TaskLists.TRASH);
        highlightList(item);
    }
    
    private void highlightList(TableItem l) {
        if (currentList.getName().equals(l.getText())) {
            l.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
        } else {
            l.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        }
    }

    private void createFilterButtons() {
        Button btnAll = new Button(this, SWT.NONE);
        btnAll.setBounds(160, 395, 35, 25);
        btnAll.setText(getResourceString("filter.all"));

        Button btnImportant = new Button(this, SWT.NONE);
        btnImportant.setBounds(200, 395, 65, 25);
        btnImportant.setText(getResourceString("filter.important"));

        Button btnCompleted = new Button(this, SWT.NONE);
        btnCompleted.setBounds(270, 395, 75, 25);
        btnCompleted.setText(getResourceString("filter.completed"));

        Button btnOverdue = new Button(this, SWT.NONE);
        btnOverdue.setBounds(350, 395, 65, 25);
        btnOverdue.setText(getResourceString("filter.overdue"));

        Button btnToday = new Button(this, SWT.NONE);
        btnToday.setBounds(440, 395, 45, 25);
        btnToday.setText(getResourceString("filter.today"));

        Button btnTomorrow = new Button(this, SWT.NONE);
        btnTomorrow.setBounds(490, 395, 75, 25);
        btnTomorrow.setText(getResourceString("filter.tomorrow"));

        Button btnNextDays = new Button(this, SWT.NONE);
        btnNextDays.setBounds(570, 395, 75, 25);
        btnNextDays.setText(getResourceString("filter.custom"));

        Button btnWithoutDate = new Button(this, SWT.NONE);
        btnWithoutDate.setBounds(650, 395, 90, 25);
        btnWithoutDate.setText(getResourceString("filter.nodate"));
    }

    private void createStatusBar() {
        Label statusBarSeperator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        statusBarSeperator.setBounds(0, 425, 750, 2);

        statusBar = new Label(this, SWT.NONE);
        statusBar.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        statusBar.setBounds(10, 430, 730, 20);

        setStatusBar(getResourceString("msg.welcome"));
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
    private void createWindowMenu(Menu menuBar) {
        MenuItem mntmWindow = new MenuItem(menuBar, SWT.CASCADE);
        mntmWindow.setText(getResourceString("menu.window"));

        Menu menuWindow = new Menu(mntmWindow);
        mntmWindow.setMenu(menuWindow);

        MenuItem mntmListView = new MenuItem(menuWindow, SWT.RADIO);
        mntmListView.setSelection(true);
        mntmListView.setText(getResourceString("list"));

        MenuItem mntmCalendarView = new MenuItem(menuWindow, SWT.RADIO);
        mntmCalendarView.setText(getResourceString("calendar"));
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
        mntmEditTask.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                editTask();
            }
        });
        mntmEditTask.setText("Edit");

        final MenuItem mntmDeleteTask = new MenuItem(menuEdit, SWT.NONE);
        mntmDeleteTask.setText("Delete");

        new MenuItem(menuEdit, SWT.SEPARATOR);

        final MenuItem mntmSearch = new MenuItem(menuEdit, SWT.NONE);
        mntmSearch.setText(getResourceString("find"));
        
        menuEdit.addMenuListener(new MenuAdapter() {
            public void menuShown(MenuEvent e) {
                mntmEditTask.setEnabled(taskTable.getSelectionCount() != 0);
                mntmDeleteTask.setEnabled(taskTable.getSelectionCount() != 0);
            }
        });
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
                addNewList();
            }
        });
        mntmAddList.setText("Add List");

        MenuItem mntmAddTask = new MenuItem(menuUser, SWT.NONE);
        mntmAddTask.setAccelerator(SWT.MOD1 + 'N');
        mntmAddTask.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addTask();
            }
        });
        mntmAddTask.setText("Add New Task");

        new MenuItem(menuUser, SWT.SEPARATOR);

        MenuItem mntmSave = new MenuItem(menuUser, SWT.NONE);
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
                if (!save())
                    return false;
            }
        }

        return true;
    }

    /**
     * 
     * @return
     */
    private boolean save() {
        FileHandler.saveAll(lists);
        return true;
    }

    /**
     * 
     * @param tableItem
     */
    private void editTask() {
        TableItem[] items = taskTable.getSelection();
        if (items.length > 0) {
            int index = Integer.parseInt(items[0].getText());

            TaskDetailDialog dialog = new TaskDetailDialog(this, 0);
            dialog.setTask(currentList.getTask(index));

            String feedback = dialog.open();
            if (feedback != null) {
                setStatusBar(feedback);
                isModified = true;
            }
        }
    }
    
    private void addTask() {
        TaskDetailDialog dialog = new TaskDetailDialog(this, 1);
        
        Task newTask = new Task();
        
        dialog.setTask(newTask);
        
        String feedback = dialog.open();
        
        if (feedback != null) {
            setStatusBar(feedback);
            isModified = true;
        }
    }

    private void setStatusBar(String msg) {
        statusBar.setText(msg);
    }

    private void displayError(String msg) {
        MessageBox box = new MessageBox(this, SWT.ICON_ERROR);
        box.setText("Error");
        box.setMessage(msg);
        box.open();
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
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

}
