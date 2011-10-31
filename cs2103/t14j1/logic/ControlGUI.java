package cs2103.t14j1.logic;

import java.util.Date;

import cs2103.t14j1.logic.events.Event;
import cs2103.t14j1.logic.search.Search;
import cs2103.t14j1.logic.search.SearchEngine;
import cs2103.t14j1.logic.smartbar.ParseCommand;
import cs2103.t14j1.logic.undo.UndoManager;
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
    private UndoManager   undoManager;   // UndoManager
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
        this.undoManager  = new UndoManager();
        this.searchEngine = new SearchEngine(lists);
        this.eventHandler = null;
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
                    moveTask(getTaskIdx(), getListName());
                    break;
                case EDIT_TASK:
                    editTask(getTaskIdx());
                    break;
                case ADD_REMINDER:
                    addReminder(getTaskIdx(), getReminderParameter());
                    break;
                case REMOVE_REMINDER:
                    removeReminder(getTaskIdx());
                    break;
                case MARK_COMPLETE:
                    toggleStatus(getTaskIdx(), Task.COMPLETED);
                    break;
                case MARK_PRIORITY:
                    togglePriority(getTaskIdx(), getNewTaskPriority());
                    break;
                case ADD_LIST:
                    addList(getListName());
                    break;
                case EDIT_LIST:
                    editList(extractOldListName(), null);
                    break;
                case RENAME_LIST:
                    editList(extractOldListName(), extractNewListName());
                    break;
                case DELETE_LIST:
                    deleteList(getListName());
                    break;
                case SWITCH_LIST:
                    eventHandler.switchToList(getListName());
                    break;
                case SEARCH:
                    doSearch(getSearchResult());
                    break;
                case UNDO:
                    undo();
                    break;
                case REDO:
                    redo();
                    break;
                default:
                    eventHandler.setStatus(eventHandler.getMsg("msg.invalid.command"));
                    break;
            }
        } catch (Exception e) {
            eventHandler.setStatus(eventHandler.getMsg("error.logic.command"));
        }
    }
    
    
    public void moveTask(int index, String listName) {
        Event newEvent = Event.generateEvent(Commands.MOVE_TASK);
        registerEvent(newEvent, index, listName);
    }

    public void addReminder(int index, Reminder parameter) {
        Task task = eventHandler.getTask(index);
        
        Date remindTime = null;

        switch (parameter) {
            case START:
                remindTime = task.getStartDateTime();
                break;
            case END:
                remindTime = task.getEndDateTime();
                break;
            case DEADLINE:
                remindTime = task.getDeadline();
                break;
            case CUSTOM:
                remindTime = getReminderTime();
                break;
        }
        
        Event newEvent = Event.generateEvent(Commands.ADD_REMINDER);
        registerEvent(newEvent, task, remindTime);
    }

    public void removeReminder(int index) {
        Task task = eventHandler.getTask(index);
        Event newEvent = Event.generateEvent(Commands.REMOVE_REMINDER);
        registerEvent(newEvent, task);
    }

    public void toggleStatus(int index, Boolean newStatus) {
        Event newEvent = Event.generateEvent(Commands.MARK_COMPLETE);
        registerEvent(newEvent, index, newStatus);
    }

    public void togglePriority(int index, Priority newPriority) {
        Event newEvent = Event.generateEvent(Commands.MARK_PRIORITY);
        registerEvent(newEvent, index, newPriority);
    }

    public void editList(String oldlist, String newlist) {
        Event newEvent = Event.generateEvent(Commands.EDIT_LIST);
        registerEvent(newEvent, oldlist, newlist);
    }

    public void deleteTask(int taskIdx) {
        Event newEvent = Event.generateEvent(Commands.DELETE_TASK);
        registerEvent(newEvent, taskIdx);
    }

    public void doSearch(TaskList searchResult) {
        Event newEvent = Event.generateEvent(Commands.SEARCH);
        registerEvent(newEvent, searchResult);
    }
    
    public void clearTrash() { // not undoable, not a individual event
        lists.getList(TaskLists.TRASH).clear();
        eventHandler.setStatus(eventHandler.getMsg("msg.CLEAR_TRASH"));
    }
    
    public void clearCompleted(TaskList list) {
        TaskList remove = new TaskList("Completed");
        
        // look through all completed tasks
        for (Task t : list) {
            if (t.isCompleted())
                remove.addTask(t);
        }
        // move all completed tasks to Trash
        for (Task t : remove) {
            lists.moveTask(TaskLists.TRASH, t);
        }
        
        eventHandler.setStatus(String.format(eventHandler.getMsg("msg.CLEAR_COMPLETED"), remove.getSize(), list.getName()));
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
    
    public Task quickAddTask() {
        Task task = addTask();
        
        if (addTask(task)) {
            return task;
        } else {
            return null;
        }
    }
    
    public boolean addTask(Task task) {
        Event newEvent = Event.generateEvent(Commands.ADD_TASK);
        return registerEvent(newEvent, task);
    }
    
    public void editTask(int index) {
        Event newEvent = Event.generateEvent(Commands.EDIT_TASK);
        registerEvent(newEvent, index);
    }
    
    public boolean registerEvent(Event e, Object... objs) {
        assert(e != null);
        
        e.setEventLisnter(eventHandler);
        e.register(objs);
        
        boolean success = e.execute();
        
        if (success && e.hasUndo()) {
            undoManager.addUndo(e);
        }
        
        return success;
    }
    
    private void convertLongTimeToDate (Date d, Long secondsFromStartOfDay) {
		if(d == null) {
			return;
		} else if(secondsFromStartOfDay == null) {
			/*A value of null indicates that the user did not specify any time and
			so, we assume the time to be 00:00:00*/
			d.setHours(0);
			d.setMinutes(0);
			d.setSeconds(0);
		} else {
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
        registerEvent(newEvent, name);
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
		//Boolean status      = parseCommand.extractStatus();
		Date afterDateTime  = parseCommand.extractSearchAfterDate();
		Date beforeDateTime = parseCommand.extractSearchBeforeDate();
		
		searchEngine.setProperty(Search.NAME, name);
		searchEngine.setProperty(Search.LIST, list);
		searchEngine.setProperty(Search.PRIORITY, priority);
		searchEngine.setProperty(Search.PLACE, place);
		searchEngine.setProperty(Search.DURATION, duration);
		//searchEngine.setProperty(Search.STATUS, status);
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
    
    public boolean hasUndo() {
        return undoManager.hasUndo();
    }
    
    public boolean hasRedo() {
        return undoManager.hasRedo();
    }
    
    public void undo() {
        assert(undoManager.hasUndo());
        
        Event lastEvent = undoManager.getUndo();
        Event redo = lastEvent.undo();
        
        if (redo != null) {
            undoManager.addRedo(redo);
        }
    }
    
    public void redo() {
        assert(undoManager.hasRedo());
        
        Event lastEvent = undoManager.getRedo();
        Event undo = lastEvent.undo();
        
        if (undo != null) {
            undoManager.addUndo(undo);
        }
    }
    
    public void setEventListener(EventListener e) {
        eventHandler = e;
    }
    
}