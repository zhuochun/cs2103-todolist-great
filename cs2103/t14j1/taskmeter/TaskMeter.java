package cs2103.t14j1.taskmeter;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

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
	private Text smartBar;
	private boolean isModified;
	private Table taskList;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
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
	 * Create the shell.
	 * @param display
	 */
	public TaskMeter(Display display) {
		super(display, SWT.CLOSE | SWT.MIN | SWT.TITLE);
		
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
	}

	/**
	 * 
	 */
	private void createSmartBar() {
		smartBar = new Text(this, SWT.BORDER);
		smartBar.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		smartBar.setBounds(5, 5, 735, 30);
	}

	/**
	 * Creates the menu at the top of the shell where most
	 * of the programs functionality is accessed.
	 *
	 * @return	The <code>Menu</code> widget that was created
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
	 * 
	 */
	private void createTaskList() {
		taskList = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		taskList.setBounds(5, 40, 150, 350);
		taskList.setHeaderVisible(true);
		taskList.setLinesVisible(true);
		
		TableColumn tblclmnLists = new TableColumn(taskList, SWT.CENTER);
		tblclmnLists.setResizable(false);
		tblclmnLists.setWidth(146);
		tblclmnLists.setText(getResourceString("list"));
		
		TableItem tableInbox = new TableItem(taskList, SWT.NONE);
		tableInbox.setChecked(true);
		tableInbox.setText(getResourceString("list.inbox"));
		
		TableItem tableTrash = new TableItem(taskList, SWT.NONE);
		tableTrash.setText(getResourceString("list.trash"));
		
		Button btnAddANew = new Button(this, SWT.NONE);
		btnAddANew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addNewTask();
			}
		});
		btnAddANew.setBounds(4, 395, 151, 25);
		btnAddANew.setText(getResourceString("list.add"));
	}
	
	private void addNewTask() {
		AddListDialog dialog = new AddListDialog(this);
		String newList = dialog.open();
		if (newList != null)
			setStatusBar(newList);
	}

	/**
	 * 
	 */
	private void createTaskTable() {
		taskTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		taskTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = taskTable.getSelection();
				if (items.length > 0) {
					editTask(items[0]);
				}
			}
		});
		taskTable.setBounds(160, 40, 579, 350);
		taskTable.setHeaderVisible(true);
		taskTable.setLinesVisible(true);
		
		TableColumn tblclmnId = new TableColumn(taskTable, SWT.CENTER);
		tblclmnId.setResizable(false);
		tblclmnId.setWidth(26);
		tblclmnId.setText(getResourceString("table.id"));
		
		TableColumn tblclmnName = new TableColumn(taskTable, SWT.CENTER);
		tblclmnName.setWidth(218);
		tblclmnName.setText(getResourceString("table.name"));
		
		TableColumn tblclmnPriority = new TableColumn(taskTable, SWT.CENTER);
		tblclmnPriority.setResizable(false);
		tblclmnPriority.setMoveable(true);
		tblclmnPriority.setWidth(50);
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
		tblclmnCompleted.setWidth(75);
		tblclmnCompleted.setText(getResourceString("table.completed"));
	}

	/**
	 * 
	 */
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

	/**
	 * 
	 */
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
		
		MenuItem mntmUndo = new MenuItem(menuEdit, SWT.NONE);
		mntmUndo.setText(getResourceString("undo"));
		
		MenuItem mntmRedo = new MenuItem(menuEdit, SWT.NONE);
		mntmRedo.setText(getResourceString("redo"));
		
		new MenuItem(menuEdit, SWT.SEPARATOR);
		
		MenuItem mntmSearch = new MenuItem(menuEdit, SWT.NONE);
		mntmSearch.setText(getResourceString("find"));
	}

	/**
	 * @param menuBar
	 */
	private void createUserMenu(Menu menuBar) {
		MenuItem mntmUser = new MenuItem(menuBar, SWT.CASCADE);
		mntmUser.setText(getResourceString("menu.user"));
		
		Menu menuUser = new Menu(mntmUser);
		mntmUser.setMenu(menuUser);
		
		MenuItem mntmSave = new MenuItem(menuUser, SWT.NONE);
		mntmSave.setText(getResourceString("save"));
		
		new MenuItem(menuUser, SWT.SEPARATOR);
		
		MenuItem mntmExit = new MenuItem(menuUser, SWT.NONE);
		mntmExit.setText(getResourceString("exit"));
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean closeTaskMeter() {
		if(isModified) {
			//ask user if they want to save current address book
			MessageBox box = new MessageBox(this, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			box.setText(this.getText());
			box.setMessage(getResourceString("msg.close"));
		
			int choice = box.open();
			if(choice == SWT.CANCEL) {
				return false;
			} else if(choice == SWT.YES) {
				if (!save()) return false;
			}
		}
			
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean save() {
		
		return true;
	}
	
	/**
	 * 
	 * @param tableItem
	 */
	private void editTask(TableItem tableItem) {
		TaskDetailDialog dialog = new TaskDetailDialog(this);
		String[] values = dialog.open();
		
		if (values != null) {
			isModified = true;
		}
		/*
		dialog.setLabels(columnNames);
		String[] values = new String[table.getColumnCount()];
		for (int i = 0; i < values.length; i++) {
			values[i] = item.getText(i);
		}
		dialog.setValues(values);
		values = dialog.open();
		if (values != null) {
			item.setText(values);
			isModified = true;
		}
		*/
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
	 * Returns a string from the resource bundle.
	 * We don't want to crash because of a missing String.
	 * Returns the key if not found.
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
