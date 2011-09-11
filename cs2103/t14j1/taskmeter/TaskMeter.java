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
		
		MenuItem mntmfile = new MenuItem(menu, SWT.CASCADE);
		mntmfile.setText("File");
		
		Menu menu_1 = new Menu(mntmfile);
		mntmfile.setMenu(menu_1);
		
		MenuItem mntmEdit = new MenuItem(menu, SWT.CASCADE);
		mntmEdit.setText("Edit");
		
		Menu menu_2 = new Menu(mntmEdit);
		mntmEdit.setMenu(menu_2);
		
		MenuItem mntmWindow = new MenuItem(menu, SWT.CASCADE);
		mntmWindow.setText("Window");
		
		Menu menu_3 = new Menu(mntmWindow);
		mntmWindow.setMenu(menu_3);
		
		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");
		
		Menu menu_4 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_4);
		
		smartBar = new Text(this, SWT.BORDER);
		smartBar.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		smartBar.setBounds(5, 5, 735, 30);
		
		Label statusBarSeperator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		statusBarSeperator.setBounds(0, 425, 750, 2);
		
		statusBar = new Label(this, SWT.NONE);
		statusBar.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		statusBar.setBounds(10, 430, 730, 20);
		statusBar.setText("StatusBar");
		
		taskLists = new List(this, SWT.BORDER);
		taskLists.setBounds(5, 40, 150, 350);
		
		Button btnAddANew = new Button(this, SWT.NONE);
		btnAddANew.setBounds(5, 395, 150, 25);
		btnAddANew.setText("Add A New List");
		
		taskTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		taskTable.setBounds(160, 40, 579, 350);
		taskTable.setHeaderVisible(true);
		taskTable.setLinesVisible(true);
		
		TableColumn tblclmnId = new TableColumn(taskTable, SWT.CENTER);
		tblclmnId.setResizable(false);
		tblclmnId.setWidth(26);
		tblclmnId.setText("Id");
		
		TableColumn tblclmnName = new TableColumn(taskTable, SWT.CENTER);
		tblclmnName.setWidth(218);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnPriority = new TableColumn(taskTable, SWT.CENTER);
		tblclmnPriority.setResizable(false);
		tblclmnPriority.setMoveable(true);
		tblclmnPriority.setWidth(50);
		tblclmnPriority.setText("Priority");
		
		TableColumn tblclmnDate = new TableColumn(taskTable, SWT.CENTER);
		tblclmnDate.setMoveable(true);
		tblclmnDate.setWidth(105);
		tblclmnDate.setText("Date");
		
		TableColumn tblclmnDuration = new TableColumn(taskTable, SWT.CENTER);
		tblclmnDuration.setMoveable(true);
		tblclmnDuration.setWidth(100);
		tblclmnDuration.setText("Duration");
		
		TableColumn tblclmnCompleted = new TableColumn(taskTable, SWT.CENTER);
		tblclmnCompleted.setMoveable(true);
		tblclmnCompleted.setWidth(75);
		tblclmnCompleted.setText("Completed");
		
		Button btnAll = new Button(this, SWT.NONE);
		btnAll.setBounds(160, 395, 35, 25);
		btnAll.setText("All");
		
		Button btnImportant = new Button(this, SWT.NONE);
		btnImportant.setBounds(200, 395, 65, 25);
		btnImportant.setText("Important");
		
		Button btnCompleted = new Button(this, SWT.NONE);
		btnCompleted.setBounds(270, 395, 75, 25);
		btnCompleted.setText("Completed");
		
		Button btnOverdue = new Button(this, SWT.NONE);
		btnOverdue.setBounds(350, 395, 65, 25);
		btnOverdue.setText("Overdue");
		
		Button btnToday = new Button(this, SWT.NONE);
		btnToday.setBounds(440, 395, 45, 25);
		btnToday.setText("Today");
		
		Button btnTomorrow = new Button(this, SWT.NONE);
		btnTomorrow.setBounds(490, 395, 75, 25);
		btnTomorrow.setText("Tomorrow");
		
		Button btnNextDays = new Button(this, SWT.NONE);
		btnNextDays.setBounds(570, 395, 75, 25);
		btnNextDays.setText("Next 7 Days");
		
		Button btnWithoutDate = new Button(this, SWT.NONE);
		btnWithoutDate.setBounds(650, 395, 90, 25);
		btnWithoutDate.setText("Without Date");
		
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText(getResourceString("window.title"));
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
