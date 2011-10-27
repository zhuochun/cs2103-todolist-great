package cs2103.t14j1.logic.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ibm.icu.util.StringTokenizer;

import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;

public class SearchEngine {
    
    private TaskLists lists; // stores a copy of the lists
    
    // Stores the properties that should be matched
    private String   name;              // define the task action
    private String   place;             // define the place of task
    private String   list;              // belong to which list
    private Priority priority;          // priority of the task
    private Date     startDateTime;     // start date and time
    private Date     endDateTime;       // end date and time
    private Date     deadline;          // deadline date and time
    private Long     duration;          // duration of task
    private boolean  status;            // completed or not
    private Date afterDateTime;			// If afterDateTime is specified, only tasks having date and time after afterDateTime will be included in the search results
    private Date beforeDateTime;		// If beforeDateTime is specified, only tasks having date and time before beforeDateTime will be included in the search results
    
    /**
     * ControlGUI will initial searchEngine and pass in a copy of the lists
     * 
     * @param lists
     */
    public SearchEngine(TaskLists lists) {
        this.lists = lists;
        resetProperties();
    }
    
    /**
     * reset all the properties to null conditions
     */
    public void resetProperties() {
        name          = null;
        place         = null;
        list          = null;
        priority      = null;
        startDateTime = null;
        endDateTime   = null;
        deadline      = null;
        duration      = null;
        status        = Task.INCOMPLETE;
    }
    
    /**
     * perform the search according to the properties that have been set
     * 
     * @return the search result as a TaskList object. If nothing is found, the TaskList
     * contains 0 tasks.
     */
    public TaskList performSearch() {
        
        ArrayList<TaskList> searchLists = selectListsToBeSearched();
        
        TaskList searchResult = searchSelectedTaskLists(searchLists);
        	
        return searchResult;
    }
    
    /**
     * Depending on the value after the hash-tag in the user input, creates a list of TaskLists which need to searched
     * @return the list of TaskLists
     */
    private ArrayList<TaskList> selectListsToBeSearched() {
    	
    	ArrayList<TaskList> searchLists = new ArrayList<TaskList>();
    	
    	if((list != null) && (!lists.hasList(list))) {
        }   	
    	else if(list != null)
        	searchLists.add(lists.getList(list));
        else {
        	Iterator<Entry<String, TaskList>> iterator = lists.iterator();
        	while (iterator.hasNext()) {
        		TaskList taskList = iterator.next().getValue();
        		searchLists.add(taskList);
        	}
        }
    	
    	return searchLists;
	}

	/**
	 * Performs the search in the TaskLists passed to it
	 * @param searchLists the list of TaskLists which need to searched
	 * @return the search result in the form of a list of tasks which meet the search criteria 
	 */
	private TaskList searchSelectedTaskLists(ArrayList<TaskList> searchLists) {
		
    	TaskList searchResult = new TaskList("Search Result");
    	
    	for(int i = 0; i < searchLists.size(); i ++) {
    		ArrayList<Task> searchResultsInThisList = searchList(searchLists.get(i));
    		for(int j = 0; j < searchResultsInThisList.size(); j ++) {
    			searchResult.addTask(searchResultsInThisList.get(j));
    		}
    	}
    	
    	return searchResult;
    	
	}

	/**
     * Searches for tasks in a particular list
     * @param taskList the taskList which is to be searched
     * @return a list of all tasks which match the search criteria
     */
    private ArrayList<Task> searchList(TaskList taskList) {
    	
    	ArrayList<Task> searchResults = new ArrayList<Task>();
    	
    	Iterator<Task> iter = taskList.iterator();
    	
    	while(iter.hasNext()) {
    		Task task = iter.next();
    		if(doesTaskNameContainSearchString(task)
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

	private boolean doesTaskNameContainSearchString(Task task) {
		
		if(name == null)
			return true;
		
		String trimmedName = name.trim();
		
		if(trimmedName.compareTo("") == 0)
			return true;
		
		String taskNameLowerCase = task.getName().toLowerCase();
		String trimmedNameLowerCase = trimmedName.toLowerCase();
		
		StringTokenizer st = new StringTokenizer(trimmedNameLowerCase);
				
		while(st.hasMoreTokens()) {
			String word = st.nextToken();
			if(taskNameLowerCase.contains(word))
				continue;
			if((task.getPlace() != null) && (task.getPlace().toLowerCase().contains(word)))
				continue;
			return false;
		}
		return true;
	}

	/**
     * able to set an individual property of the searchEngine
     * 
     * @param property
     * @param value
     */
    public void setProperty(Search property, Object value) {
        
        switch (property) {
            case NAME:
                this.name = (String) value;
                break;
            case PLACE:
            	this.place = (String)value;
            	break;
            case LIST:
            	this.list = (String)value;
            	break;
            case PRIORITY:
            	this.priority = (Priority)value;
            	break;
            case STARTDATETIME:
            	this.startDateTime = (Date)value;
            	break;
            case ENDDATETIME:
            	this.endDateTime = (Date)value;
            	break;
            case DEADLINE:
            	this.deadline = (Date)value;
            	break;
            case DURATION:
            	this.duration = (Long)value;
            	break;
            case STATUS:
            	this.status = (Boolean)value;
            	break;
            case AFTERDATETIME:
            	this.afterDateTime = (Date)value;
            	break;
            case BEFOREDATETIME:
            	this.beforeDateTime = (Date)value;
            	break;
        }
        
    }

}