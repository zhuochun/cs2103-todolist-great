package cs2103.t14j1.logic.search;

import java.util.Date;

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
        TaskList searchResult = new TaskList("Search Result");
        
        // TODO: search through the lists according to the properties set
        
        return searchResult;
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