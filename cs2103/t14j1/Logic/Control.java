package cs2103.t14j1.logic;

import java.sql.Time;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

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
	
	TaskLists lists;        // stores all the lists
	TaskList  searchResult; // stores the last search result
	String    currentList;  // stores the current list name of the user is viewing
	ParseCommand parseCommand;
	private static boolean shouldExit;

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
			
			checkForExit();
		}
	}
	
	/**
	 * Check if the user wants the program to exit, and exit if he wants it to
	 */
	private static void checkForExit() {
		if(shouldExit)
			System.exit(0);
	}

	/**
	 * constructor
	 */
	public Control() {
		currentList = TaskLists.INBOX;
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
            e.printStackTrace();
        }
		
		Commands command = Commands.INVALID;
		
		// Zhuochun's Note
		// used to display lists and tasks in command line
		if (input.equals("ll")) { // ll = list all lists
		    displayList();
		} else if (input.equals("lt")) { // lt = list all tasks in current list
		    display(lists.getList(currentList));
		} else {
		    command = parseCommand.extractCommand();
		}
		
		String feedback = executeCommand(command, input);
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
			return addTask();	// 100% implemented
		case ADD_LIST:
			return addList();	// 100%
		case SEARCH:
			return search();
		case DELETE_TASK:
			return deleteTask();
		case DELETE_LIST:
			return deleteList();
		case EDIT_TASK:
			return editTask();
		case EDIT_LIST:
			return editList();
		case SORT:
			return sortBy();
		case SWITCH_LIST:
			switchList();
		case EXIT:
			return saveAndExit();
		default:
			return "invalid command";
		}
	}

	/**
	 * add task into TaskLists
	 * 
	 * Shubham: Have to add functionality for deadline
	 * 
	 * @return
	 */
	private String addTask() {
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
		
		convertLongTimeToDate(startDateTime, startTime);
		convertLongTimeToDate(endDateTime, endTime);
		convertLongTimeToDate(deadline, deadlineTime);
		boolean status     = Task.INCOMPLETE;
		
		Task newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
		String result = lists.addTask(list, newTask);
		
		return result;
	}

	private String addList() {
		String name = parseCommand.extractListName();
		
		lists.add(name);
		
		String LIST_SUCCESSFULLY_ADDED = "List " + name + " has been successfully created and added";
		
		return LIST_SUCCESSFULLY_ADDED;
	}

	//not yet complete
	//
	// Zhuochun's Note
	//
	// 0. Use TaskList searchResult is simpler than ArrayList<Task> (which is the same TaskList, and that is why
	//    we create TaskList class).
	//
	// 1. if any of the name/startDateTime properties is not set in search input, comparison is going to fail
	//     so you need to change the is...() functions to return true if any of the properties is null
	// 
	// 2. TODO: if the listName is set, you only need to search within that list.
	//    btw, the parseCommand.extractList return "INBOX" when the search input has not specify which list.
	//Noted, will be done in V0.2
	//    
	// 3.  you need to decide whether by default (when the user does not specified a list), you search
	//    only the list the user is now viewing or all the lists
	//Decided, all the lists
	// 
	// 4.  for name and space, you can use regular expression to match the terms.
	//Why, isn't String matching good enough?
	//
	// 5. TODO: all those is..() functions should put into a separate search class in logic. And, I think function
	//    names like isDateBeforeGivenDateAndTime() is too long. 
	// Okay, would put into a separate Search file in Logic in V0.2
	//
	private String search() {
	    searchResult = new TaskList("search result");
		
		String name = parseCommand.extractTaskName();
		Date startDateTime = parseCommand.extractStartDate();
		
		Date endDateTime = parseCommand.extractEndDate();
		Long startTime = parseCommand.extractStartTime();
		Long endTime = parseCommand.extractEndTime();
		String place = parseCommand.extractPlace();
		Priority priority = parseCommand.extractPriority();
		convertLongTimeToDate(startDateTime, startTime);
		convertLongTimeToDate(endDateTime, endTime);
		Date afterDate = parseCommand.extractSearchAfterDate();
		Long afterTime = parseCommand.extractSearchAfterTime();
		convertLongTimeToDate(afterDate, afterTime);
		Date beforeDate = parseCommand.extractSearchBeforeDate();
		Long beforeTime = parseCommand.extractSearchBeforeTime();
		convertLongTimeToDate(beforeDate, beforeTime);
		Date deadlineDate = parseCommand.extractDeadlineDate();
		Long deadlineTime = parseCommand.extractDeadlineTime();
		convertLongTimeToDate(deadlineDate, deadlineTime);
		Long duration = parseCommand.extractDuration();
		String listName = parseCommand.extractListName();
		
		Iterator<Entry<String, TaskList>> iterator = lists.iterator();
		while(iterator.hasNext()) {
			TaskList taskList = iterator.next().getValue();
			
			for(int i = 0; i < taskList.getSize(); i ++) {
				if(isNameSame(name, taskList.getTask(i))
						&& isStartDateSame(startDateTime, taskList.getTask(i))
						&& isEndDateSame(endDateTime, taskList.getTask(i))
						&& isDeadlineDateSame(deadlineDate, taskList.getTask(i))
						&& isPlaceSame(place, taskList.getTask(i))
						&& isPrioritySame(priority, taskList.getTask(i))
						&& isDateAfterGivenDateAndTime(afterDate, duration, taskList.getTask(i))
						&& isDateBeforeGivenDateAndTime(beforeDate, duration, taskList.getTask(i))
						&& isDurationSame(duration, taskList.getTask(i))
						&& isListNameSame(listName, taskList.getTask(i))) {
					searchResult.add(taskList.getTask(i));
				}
			}
			
		}
		
		display(searchResult);
		
		String SEARCH_RESULT;
		
		if (searchResult.isEmpty())
			SEARCH_RESULT = "No match found";
		else
			SEARCH_RESULT = "Search successful.";
		
		return SEARCH_RESULT;
	}
	
	private boolean isListNameSame(String listName, Task task) {
	    
		return (listName.equals(task.getList()));
	}

	private boolean isDurationSame(Long duration, Task task) {
		
		if(duration == null)
			return true;
		
		if(task.getDuration() == null)
			return false;
		
		return (duration == task.getDuration());
	}

	private boolean isDateBeforeGivenDateAndTime(Date beforeDate, Long duration, Task task) {
		
		if((task.getDuration() == null) || (duration == null)) {
			//This means that the user didn't specify the end time for this task and so we can just rely on the end date
			return (isDateBefore(task.getDeadline(), beforeDate) || isDateBefore(task.getEndDateTime(), beforeDate) || isDateBefore(task.getStartDateTime(), beforeDate));
		}
		
		else {
			return (isDateAndTimeBefore(task.getDeadline(), beforeDate) || isDateAndTimeBefore(task.getEndDateTime(), beforeDate) || isDateAndTimeBefore(task.getStartDateTime(), beforeDate));
		}
	}
	
	private boolean isDateBefore(Date a, Date b) {
		if (a.getYear() < b.getYear())
			return true;
		else if (a.getYear() > b.getYear())
			return false;
		else {
			if(a.getMonth() < b.getMonth())
				return true;
			else if(a.getMonth() > b.getMonth())
				return false;
			else {
				if(a.getDate() <= b.getDate())
					return true;
				else
					return false;
			}		
		}
	}
	
	private boolean isDateAndTimeBefore(Date a, Date b) {
		return (a.getTime() <= b.getTime());
	}

	private boolean isDateAfterGivenDateAndTime(Date afterDate, Long duration, Task task) {
		
		if((task.getDuration() == null) || (duration == null)) {
			return (isDateAfter(task.getDeadline(), afterDate) || isDateAfter(task.getEndDateTime(), afterDate) || isDateAfter(task.getStartDateTime(), afterDate));
		}
		
		else {
			return (isDateAndTimeAfter(task.getDeadline(), afterDate) || isDateAndTimeAfter(task.getEndDateTime(), afterDate) || isDateAndTimeAfter(task.getStartDateTime(), afterDate));
		}
	}

	private boolean isDateAfter(Date a, Date b) {
		if (a.getYear() > b.getYear())
			return true;
		else if (a.getYear() < b.getYear())
			return false;
		else {
			if(a.getMonth() > b.getMonth())
				return true;
			else if(a.getMonth() < b.getMonth())
				return false;
			else {
				if(a.getDate() >= b.getDate())
					return true;
				else
					return false;
			}		
		}
	}

	private boolean isDateAndTimeAfter(Date a, Date b) {
		return (a.getTime() >= b.getTime());
	}

	private boolean isPrioritySame(Priority priority, Task task) {
		
		if(priority == null)
			return true;
		
		else if (task.getPriority() == null)
			return false;
		
		else
			return (priority == task.getPriority());
	}

	private boolean isPlaceSame(String place, Task task) {
		
		if(place == null)
			return true;
		
		else if(task.getPlace() == null)
			return false;
			
		else
			return place.equals(task.getPlace());
	}

	private boolean isDeadlineDateSame(Date deadlineDate, Task task) {
		/*TODO For now, this only compares the dates without paying attention to
		time. This is because we set the default time to 0 if no time is mentioned.
		Need to change this in Version 0.2*/
		if(deadlineDate.getYear() != task.getDeadline().getYear())
			return false;
		else {
			if(deadlineDate.getMonth() != task.getDeadline().getMonth())
				return false;
			else {
				if (deadlineDate.getDate() != task.getDeadline().getDate())
					return false;
				else
					return true;
			}
		}
	}

	private boolean isEndDateSame(Date endDateTime, Task task) {
		/*TODO Make date and time separate in Version 0.2*/
		
		if(endDateTime.getYear() != task.getEndDateTime().getYear())
			return false;
		else {
			if(endDateTime.getMonth() != task.getEndDateTime().getMonth())
				return false;
			else {
				if (endDateTime.getDate() != task.getEndDateTime().getDate())
					return false;
				else
					return true;
			}
		}
	}

	private boolean isStartDateSame(Date startDateTime, Task task) {
		
		/*TODO Make date and time separate in Version 0.2*/
		
		if(startDateTime.getYear() != task.getStartDateTime().getYear())
			return false;
		else {
			if(startDateTime.getMonth() != task.getStartDateTime().getMonth())
				return false;
			else {
				if (startDateTime.getDate() != task.getStartDateTime().getDate())
					return false;
				else
					return true;
			}
		}
		
	}

	private boolean isNameSame(String name, Task task) {
		return name.equals(task.getName());
	}

	// Zhuochun's Note
	//
	// As I have told you, if the secondsFromStartOfDay is null (a condition you didn't take care of here)
	// you can set the hour, minute, seconds to 0.
	//
	private void convertLongTimeToDate (Date d, Long secondsFromStartOfDay) {
		
		if(d == null)
			return;
		
		else if(secondsFromStartOfDay == null) {
			/*A value of null indicates that the user did not specify any time and
			so, we assume the time to be 00:00:00*/
			
			d.setHours(0);
			d.setMinutes(0);
			d.setSeconds(0);
		}
		
		else {
			int hours     = secondsFromStartOfDay.intValue() / 3600;
			int minutes   = (secondsFromStartOfDay.intValue() - hours * 60*60)/60;
			int seconds   = secondsFromStartOfDay.intValue() - hours * 3600 - minutes * 60;
			d.setHours(hours);
			d.setMinutes(minutes);
			d.setSeconds(seconds);
		}
	}

	private String deleteTask() {
		
		int numberOfTask = parseCommand.extractTaskNum();
		Task taskToBeDeleted = searchResult.getTask(numberOfTask - 1);
		TaskList listToWhichTaskBelongs = getListToWhichTaskBelongs(taskToBeDeleted);
		int indexOfTaskInTaskList = listToWhichTaskBelongs.findIndexOfTask(taskToBeDeleted);
		String postDeletionMessage = listToWhichTaskBelongs.delete(indexOfTaskInTaskList);
		
		return postDeletionMessage;
		
	}

	private TaskList getListToWhichTaskBelongs(Task task) {
		
		String listName = task.getList();
		
		TaskList list = lists.getList(listName);
		
		return list;
		
	}

	private String deleteList() {
		
		String listName = parseCommand.extractListName();
		
		String postDeletionMessage = lists.remove(listName);
		
		return postDeletionMessage;
	}

	private String editTask() {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String editList() {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String sortBy() {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	public String saveAndExit() {
		FileHandler.saveAll(lists);
		
		String SAVE_COMPLETED = "Saved";
		
		shouldExit = true;
		
		return SAVE_COMPLETED;
	}
	
	/**
	 * display all task in a list
	 * 
	 * @param tasklist
	 */
	public void display(TaskList tasklist) {
	    System.out.println(tasklist.getName());
	    
	    int index = 1;
	    for (Task t : tasklist) {
	        System.out.print((index++) + "\t");
	        System.out.print(t.toString());
	        System.out.print("\n");
	    }
	}
	
	/**
	 * display all the lists in application
	 */
	public void displayList() {
	    int index = 1;
	    for (Entry<String, TaskList> tl : lists) {
	        System.out.print((index++) + "\t");
	        System.out.print(tl.getKey());
	        System.out.print("\n");
	    }
	}
	
	public void switchList () {
		String listName = parseCommand.extractListName();
		
		TaskList list = lists.getList(listName);
		
		currentList = listName;
		
		display(list);
	}

}