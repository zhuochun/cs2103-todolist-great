package cs2103.t14j1.taskmeter;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class TaskMeter extends Shell {
	
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("taskmeter_res");
	private Label statusBar;
	private List taskLists;
	private Table taskTable;
	private Text smartBar;
	
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
		setMinimumSize(new Point(750, 500));
		setLayout(null);
		
		Menu menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);
		
		MenuItem mntmUser = new MenuItem(menu, SWT.CASCADE);
		mntmUser.setText(getResourceString("menu.user"));
		
		Menu menuUser = new Menu(mntmUser);
		mntmUser.setMenu(menuUser);
		
		MenuItem mntmSave = new MenuItem(menuUser, SWT.NONE);
		mntmSave.setText(getResourceString("save"));
		
		new MenuItem(menuUser, SWT.SEPARATOR);
		
		MenuItem mntmExit = new MenuItem(menuUser, SWT.NONE);
		mntmExit.setText(getResourceString("exit"));
		
		MenuItem mntmEdit = new MenuItem(menu, SWT.CASCADE);
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
		
		MenuItem mntmWindow = new MenuItem(menu, SWT.CASCADE);
		mntmWindow.setText(getResourceString("menu.window"));
		
		Menu menuWindow = new Menu(mntmWindow);
		mntmWindow.setMenu(menuWindow);
		
		MenuItem mntmListView = new MenuItem(menuWindow, SWT.CHECK);
		mntmListView.setSelection(true);
		mntmListView.setText(getResourceString("list"));
		
		MenuItem mntmCalendarView = new MenuItem(menuWindow, SWT.CHECK);
		mntmCalendarView.setText(getResourceString("calendar"));
		
		MenuItem mntmSettings = new MenuItem(menu, SWT.CASCADE);
		mntmSettings.setText(getResourceString("menu.setting"));
		
		Menu menuSetting = new Menu(mntmSettings);
		mntmSettings.setMenu(menuSetting);
		
		MenuItem mntmUserSettings = new MenuItem(menuSetting, SWT.NONE);
		mntmUserSettings.setText(getResourceString("setting"));
		
		MenuItem mntmHelps = new MenuItem(menu, SWT.CASCADE);
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
		mntmAbout.setText(getResourceString("about"));
		
		smartBar = new Text(this, SWT.BORDER);
		smartBar.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		smartBar.setBounds(5, 5, 735, 30);
		
		Label statusBarSeperator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		statusBarSeperator.setBounds(0, 425, 750, 2);
		
		statusBar = new Label(this, SWT.NONE);
		statusBar.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		statusBar.setBounds(10, 430, 730, 20);
		statusBar.setText(getResourceString("msg.welcome"));
		
		taskLists = new List(this, SWT.BORDER);
		taskLists.setBounds(5, 40, 150, 350);
		
		Button btnAddANew = new Button(this, SWT.NONE);
		btnAddANew.setBounds(5, 395, 150, 25);
		btnAddANew.setText(getResourceString("add.list"));
		
		taskTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
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
		
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText(getResourceString("app.title.ful"));
		setSize(750, 500);
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
