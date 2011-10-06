package cs2103.t14j1.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
	ArrayList<Task> latestSearchResults;

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
	 * Shubham: Have to add functionality for deadline
	 * 
	 * @param input
	 * @return
	 */
	private String addTask(String input) {
		String name        = parseCommand.extractTaskName();
		String list        = parseCommand.extractListName();
		Priority priority  = parseCommand.extractPriority();
		String place 	   = parseCommand.extractPlace();
		Date startDateTime = parseCommand.extractStartDate();
		Date endDateTime   = parseCommand.extractEndDate();
		Long startTime     = parseCommand.extractStartTime();
		Long endTime       = parseCommand.extractEndTime();
		Date deadline 	   = parseCommand.extractDeadlineDate();
		Long deadlineTime  = parseCommand.extractDeadlineTime();
		Long duration      = parseCommand.extractDuration();
		convertLongTimeToDateObject(startDateTime, startTime);
		convertLongTimeToDateObject(endDateTime, endTime);
		convertLongTimeToDateObject(deadline, deadlineTime);
		boolean status     = Task.NOT_COMPLETED;
		
		Task newTask = new Task(name, list, priority, startDateTime, endDateTime, status, deadline, duration, place);
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
		
		if(latestSearchResults.size() != 0)
			latestSearchResults.clear();
		
		String name = parseCommand.extractTaskName();
		Date startDateTime = parseCommand.extractStartDate();
		Date endDateTime = parseCommand.extractEndDate();
		Long startTime = parseCommand.extractStartTime();
		Long endTime = parseCommand.extractEndTime();
		String place = parseCommand.extractPlace();
		Priority priority = parseCommand.extractPriority();
		convertLongTimeToDateObject(startDateTime, startTime);
		convertLongTimeToDateObject(endDateTime, endTime);
		Date afterDate = parseCommand.extractSearchAfterDate();
		Long afterTime = parseCommand.extractSearchAfterTime();
		convertLongTimeToDateObject(afterDate, afterTime);
		Date beforeDate = parseCommand.extractSearchBeforeDate();
		Long beforeTime = parseCommand.extractSearchBeforeTime();
		convertLongTimeToDateObject(beforeDate, beforeTime);
		Date deadlineDate = parseCommand.extractDeadlineDate();
		Long deadlineTime = parseCommand.extractDeadlineTime();
		convertLongTimeToDateObject(deadlineDate, deadlineTime);
		Long duration = parseCommand.extractDuration();
		String listName = parseCommand.extractListName();
		
		Iterator<Entry<String, TaskList>> iterator = lists.iterator();
		while(iterator.hasNext()) {
			cs2103.t14j1.storage.TaskList taskList = iterator.next().getValue();
			for(int i = 0; i < taskList.getSize(); i ++) {
				if(isNameSame(name, taskList.getTask(i))
						&& isStartDateSame(startDateTime, taskList.getTask(i))
						&& isEndDateSame(endDateTime, taskList.getTask(i))
						&& isDeadlineDateSame(deadlineDate, taskList.getTask(i))
						&& isPlaceSame(place, taskList.getTask(i))
						&& isPrioritySame(priority, taskList.getTask(i))
						&& isDateAfterGivenDateAndTime(afterDate, taskList.getTask(i))
						&& isDateBeforeGivenDateAndTime(beforeDate, taskList.getTask(i))
						&& isDurationSame(duration, taskList.getTask(i))
						&& isListNameSame(listName, taskList.getTask(i))) {
					latestSearchResults.add(taskList.getTask(i));
				}
			}
		}
		
		display(latestSearchResults);
		
		String SEARCH_RESULT;
		
		if(latestSearchResults.size() == 0)
			SEARCH_RESULT = "No match found";
		else
			SEARCH_RESULT = "Search successful.";
		
		return SEARCH_RESULT;
	}
	
	private boolean isListNameSame(String listName, Task task) {
		return (listName.equals(task.getList()));
	}

	private boolean isDurationSame(Long duration, Task task) {
		return (duration == task.getDuration());
	}

	private boolean isDateBeforeGivenDateAndTime(Date beforeDate, Task task) {
		return (isDateBefore(task.getDeadline(), beforeDate) || isDateBefore(task.getEndDateInDateFormat(), beforeDate) || isDateBefore(task.getStartDateInDateFormat(), beforeDate));
	}
	
	private boolean isDateBefore(Date a, Date b) {
		return (a.getTime() <= b.getTime()); 
	}

	private boolean isDateAfterGivenDateAndTime(Date afterDate, Task task) {
		return (isDateAfter(task.getDeadline(), afterDate) || isDateAfter(task.getEndDateInDateFormat(), afterDate) || isDateAfter(task.getStartDateInDateFormat(), afterDate));
	}

	private boolean isDateAfter(Date a, Date b) {
		return (a.getTime() >= b.getTime());
	}

	private boolean isPrioritySame(Priority priority, Task task) {
		return (priority == task.getPriority());
	}

	private boolean isPlaceSame(String place, Task task) {
		return place.equals(task.getPlace());
	}

	private boolean isDeadlineDateSame(Date deadlineDate, Task task) {
		return (deadlineDate.getTime() == task.getDeadline().getTime());
	}

	private boolean isEndDateSame(Date endDateTime, Task task) {
		return (endDateTime.getTime() == task.getEndDateInDateFormat().getTime());
	}

	private boolean isStartDateSame(Date startDateTime, Task task) {
		return (startDateTime.getTime() == task.getStartDateInDateFormat().getTime());
	}

	private boolean isNameSame(String name, Task task) {
		return name.equals(task.getName());
	}

	private void convertLongTimeToDateObject (Date d, Long secondsFromStartOfDay) {
		int hours     = secondsFromStartOfDay.intValue() / 3600;
		int minutes   = (secondsFromStartOfDay.intValue() - hours * 60*60)/60;
		int seconds   = secondsFromStartOfDay.intValue() - hours * 3600 - minutes * 60;
		d.setHours(hours);
		d.setMinutes(minutes);
		d.setSeconds(seconds);
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
	
	public void display(ArrayList<Task> a) {
		for(int i = 0; i < a.size(); i ++) {
			System.out.println((i + 1) + ". " + (a.get(i).getName()));
		}
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