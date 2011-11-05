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
import cs2103.t14j1.storage.When;

public class SearchEngine {
    
    private TaskLists lists; // stores a copy of the lists
    
    // Stores the properties that should be matched
    private String   name;              // define the task action
    private String   place;             // define the place of task
    private String   list;              // belong to which list
    private Priority priority;          // priority of the task
    private Long     duration;          // duration of task
    private Boolean  status;            // completed or not
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
    	
    	if(!lists.hasList(list)) {
    		Iterator<Entry<String, TaskList>> iterator = lists.iterator();
        	while (iterator.hasNext()) {
        		TaskList taskList = iterator.next().getValue();
        		searchLists.add(taskList);
        	}
        }   	
    	else
        	searchLists.add(lists.getList(list));
    	
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
					&& isPlaceSame(task)
					&& isPrioritySame(task)
					&& isDateAfterAndBeforeGivenDatesAndTimes(task)
					&& isDurationSame(task)
					&& isStatusSame(task)) {
				searchResults.add(task);
    		}
    	}
    	return searchResults;
    }

	private boolean isStatusSame(Task task) {
		if(status == null)
			return true;
		else
			return (status == task.getStatus());
	}

	private boolean isDurationSame(Task task) {
		When when = task.getWhen();
		if(duration == null)
			return true;
		else if(when.getDuration() == null)
			return false;
		else if(duration == when.getDuration())
			return true;
		else return false;
	}

	private boolean isDateAfterAndBeforeGivenDatesAndTimes(Task task) {
		When when = task.getWhen();
		if((afterDateTime == null) && (beforeDateTime == null))
			return true;
		else if(afterDateTime == null) {
			if((when.getStartDateTime() == null) && (when.getDeadline() == null))
				return false;
			else if((when.getDeadline() == null) && (when.getEndDateTime().getTime() <= beforeDateTime.getTime()))
				return true;
			else if((when.getEndDateTime() == null) && (when.getDeadline().getTime() <= beforeDateTime.getTime()))
				return true;
			else
				return false;
		}
		else if(beforeDateTime == null) {
			if((when.getStartDateTime() == null) && (when.getDeadline() == null))
				return false;
			else if((when.getDeadline() == null) && (when.getStartDateTime().getTime() >= afterDateTime.getTime()))
				return true;
			else if((when.getStartDateTime() == null) && (when.getDeadline().getTime() >= afterDateTime.getTime()))
				return true;
			else
				return false;
		}
		else {
			if((when.getStartDateTime() == null) && (when.getDeadline() == null))
				return false;
			else if((when.getDeadline() == null) && ((when.getStartDateTime().getTime() >= afterDateTime.getTime()) || (when.getEndDateTime().getTime() <= beforeDateTime.getTime())))
				return true;
			else if((when.getEndDateTime() == null) && ((when.getDeadline().getTime() >= afterDateTime.getTime()) || (when.getDeadline().getTime() <= beforeDateTime.getTime())))
				return true;
			else
				return false;
		}
	}

	private boolean isPrioritySame(Task task) {
		if(priority == null)
			return true;
		return (priority == task.getPriority());
	}

	private boolean isPlaceSame(Task task) {
		
		if(place == null)
			return true;
		
		String trimmedPlace = place.trim();
		
		if(trimmedPlace.compareTo("") == 0)
			return true;
		
		String taskPlaceLowerCase = task.getPlace().toLowerCase();
		String trimmedPlaceLowerCase = trimmedPlace.toLowerCase();
		
		StringTokenizer st = new StringTokenizer(trimmedPlaceLowerCase);
		while(st.hasMoreTokens()) {
			String word = st.nextToken();
			if(taskPlaceLowerCase.contains(word))
				continue;
			if((task.getPlace() != null) && (task.getPlace().toLowerCase().contains(word)))
				continue;
			return false;
		}
		
		return true;
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