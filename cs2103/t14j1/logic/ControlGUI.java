package cs2103.t14j1.logic;

import java.util.Date;

import cs2103.t14j1.logic.events.Event;
import cs2103.t14j1.logic.search.Search;
import cs2103.t14j1.logic.search.SearchEngine;
import cs2103.t14j1.logic.smartbar.ParseCommand;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;
import cs2103.t14j1.taskmeter.EventListener;
import cs2103.t14j1.taskmeter.reminder.Reminder;

public class ControlGUI {
    
    private EventListener eventHandler;
    private TaskLists     lists;         // stores a copy of all the lists
    private Commands      userCommand;   // stores the last user command
    private ParseCommand  parseCommand;  // smartBar parseCommand
    private SearchEngine  searchEngine;  // search engine
    
    /**
     * in taskMeter GUI, it will create a instance a ControlGUI and interact with it in logic part
     * GUI will pass in the lists, so Logic has a copy of the lists as well.
     * 
     * @param lists
     */
    public ControlGUI(TaskLists lists) {
        this.lists        = lists;
        this.searchEngine = new SearchEngine(lists);
    }
    
    /**
     * when taskMeter GUI accepted a new input in smartBar, it will call this method to pass
     * in the new user input to controlGUI
     * 
     * @param input
     * @return Commands
     */
    public Commands setUserInput(String input) {
        try {
            parseCommand  = new ParseCommand(input);
            userCommand   = parseCommand.extractCommand();
        } catch (Exception e) {
            userCommand   = Commands.INVALID;
        }
        
        return userCommand;
    }
    
    /**
     * after the taskMeter GUI has passed in the user input, it will call this method to get the
     * command the user just entered. The original executeCommand() method, the switch will be
     * done in GUI.
     * 
     */
    public Commands getCommand() {
        return userCommand;
    }
    
    /**
     * GUI call this method to clear last command to prevent any crashes in controlGUI
     */
    public void resetCommand() {
        userCommand = null;
        searchEngine.resetProperties();
    }
    
    /**
     * execute input command from user
     * 
     * @param input         user's input string
     */
    public void executeCommand() {
        // register event
        try {
            switch (getCommand()) {
                case ADD_TASK:
                    addTask(addTask());
                    break;
                case DELETE_TASK:
                    deleteTask(getTaskIdx());
                    break;
                case MOVE_TASK:
                    moveTask(logic.getTaskIdx(), logic.getListName());
                    break;
                case EDIT_TASK:
                    editTask(logic.getTaskIdx());
                    break;
                case ADD_REMINDER:
                    addReminder(logic.getTaskIdx(), logic.getReminderParameter());
                    break;
                case MARK_COMPLETE:
                    toggleStatus(logic.getTaskIdx());
                    break;
                case MARK_PRIORITY:
                    togglePriority(logic.getTaskIdx(), logic.getNewTaskPriority());
                    break;
                case ADD_LIST:
                    addList(getListName());
                    break;
                case EDIT_LIST:
                    editList(logic.extractNewListName(), null);
                    break;
                case RENAME_LIST:
                    editList(logic.extractNewListName(), logic.extractNewListName());
                    break;
                case DELETE_LIST:
                    deleteList(getListName());
                    break;
                case SWITCH_LIST:
                    switchList(logic.getListName());
                    break;
                case SEARCH:
                    doSearch(getSearchResult());
                    break;
                default:
                    eventHandler.setStatus(eventHandler.getMsg("msg.invalid.command"));
                    break;
            }
        } catch (Exception e) {
            eventHandler.setStatus(eventHandler.getMsg("error.logic.command"));
        }
    }
    
    
    public void deleteTask(int taskIdx) {
        Event newEvent = Event.generateEvent(Commands.DELETE_TASK);
        registerEvent(newEvent, taskIdx);
    }

    public void doSearch(TaskList searchResult) {
        Event newEvent = Event.generateEvent(Commands.SEARCH);
        registerEvent(newEvent, searchResult);
    }

    /**
     * if the user's command is ADD_TASK, GUI will call this method to perform the actual addTask
     * 
     * @return the new task if successfully added or null if failed
     */
    private Task addTask() {
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
		boolean status     = Task.INCOMPLETE;
		
		convertLongTimeToDate(startDateTime, startTime);
		convertLongTimeToDate(endDateTime, endTime);
		convertLongTimeToDate(deadline, deadlineTime);
		
		Task newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
		
		return newTask;
    }
    
    public void addTask(Task task) {
        Event newEvent = Event.generateEvent(Commands.ADD_TASK);
        registerEvent(newEvent, task);
    }
    
    public void editTask() {
    }
    
    public void registerEvent(Event e, Object... objs) {
        e.setEventLisnter(eventHandler);
        e.register(objs);
        e.execute();
    }
    
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
    
