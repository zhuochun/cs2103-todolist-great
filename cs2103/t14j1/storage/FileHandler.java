package cs2103.t14j1.storage;

import java.util.Date;

import cs2103.t14j1.logic.DateFormat;


/**
 * the file handler to load from and save to files
 * 
 * TODO: Worapol, I have wrote the basic functions and demo, you only need to add
 *       code that open XML files, read it and save it.
 * 
 * @author Zhuochun, Worapol
 *
 */
public class FileHandler {
	
	private static String taskFile = "user/tasks.txt";
	private static String listFile = "user/lists.txt";
	
	private static final String LOAD_SUCCESS = "All Lists and Tasks are Ready!";
	private static final String SAVE_SUCCESS = "All Lists and Tasks are Saved!";
	
	public static String loadAll(TaskLists lists) {
		loadLists(lists);
		loadTasks(lists);
		
		return LOAD_SUCCESS;
	}
	
	private static void loadLists(TaskLists lists) {
		// this is a demo of how to add a list
		lists.add("test list");
		
		// TODO: open the listFile and load it lists
	}

	
	private static void loadTasks(TaskLists lists) {
		
		// these are demos of how to add a task into list
		// delete these demos only after you finished the loading
		// because they are used for other layer for testing
		
		// add one task
		String name = "new task";
		String list = "inbox";
		Priority priority = Priority.IMPORTANT;
		Date startDateTime = null;
		Date endDateTime = null;
		boolean status = Task.NOT_COMPLETED;
		Task newTask = new Task(name, list, priority, startDateTime, endDateTime, status);
		lists.addTask(list, newTask);
		
		// add another task
		name = "new task 2";
		list = "inbox";
		priority = Priority.NORMAL;
		startDateTime = DateFormat.strToDateLong("2011-9-20 14:20:20");
		endDateTime = DateFormat.strToDateLong("2011-9-20 15:20:30");
		status = Task.NOT_COMPLETED;
		newTask = new Task(name, list, priority, startDateTime, endDateTime, status);
		lists.addTask(list, newTask);
		
		// TODO: open the taskFile and load all the tasks into lists
		
	}
	
	public static String saveAll(TaskLists lists) {
		String[] listNames = lists.getListNames(); // it will give you all the lists name
		
		saveLists(listNames);
		saveTasks(listNames, lists);
		
		return SAVE_SUCCESS;
	}
	
	/**
	 * 
	 * 
	 * @param lists
	 */
	private static void saveLists(String[] names) {
		
		for (String name : names) {
			// TODO: save list name to XML listFile
		}
		
	}
	
	/**
	 * 
	 * TODO: this is slow to extract list one by one using name,
	 *       there is better solution which is to implement iteration for
	 *       taskLists class. (left this to next version)
	 * 
	 * @param lists
	 */
	private static void saveTasks(String[] names, TaskLists lists) {
		
		for (String name : names) {
			TaskList list = lists.getList(name);
			
			int numTasks = list.getSize();
			
			for (int i = 0; i < numTasks; i++) {
				Task task = list.getTask(i);
				
				// TODO: write task detail to XML taskFile
				// You can use task.getName() etc to get what you need to write,
				// Note, use task.getStartLong() and task.getEndLong() to write time
				
			}
		}
		
	}
	
}