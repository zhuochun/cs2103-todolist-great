package cs2103.t14j1.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Map.Entry;

import cs2103.t14j1.logic.smartbar.ParseCommand;
import cs2103.t14j1.storage.FileHandler;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;

/**
 * It is the centre of logic layer.
 * It also serves as the command line interface for version 0.1
 * Afterwards, it serve as the bridge between user interface and logic
 *
 * @author Shubham, Zhuochun
 * 
 */
class Control {
	
	TaskLists lists;
	ParseCommand parseCommand;

	/**
	 * command line interface, just for version 0.1
	 * will be replaced by user interface, so I keep it short
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		Control cml  = new Control();
		String  line, result;
		
		while (true) {
			System.out.print("Command: ");
			line = scan.nextLine();
			result = cml.processInput(line); // result is a String that will be display in statusBar in GUI
			System.out.println(result);
		}
	}
	
	/**
	 * constructor
	 */
	public Control() {
		lists = new TaskLists();
		FileHandler.loadAll(lists); // call storage to load all lists and tasks from file
	}
	
	/**
	 * process input line
	 * 
	 * @param input
	 * @return
	 */
	public String processInput(String input) {
		try {
            parseCommand = new ParseCommand(input);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		Commands command = parseCommand.extractCommand();
		String feedback  = executeCommand(command, input);
		return feedback;
	}

	/**
	 * execute command
	 * 
	 * @param command
	 * @param input
	 * @return feedback
	 */
	public String executeCommand(Commands command, String input) {
		switch (command) {
		case ADD_TASK:
			return addTask(input);
		case ADD_LIST:
			return addList(input);
		case SEARCH:
			return search(input);
		case DELETE_TASK:
			return deleteTask(input);
		case DELETE_LIST:
			return deleteList(input);
		case EDIT_TASK:
			return editTask(input);
		case EDIT_LIST:
			return editList(input);
		case SORT:
			return sortBy(input);
		case SWITCH_LIST:
			switchList(input); 
		default:
			return "invalid command";
		}
	}

	/**
	 * add task into TaskLists
	 * 
	 * @param input
	 * @return
	 */
	private String addTask(String input) {
		String name        = parseCommand.extractTaskName();
		String list        = parseCommand.extractListName();
		Priority priority  = parseCommand.extractPriority();
		Date startDateTime = parseCommand.extractStartDate();
		Date endDateTime   = parseCommand.extractEndDate();
		boolean status    = Task.NOT_COMPLETED;
		
		Task newTask = new Task(name, list, priority, startDateTime, endDateTime, status);
		String result = lists.addTask(list, newTask);
		
		return result;
	}

	private String addList(String input) {
		
		String name = parseCommand.extractListName();
		
		lists.add(name);
		
		String LIST_SUCCESSFULLY_ADDED = "List " + name + " has been successfully created and added";
		
		return LIST_SUCCESSFULLY_ADDED;
	}

	//not yet complete
	private String search(String input) {
		
		String name = parseCommand.extractTaskName();
		Date startDate = parseCommand.extractStartDate();
		Date endDate = parseCommand.extractEndDate();
		Long startTime = parseCommand.extractStartTime();
		Long endTime = parseCommand.extractEndTime();
		String place = parseCommand.extractPlace();
		Priority priority = parseCommand.extractPriority();
		
		return null;
	}

	private String deleteTask(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String deleteList(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String editTask(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String editList(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String sortBy(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	public String save() {
		
		FileHandler.saveAll(lists);
		
		String SAVE_COMPLETED = "Saved";
		
		return SAVE_COMPLETED;
		
	}
	
	public void display(cs2103.t14j1.storage.TaskList listName) {
		
	    /*
		List<cs2103.t14j1.storage.Task> list = listName.getAllTasks();
		
		for(int i = 0; i < list.size(); i ++) { 
			System.out.println((i + 1) + ". " + list.get(i).getName());
		}
		*/
	}
	
	public void switchList (String input) {
		
		String listName = parseCommand.extractListName();
		
		cs2103.t14j1.storage.TaskList list = findList(listName);
		
		display(list);
		
	}

	private TaskList findList(String listName) {

		for(Entry<String, TaskList> list: lists) {
			String name = list.getKey();
			if(name.equals(listName))
				return list.getValue();
		}
		
		return null;
	
	}
	
}