package cs2103.t14j1.logic;

import cs2103.t14j1.logic.search.Search;
import cs2103.t14j1.logic.search.SearchEngine;
import cs2103.t14j1.logic.smartbar.ParseCommand;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;

public class ControlGUI {
    
    private TaskLists    lists;         // stores a copy of all the lists
    private Commands     userCommand;   // stores the last user command
    private ParseCommand parseCommand;  // smartBar parseCommand
    private SearchEngine searchEngine;  // search engine
    
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
     */
    public void setUserInput(String input) {
        parseCommand  = new ParseCommand(input);
        userCommand   = parseCommand.extractCommand();
    }
    
    /**
     * after the taskMeter GUI has passed in the user input, it will call this method to get the
     * command the user just entered. The original executeCommand() method, the switches will be
     * done in GUI.
     * 
     */
    public Commands getCommand() {
        return userCommand;
    }
    
    /**
     * after each command is processed, GUI will call this method to clear last command to prevent
     * any crashes in controlGUI
     */
    public void resetCommand() {
        userCommand = null;
        searchEngine.resetProperties();
    }
    
    /**
     * if the user's command is ADD_TASK, GUI will call this method to perform the actual addTask
     * 
     * @return the new task if successfully added or null if failed
     */
    public Task addTask() {
        
        // TODO: code for addTask
        
        Task newTask = new Task();
        
        // This method will still perform the actual adding of newTask
        lists.addTask(newTask.getList(), newTask);
        
        // it will return the newTask to GUI, so GUI can display it to the user
        return newTask;
    }
    
    /**
     * if the user's command is DELETE_TASK/EDIT_TASK/MARK_COMPLETE/MARK_PRIORITY
     * GUI will call this method to get the index the user entered
     * 
     * @return the index the user eneted
     */
    public int getTaskIdx() {
        
        // TODO: finish the switch for task index, index starts from 1
        
        switch (userCommand) {
            case EDIT_TASK:
                return 0;
            case DELETE_TASK:
                return 0;
        }
        
        return 0;
    }

    /**
     * if the user's command is MARK_PRIORITY, GUI will call this method to get the new priority
     * user entered
     * 
     * @return the new priority user entered
     */
    public Priority getNewTaskPriority() {
        return Priority.IMPORTANT;
    }

    /**
     * if the user's command is ADD_LIST, GUI will call this method to perform the actual addList
     * 
     * @return the new TaskList if successfully added or null if failed
     */
    public TaskList addList() {
        String name = parseCommand.extractListName();
        
        lists.addList(name);
        
        return lists.getList(name);
    }
    
    /**
     * if the user's command is ADD_LIST/EDIT_LIST/DELETE_LIST/SWTICH_LIST
     * GUI will call this method to get the list name the user entered
     * 
     * @return the list name user enterd
     */
    public String getListName() {
        
        // TODO: finish the switch for list name
        
        switch (userCommand) {
            case ADD_LIST:
                return "";
            case EDIT_LIST:
                return "";
        }
        
        return "";
    }
    
    /**
     * GUI will call this method to get the search result
     * 
     * 1. The search properties can be set through GUI search
     * 2. The search properties can be set through user command SEARCH
     * 
     * @return the list of search result, empty list if nothing is found
     */
    public TaskList getSearchResult() {
        if (userCommand == Commands.SEARCH) {
            setSearchProperties();
        }
        
        TaskList searchResult = searchEngine.performSearch();
        
        return searchResult;
    }
    
    /**
     * get the user inputs from parseCommand, and set all the properties
     */
    private void setSearchProperties() {
        
        // TODO: code here for properties setting
        // TODO: finish the searchEngine class
        
        searchEngine.setProperty(Search.NAME, "Hello"); // a eg to set properties
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
    
}