    /**
     * if the user's command is DELETE_TASK/EDIT_TASK/MARK_COMPLETE/MARK_PRIORITY
     * GUI will call this method to get the index the user entered
     * 
     * @return the index the user entered
     */
    public int getTaskIdx() {
        
        int taskNum = parseCommand.extractTaskNum();
        
        return taskNum;
    }

    /**
     * if the user's command is MARK_PRIORITY, GUI will call this method to get the new priority
     * user entered
     * 
     * @return the new priority user entered
     */
    public Priority getNewTaskPriority() {
        
    	Priority newPriority  = parseCommand.extractPriority();
        
        if((newPriority != Priority.IMPORTANT) && (newPriority != Priority.NORMAL) && (newPriority != Priority.LOW))
        	newPriority = Priority.IMPORTANT;
        
        return newPriority;
    }

    /**
     * if the user's command is ADD_LIST, GUI will call this method to perform the actual addList
     * 
     * @return the new TaskList if successfully added or null if failed
     */
    public void addList(String name) {
        Event newEvent = Event.generateEvent(Commands.ADD_LIST);
        
        newEvent.register(name);
        newEvent.execute();
    }
    
    /**
     * if the user's command is EDIT_LIST, GUI will call this method to perform the actual editList
     * 
     * @param oldListName
     * @param newListName
     * @return true is editing is successful
     * @throws Exception 
     */
    public boolean renameList(String oldListName, String newListName) throws Exception {
    	TaskList oldList = lists.getList(oldListName);
    	
    	//If no list with the name oldListname exists
    	if(oldList == null) {
    		throw new Exception(oldListName + " does not exist");
    	}
    	
    	oldList.setName(newListName);
    	
    	// remove oldList from lists because lists use treeMap
    	lists.removeList(oldListName);
    	lists.addList(oldList);
    	
    	return true;
    }
    
    /**
     * if the user's command is DELETE_LIST, GUI will call this method to perform the actual deleteList
     * 
     * @return true is deleting is successful
     */
    public void deleteList(String listName) {
        Event newEvent = Event.generateEvent(Commands.DELETE_LIST);
        registerEvent(newEvent, listName);
    }
    
    /**
     * if the user's command is ADD_LIST/DELETE_LIST/SWTICH_LIST
     * GUI will call this method to get the list name the user entered
     * 
     * @return the list name user enterd
     */
    public String getListName() {
        
    	String listName = parseCommand.extractListName();
        return listName;
    }
    
    /**
     * GUI will call this method to get the search result
     * 
     * 1. The search properties can be set through GUI search dialog
     * 2. The search properties can be set through user command SEARCH
     * 
     * @return the list of search results, empty list if nothing is found
     */
    public TaskList getSearchResult() {
        
    	assert (userCommand == Commands.SEARCH);
    	
    	setSearchProperties();
        
        TaskList searchResult = searchEngine.performSearch();
        
        return searchResult;
    }
    
    /**
     * get the user inputs from parseCommand, and set all the properties
     */
    private void setSearchProperties() {
        
    	String name         = parseCommand.extractTaskName();
		String list         = parseCommand.extractListName();
		Priority priority   = parseCommand.extractPriority();
		String place 	    = parseCommand.extractPlace();
		Long duration       = parseCommand.extractDuration();
		Boolean status      = parseCommand.extractStatus();
		Date afterDateTime  = parseCommand.extractSearchAfterDate();
		Date beforeDateTime = parseCommand.extractSearchBeforeDate();
		
		searchEngine.setProperty(Search.NAME, name);
		searchEngine.setProperty(Search.LIST, list);
		searchEngine.setProperty(Search.PRIORITY, priority);
		searchEngine.setProperty(Search.PLACE, place);
		searchEngine.setProperty(Search.DURATION, duration);
		searchEngine.setProperty(Search.STATUS, status);
		searchEngine.setProperty(Search.AFTERDATETIME, afterDateTime);
		searchEngine.setProperty(Search.BEFOREDATETIME, beforeDateTime);
    }
    
    /**
     * GUI will call this method to set the search engine, because we have normal Ctrl + F to
     * search in GUI as well.
     * 
     * @param property
     * @param value
     */
    public void setSearchProperty(Search property, Object value) {
        searchEngine.setProperty(property, value);
    }

	public String extractOldListName() {
		
		return parseCommand.extractListName();
	}

	public String extractNewListName() {
		
		return parseCommand.extractNewListName();
		
	}

    public Reminder getReminderParameter() {
        
    	Reminder parameter = parseCommand.getRemindParamter();
    	
    	return parameter;
    	
    }

    public Date getReminderTime() {
    	
    	Date reminderTime = parseCommand.getRemindTime();
    	
        return reminderTime;
    }
    
}
