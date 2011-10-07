package cs2103.t14j1.logic;

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
            // TODO Auto-generated catch block
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
		
		convertLongTimeToDate(startDateTime, startTime);
		convertLongTimeToDate(endDateTime, endTime);
		convertLongTimeToDate(deadline, deadlineTime);
		boolean status     = Task.INCOMPLETE;
		
		Task newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
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
	//
	// Zhuochun's Note
	//
	// 0. Use TaskList searchResult is simpler than ArrayList<Task> (which is the same TaskList, and that is why
	//    we create TaskList class).
	//
	// 1. if any of the name/startDateTime properties is not set in search input, comparison is going to fail
	//    TODO: so you need to change the is...() functions to return true if any of the properties is null
	// 
	// 2. TODO: if the listName is set, you only need to search within that list.
	//    btw, the parseCommand.extractList return "INBOX" when the search input has not specify which list.
	//    
	// 3. TODO: you need to decide whether by default (when the user does not specified a list), you search
	//    only the list the user is now viewing or all the lists
	// 
	// 4. TODO: for name and space, you can use regular expression to match the terms.
	//
	// 5. TODO: all those is..() functions should put into a separate search class in logic. And, I think function
	//    names like isDateBeforeGivenDateAndTime() is too long. 
	//
	private String search(String input) {
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
						&& isDateAfterGivenDateAndTime(afterDate, taskList.getTask(i))
						&& isDateBeforeGivenDateAndTime(beforeDate, taskList.getTask(i))
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
	    if (listName == null)
	        return true;
	    
		return (listName.equals(task.getList()));
	}

	private boolean isDurationSame(Long duration, Task task) {
		return (duration == task.getDuration());
	}

	private boolean isDateBeforeGivenDateAndTime(Date beforeDate, Task task) {
		return (isDateBefore(task.getDeadline(), beforeDate) || isDateBefore(task.getEndDateTime(), beforeDate) || isDateBefore(task.getStartDateTime(), beforeDate));
	}
	
	private boolean isDateBefore(Date a, Date b) {
		return (a.getTime() <= b.getTime()); 
	}

	private boolean isDateAfterGivenDateAndTime(Date afterDate, Task task) {
		return (isDateAfter(task.getDeadline(), afterDate) || isDateAfter(task.getEndDateTime(), afterDate) || isDateAfter(task.getStartDateTime(), afterDate));
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
		return (endDateTime.getTime() == task.getEndDateTime().getTime());
	}

	private boolean isStartDateSame(Date startDateTime, Task task) {
		return (startDateTime.getTime() == task.getStartDateTime().getTime());
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
	
	public void switchList (String input) {
		String listName = parseCommand.extractListName();
		
		TaskList list = lists.getList(listName);
		
		currentList = listName;
		
		display(list);
	}

}