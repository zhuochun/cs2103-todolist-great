package cs2103.t14j1.storage;

import java.util.Date;


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
		
		// this is a demo of how to add a task into list
		String name = "new task";
		String list = "inbox";
		Priority priority = Priority.IMPORTANT;
		Date startDateTime = null;
		Date endDateTime = null;
		boolean status = Task.NOT_COMPLETED;
		
		Task newTask = new Task(name, list, priority, startDateTime, endDateTime, status);
		
		lists.addTask("inbox", newTask);
		
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
			// TODO: save name to XML listFile
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
				
				// TODO: write task detail to XML
				
			}
		}
		
	}
	
}