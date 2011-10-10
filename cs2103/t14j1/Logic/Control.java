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
 * @author Shubham
 * 
 */
class Control {
	
	private TaskLists lists;        // stores all the lists
	private TaskList  searchResult; // stores the tasks which were short listed after user's last search query
	private String    currentListName;  // stores the current list name of the user is viewing
	private ParseCommand parseCommand;
	private static boolean shouldExit;//stores whether the user wants to exit or not so that the application can exit after save command is completed

	/**
	 * CLI just for version 0.1.
	 * It will be replaced by GUI in Version 0.2, so I just keep it short
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
	 * Constructor
	 *  This would set the INBOX as the current list, and load the task from
	 *   the storage
	 */
	public Control() {
		currentListName = TaskLists.INBOX;
		lists = new TaskLists();
		FileHandler.loadAll(lists); // call storage to load all lists and tasks from file
	}
	
	/**
	 * Process input line
	 * 
	 * @param input
	 * @return
	 * 	The response String
	 */
	public String processInput(String input) {
		try {
			parseCommand = new ParseCommand(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Commands command = Commands.INVALID;
		
		command = parseCommand.extractCommand();
		
		String feedback = executeCommand(command);
		return feedback;
	}

	/**
	 * Execute command
	 * 
	 * @param command Stores the command which is to be executed
	 * @return feedback This is the success or failure message. In some cases, it can also provide additional information.
	 */
	public String executeCommand(Commands command) {
		switch (command) {
		case ADD_TASK:
			// Nullify the search result if exist
			searchResult = null;
			return addTask();
//		case ADD_LIST:
//			return addList();
//			-- as noted in the Commands.java, you don't need to put it here
//			-- songyy
			
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
			return switchList();
		case DISPLAY_TASK:
			return displayTask();
		case EXIT:
			return saveAndExit();
		default:
			return "invalid command";
		}
	}

	
	/**
	 * Adds the task
	 * @return the feedback
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
		
		// notes on 2011-10-10 3:15:34 by Songyy:
		//	the returning of String should definitely be changed, because by 
		//	returning the String, one cannot tell if it succeeds
		String result = lists.addTask(newTask.getList(), newTask);
		
		// want to show the task details added; so assume it succeeds
		result += ("\n  Task details:\n" + newTask.getDisplayTaskStr());
		
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
				Task currentTask = taskList.getTask(i+1);
				if(containsStr(name, currentTask)
						&& isStartDateSame(startDateTime, currentTask)
						&& isEndDateSame(endDateTime, currentTask)
						&& isDeadlineDateSame(deadlineDate, currentTask)
						&& isPlaceSame(place, currentTask)
						&& isPrioritySame(priority, currentTask)
						&& isDateAfterGivenDateAndTime(afterDate, duration, currentTask)
						&& isDateBeforeGivenDateAndTime(beforeDate, duration, currentTask)
						&& isDurationSame(duration, currentTask)
						&& isListNameSame(listName, currentTask)) {
					searchResult.add(currentTask);
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
	    if(listName == null){
	    	return true;
	    }
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
		if(a == null || b == null){
			return true;
		}
		
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
			return (isDateAfter(task.getDeadline(), afterDate) || 
					isDateAfter(task.getEndDateTime(), afterDate) || 
					isDateAfter(task.getStartDateTime(), afterDate));
		}
		
		else {
			return (isDateAndTimeAfter(task.getDeadline(), afterDate) || 
					isDateAndTimeAfter(task.getEndDateTime(), afterDate) ||
					isDateAndTimeAfter(task.getStartDateTime(), afterDate));
		}
	}

	private boolean isDateAfter(Date a, Date b) {
		if(a == null || b == null){
			return true;
		}
		
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
		if(a == null || b == null){
			return true;
		}
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
		
		if(deadlineDate == null){
			return true;
		}
		
		if(deadlineDate.getYear() != task.getDeadline().getYear())
			return false;
		else if(deadlineDate.getMonth() != task.getDeadline().getMonth())
			return false;
		else if (deadlineDate.getDate() != task.getDeadline().getDate())
			return false;
		else
			return true;
	}

	private boolean isEndDateSame(Date endDateTime, Task task) {
		
		// on null, simply return true
		if(endDateTime == null){
			return true;
		}
		
		/*TODO Make date and time separate in Version 0.2*/
		
		if(endDateTime.getYear() != task.getEndDateTime().getYear())
			return false;
		else if(endDateTime.getMonth() != task.getEndDateTime().getMonth())
			return false;
		else if(endDateTime.getDate() != task.getEndDateTime().getDate())
			return false;
		else
			return true;
	}

	private boolean isStartDateSame(Date startDateTime, Task task) {
		
		// check null condition
		if(startDateTime == null){
			return true;	// when null, simply return true 
		}
		
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

	private boolean containsStr(String searchStr, Task task) {
		if(searchStr == null || searchStr.trim().compareTo("") == 0)
			return true;
		return task.getName().contains(searchStr);
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
		
		// delete when there's no search done
		if(searchResult == null){
			System.out.println("Deleting from list " + currentListName);
			
			// assumption: the currentListName is always valid
			TaskList currentList = lists.getList(currentListName);
			
			int taskNum = parseCommand.extractTaskNum();
			Task taskToDelete = currentList.getTask(taskNum);
			if(taskToDelete == null){	// out of range
				return "Invalid task number given.";
			}
			
			if(confirmWithUser("Are you sure to delete task \"" + taskToDelete.getName()  + "\"")){
				return currentList.delete(taskNum);
			} else{
				return "deletion canceled";
			}
		}
		
		int numberOfTask = parseCommand.extractTaskNum();
		Task taskToBeDeleted = searchResult.getTask(numberOfTask - 1);
		TaskList listToWhichTaskBelongs = getListToWhichTaskBelongs(taskToBeDeleted);
		int indexOfTaskInTaskList = listToWhichTaskBelongs.findIndexOfTask(taskToBeDeleted);
		String postDeletionMessage = listToWhichTaskBelongs.delete(indexOfTaskInTaskList);
		
		return postDeletionMessage;
		
	}

	private boolean confirmWithUser(String msg) {
		Scanner in = new Scanner(System.in);
		
		while(true){
			System.out.println(msg + "(y/n):");
			
			String response = in.next();
			
			if(response.charAt(0) == 'y'){
				return true;
			} else if(response.charAt(0) == 'n'){
				return false;
			} else{
				System.out.println("Invalid response. ");
			}
		}
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
	    System.out.println("Tasks in list: " + tasklist.getName());
	    
	    int index = 1;
	    
	    if(tasklist.getSize() == 0){
	    	System.out.println("Sorry, no task exists for this list.");
	    } else{
	    	for (Task t : tasklist) {
	    		System.out.print((index++) + "\t");
	    		System.out.print(t.toString());
	    		System.out.print("\n");
	    	}
	    }
	}
	
	private String displayTask() {
		int taskNum = parseCommand.extractTaskNum();
		
		if(searchResult!= null){
			Task taskToDisplay = searchResult.getTask(taskNum);
			if(taskToDisplay == null){
				return "Invalid task range in search result";
			} else{
				return taskToDisplay.getDisplayTaskStr();
			}
		} else{	// when search result is null, use the current list
			Task taskToDisplay = lists.getList(currentListName).getTask(taskNum);
			if(taskToDisplay == null){
				return "Invalid task range in current list: " + currentListName;
			} else {
				return taskToDisplay.getDisplayTaskStr();
			}
			
		}
	}
	
	/**
	 * display all the lists in application
	 */
	public void displayList() {
	    int index = 1;
	    
	    int countTasks = 0;
	    
	    for (Entry<String, TaskList> tl : lists) {
	        System.out.print((index++) + "\t");
	        System.out.print(tl.getKey());
	        System.out.print("\n");
	    }
	}
	
	
	
	/**
	 * Switch to another list
	 * Called directly by executeCommand
	 * @return
	 */
	public String switchList() {
		String listName = parseCommand.extractListName();
		
		TaskList list = lists.getList(listName);
		
		// when the list with listName doesn't exist
		if(list == null){
			return "Invalid list name given.";
		}
		
		currentListName = listName;
		
		display(list);
		return "";
	}
}