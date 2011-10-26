package cs2103.t14j1.storage.gCal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskLists;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.calendar.ColorProperty;
import com.google.gdata.data.calendar.HiddenProperty;
import com.google.gdata.data.calendar.SelectedProperty;
import com.google.gdata.data.extensions.EntryLink;
import com.google.gdata.data.extensions.Recurrence;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.google.gdata.data.extensions.RecurrenceException;
/**
 * 1. getTaskList method would retrieve all the tasks owned by this user;
 * 	  the tasks are added to the corresponding list
 * 
 * When first call this method, call getList to see all the list user has, and 
 * 	decide which list the user want to sync with 
 * 
 * Then pass the list(returned by getList) to getTaskFromList, and sync
 * 
 * 2. When updating a list:
 * 		a) retrieve all lists, build a Hash Table of: listId => listEntry
 * 			This would be done one time only, at the time when it's first executed
 * 			(Assumption: during the run-time of this program, the user would not
 * 				edit his own google calendar much. "much" here means, delete a 
 * 				whole list)
 * 		b) get the correspondingly list, sync
 * 
 * 3. When it's time to update tasks based on a list, consider the following:
 * 		a) retrieve all the tasks belongs to that list, build a Hash Table of 
 * 			TaskId => TaskEntry
 * 		b) loop through for all the task, sync
 * 
 * 4. Note that this the sync would only track whether one task is in a certain
 * 		calendar(or list) or not; so if a task is moved from one list to another,
 * 		the program would delete one first, and then add one in the other 
 * **************************************
 * On Sync:
 * 		-- if cannot get the task, then delete it on local
 * 				(meaning, this task has been deleted on server)
 * 		-- if the task don't have a taskId, then create task on server, and 
 * 				mark the task id 
 * 		-- else if the liver version is newer, then update the local one; 
 * 		-- else if the local is newer, update the live one
 * 		-- else do nothing
 * 
 * 
 * @author songyy This class is used for synchronizing with google calendar
 */
public class GCalSyn {
	private String username;
	private String passwd;
	CalendarService myService;

	HashMap<String,CalendarEntry> listIdToEntry;
	TaskList gCalList[];
	
	private URL feedUrl;
	private CalendarFeed list;

	// some constants used
	private static final String calFeedUrlStr = "https://www.google.com/calendar/feeds/default/owncalendars/full";
	private static final String privateUrlStrFormat = "https://www.google.com/calendar/feeds/%s/private/full";
	
	private static final String defaultColour = "#2952A3";
	
	// global constants used by other class
	/**
	 * Not sync with google calendar yet -- no record in the Google Calendar
	 */
	public static final int NOT_SYN= 0;
	
	/**
	 * There's record in Google Calendar, but the local version has changed
	 */
	public static final int TO_SYN = 1;
	
	/**
	 * The local version is the same as last time's syn
	 * But it's possible that the user edited the record in Google Calendar, 
	 *  and the changes would not be reflected here because this local version
	 *  is possibly not updated
	 */
	public static final int UPDATED = 2;
	
	
	
	/**
	 * When calling the getId() method of calendar class, a full URL got;
	 * However, only the id part (the end of the URL) is useful information
	 * 
	 * This method is created for getting the pure id (last part) 
	 * @param url
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private String getPurCalendarIdFromUrl(String url) throws UnsupportedEncodingException{
		String calendarIdURL = URLDecoder.decode(url, "utf-8");
		return calendarIdURL.substring(calendarIdURL.lastIndexOf('/') + 1);
	}
	
	
	/**
	 * @param email
	 *            the email address of the user
	 * @param password
	 *            the password of the user
	 * @throws ServiceException
	 */
	public GCalSyn(String email, String password) throws IOException,
			ServiceException {
		this.username = email;
		this.passwd = password;

		// create the URL
		feedUrl = new URL(calFeedUrlStr);

		// try logging in to the GCal
		myService = new CalendarService("Task-Meter");
		myService.setUserCredentials(this.username, this.passwd);

		// then get the list feed
		this.list = myService.getFeed(feedUrl, CalendarFeed.class);
		
		// build the calListEntry
		this.listIdToEntry = new HashMap<String,CalendarEntry>();
		this.gCalList = new TaskList[list.getEntries().size()];
		int index = 0;
		for(CalendarEntry calList:this.list.getEntries()){
			this.listIdToEntry.put(calList.getId(), calList);
			
			// create a new list on that
			TaskList taskList = new TaskList(calList.getTitle().getPlainText());
			taskList.setGCalId(getPurCalendarIdFromUrl(calList.getId()));
			
			this.gCalList[index++] = taskList;
		}
		
	}

	/**
	 * @return An Array of all the task list this user has
	 * @throws UnsupportedEncodingException
	 *             When program fail -- the logic would tell the user that very
	 *             sorry we cannot process your darling requirement
	 */
	public TaskList[] getCalLists(){
		return this.gCalList;
	}
	
	/**
	 * TODO: figure out how to get the place from CalendarEventEntry
	 * Note: this would only sync with the tasks has a start time; if there's no
	 * 		start date/time, not sync
	 * TODO: for the no start date/time ones, would sync with Task Api in the 
	 * 		future
	 * 
	 * @param gCalListName
	 * @param queryStartTime
	 *            the start time, null on no limit
	 * @param queryEndTime
	 *            end time; if null the system would take it as after 3 months'
	 *            time
	 * @return TaskList of this list null on list doesn't exist, or list is
	 *         empty
	 * @throws ServiceException
	 * @throws IOException
	 */
	public boolean syncTasksWithGCal(TaskList taskList, Date queryStartTime,
			Date queryEndTime) throws IOException, ServiceException {
		String gCalId = taskList.getGCalId();
		if(gCalId == null){
			return false;	// need to sync the list with the gCalFirst
		}
		
		// Query for a list of task to be updated
		CalendarQuery eventQuery = new CalendarQuery(new URL(String.format(
				privateUrlStrFormat, getPurCalendarIdFromUrl(gCalId))));
			
			// if range undefined, get the default date range
		if (queryStartTime != null) {
			eventQuery.setMinimumStartTime(new DateTime(queryStartTime));
		} else {
			GregorianCalendar dateInThreeMonth = new GregorianCalendar();
			dateInThreeMonth.add(GregorianCalendar.MONTH, -3);

			eventQuery.setMinimumStartTime(new DateTime(dateInThreeMonth
					.getTime()));
		}
		if (queryEndTime != null) {
			eventQuery.setMaximumStartTime(new DateTime(queryEndTime));
		} else {
			GregorianCalendar dateInThreeMonth = new GregorianCalendar();
			dateInThreeMonth.add(GregorianCalendar.MONTH, 3);
			eventQuery.setMaximumStartTime(new DateTime(dateInThreeMonth
					.getTime()));
		}
			// query on the event range
		CalendarEventFeed eventFeed = myService.query(eventQuery,
				CalendarEventFeed.class);
		
		// two task lists to be sync
		List<Task> localTasks = taskList.getTasks();
		List<CalendarEventEntry> serverEvents = eventFeed.getEntries();
		
		
		
			// create a hash map from the task id to local task //
		// build the list of event id->indexList
		// (use an index list here because there're recurring task )
		HashMap<String, ArrayList<Integer>> taskIdToLocalIndex = new HashMap<String, ArrayList<Integer>>();
		for (int i=0;i<localTasks.size();i++) {
			
			Task localTask = localTasks.get(i);
			
			// if the local task don't have a start date, do nothing about it yet
			// TODO: would implement the google task api here in the future
			if(localTask.getStartDateTime() == null){
				continue;
			}
			
			// when it's not sync on server, create that task on server
			if(localTask.getGCalId() == null){
				createTaskOnServer(taskList,localTask);
			}
			ArrayList<Integer> mappedIndexList = taskIdToLocalIndex.get(localTask.getGCalId());
			if(mappedIndexList == null){
				ArrayList<Integer> indexList = new ArrayList<Integer>();
				indexList.add(i);
				taskIdToLocalIndex.put(localTask.getGCalId(), indexList);
			} else{
				mappedIndexList.add(new Integer(i));
			}
		}
		
		// then loop through the server list to sync
		for(CalendarEventEntry serverEvent:serverEvents){
			// get the corresponding list of index on local
			ArrayList<Integer> indexList = taskIdToLocalIndex.get(getPurCalendarIdFromUrl(serverEvent.getId()));
			
			// index doesn't exist, means doesn't exist on local before
				// -- then create on local correspondingly
			if(indexList == null){
				createTaskOnLocal(taskList,serverEvent);
			}
			
			// the server edit time to compare with local one
			Date serverEditTime = new Date(serverEvent.getEdited().getValue());

			// all the events in this for loop have the same eventID
			// 		-- it's the google calendar's recurring event
			/* for each case:
			 *  if localEditTime after serverEditTime:
			 *  	update on server	--> create an exception (a new task, and remove that recur)
			 *  if localEditTime before serverEditTime:
			 * 		update everything on local	--> update all the events on local
			 */	
					// localEditTime after serverEditTime:
			List<RecurrenceException> recurExceptions = serverEvent.getRecurrenceException();
			
			for(Integer index:indexList){
				Task localTask = localTasks.get(index);
				Date localEditTime = localTask.getLastEditTime();
				
				// TODO: resolve the problem of recurring events here
				
				// and recurrence exception -- would unlink it from the set of 
				// recurrence task
				if(localEditTime.after(serverEditTime)){
					/*
					RecurrenceException recurException = new RecurrenceException();
					EntryLink entryLink = new EntryLink();
					entryLink.setEntry(new When());
					recurException.setEntryLink(entryLink);
					
					serverEvent.addRecurrenceException(null);
					updateTaskOnServer(localTask,serverEvent);
					*/
				}
			}
			
					// localEditTime before serverEditTime:
			Task firstLocalTask = localTasks.get(indexList.get(0));
			if(firstLocalTask.getLastEditTime().before(serverEditTime)){
				// get a list of server time
				List<When> serverEventTimeList = serverEvent.getTimes();
				
				// create a hash table of all the time, to take note if checked or not
				TreeMap<cs2103.t14j1.storage.When,BooleanWrapper> serverTimeChecked = 
						new TreeMap<cs2103.t14j1.storage.When,BooleanWrapper>();
				
				// put it to the tree map, based on the start time and end time
				// Note: can only use tree map here because the hash code of the
				// 	cs2103.t14j1.storage.When class is undecided -- meaning, if
				// 	use hash table we can only check if it's the same object 
				for(When serverEventTime: serverEventTimeList){
					cs2103.t14j1.storage.When serverLocalWhenToBeMapped = new cs2103.t14j1.storage.When();
					serverLocalWhenToBeMapped.setStartDateTime(new Date(serverEventTime.getStartTime().getValue()));
					serverLocalWhenToBeMapped.setEndDateTime(new Date(serverEventTime.getEndTime().getValue()));
					serverTimeChecked.put(serverLocalWhenToBeMapped,new BooleanWrapper(false));
				}
				
				// check if the local task exist on server
				for(int i=0;i<indexList.size();i++){
					Task localTask = localTasks.get(indexList.get(i));
					// check if that task exist on server task list
					BooleanWrapper checked = serverTimeChecked.get(localTask.getWhen());
					
					// when the task doesn't exist on server 
					//	-- deleted on server already, so delete on local accordingly
					if(checked == null){
						taskList.removeTask(localTask);
					} else{	// task exist; but possibly that the title has changed
						// because it's object, setting this reference to be true
						// can affect the value in the indexList (hopefully)
						checked.set(true);
						
						// update local task
						updateLocalTaskWithoutChangingTime(localTask,serverEvent);
					}
				}
				
				// in the end, check through all the tasks has been checked
				// the unchecked task are those not exist on local, so add 
				// them in
				Set<Entry<cs2103.t14j1.storage.When,BooleanWrapper>> pairs = serverTimeChecked.entrySet();
				for(Entry<cs2103.t14j1.storage.When,BooleanWrapper> pair:pairs){
					// the unchecked case, create task accordingly
					if(pair.getValue().get() == false){
						cs2103.t14j1.storage.When startEndTime = pair.getKey();
						createTaskOnLocal(taskList, serverEvent, startEndTime);
					}
				}
			}
		}
		return true;
		
/*
 *  On Sync: loop through the server task
 *   	-- if the task on local don't have a taskId, then create task on server
 *  	-- if local don't have task with that eventID, create on local (task created on server)
 * 		-- if the local task with eventID but doesn't exist on server
 * 			a) it's deleted on server
 * 				-- then delete it on local
 * 			b) this is a task moved from another list, but not yet updated
 * 				-- then check the sync status
 * 
 * 		-- then compare the lastUpdateTime: 
 * 			-- if the server version is newer, then update the local one; 
 * 			-- else if the local is newer, update the live one
 * 			-- else do nothing
 * 
 */
			

	}
	
	private void updateLocalTaskWithoutChangingTime(Task localTask,
			CalendarEventEntry serverEvent) {
		localTask.setName(serverEvent.getTitle().getPlainText());
		localTask.setLastEditTime(new Date(serverEvent.getEdited().getValue()));
	}


	private void updateTaskOnLocal(Task localTask,
			CalendarEventEntry serverEvent) {
		localTask.setName(serverEvent.getTitle().getPlainText());
	}


	private void updateTaskOnServer(Task localTask,
			CalendarEventEntry serverEvent) {
		// TODO
	}


	/** Based on the local event and local task list given, create a corresponding task on server
	 * Assumption: taskList is a list already exist on server -- i.e., it has the gCalId
	 * TODO: not done yet
	 * @param taskList
	 * @param localTask
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	private void createTaskOnServer(TaskList taskList, Task localTask) throws IOException, ServiceException {
		String gCalId = taskList.getGCalId();
		
		URL postUrl =
				  new URL(String.format(privateUrlStrFormat, gCalId));
		CalendarEventEntry myEntry = new CalendarEventEntry();
				
		// test creating a new task
		myEntry.setTitle(new PlainTextConstruct(localTask.getName()));
		// get default time zone
		DateTime lastEditTime = new DateTime(localTask.getLastEditTime());
		
		lastEditTime.setTzShift(Calendar.getInstance().getTimeZone().getOffset(0)/60/1000);
		myEntry.setEdited(lastEditTime);
		myEntry.setContent(new PlainTextConstruct(localTask.getGCalDescription()));
		
		/*DateTime startTime = new DateTime(localTask.getStartDateTime());
		DateTime endTime = new DateTime(localTask.getEndDateTime());//currentTime.getTime() + 3600*10);
		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEntry.addTime(eventTimes);*/
		
		// Send the request and receive the response:
		CalendarEventEntry insertedEntry = myService.insert(postUrl, myEntry);
		System.out.println("ID: " + insertedEntry.getId());
		
	}


	/** Based on the serverEvent given, create a corresponding task to the 
	 * 	taskList
	 * @param taskList
	 * @param serverEvent
	 */
	private void createTaskOnLocal(TaskList taskList,
			CalendarEventEntry serverEvent) {
		String taskName = serverEvent.getTitle().getPlainText();
		Date lastEditTime = new Date(serverEvent.getEdited().getValue());
		
		// get serverEvent time
		List<When> times = serverEvent.getTimes();
		
		Date currentTime = new Date();
		for(When time:times){
			Date startTime = new Date(time.getStartTime().getValue());
			Date endTime = new Date(time.getEndTime().getValue());
			// mark it as completed when endTime is after the start time
			boolean completed = endTime.before(currentTime);
			
			cs2103.t14j1.storage.Task task = new cs2103.t14j1.storage.Task(
					taskName, null, taskList.getName(), Priority.NORMAL, startTime,
					endTime, null, new Long(endTime.getSeconds()
							- startTime.getSeconds()), completed);
			task.setLastEditTime(lastEditTime);
			task.setGCalId(serverEvent.getId());
			
			//TODO: finish the cahnges
			//serverEvent.getR
			// taskList.addTask(task);
		}
		if(times.size() == 0 ){	// when it doesn't have time -- which seems impossible
			cs2103.t14j1.storage.Task task = new cs2103.t14j1.storage.Task(
					taskName, null, taskList.getName(), Priority.NORMAL, null, null,
					null, null, false);
			task.setLastEditTime(lastEditTime);
			task.setGCalId(serverEvent.getId());
			task.setList(taskList.getName());
			taskList.addTask(task);
		}
	}
	
	/**
	 * Create a new task, add it to the taskList, and return it
	 * @param taskList
	 * @param serverEvent
	 * @param startEndTime
	 * @return
	 */
	private Task createTaskOnLocal(TaskList taskList, CalendarEventEntry serverEvent, cs2103.t14j1.storage.When startEndTime){
		Task res = new Task();
		res.setList(taskList.getName());
		taskList.addTask(res);
		res.setWhen(startEndTime);
		res.setName(serverEvent.getTitle().getPlainText());
		res.setLastEditTime(new Date(serverEvent.getEdited().getValue()));
		
		return res;
	}

	/**
	 * @param list
	 * 	the task list to be created in the Google Calendar
	 * @param colour
	 * 	set null if not sure what to out here  
	 * @return
	 * 	false when the GCalId already exist in the tasklist
	 *  true on succeed
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	public boolean createNewCalendar(TaskList list) throws IOException, ServiceException{
		// when the list has an id already
		if(list.getGCalId() != null){
			return false;
		}
		
		String colour = list.getListColourStr();
		if(colour== null){
			colour = defaultColour;
		}
		
		// Create the calendar
		CalendarEntry calendar = new CalendarEntry();
		calendar.setTitle(new PlainTextConstruct(list.getName()));
		calendar.setSummary(new PlainTextConstruct("Calendar List created by TaskMetter"));
		// calendar.setTimeZone();	// not supported yet
		calendar.setHidden(HiddenProperty.FALSE);
		calendar.setColor(new ColorProperty(colour));
		// calendar.addLocation(); not supported yet
		CalendarEntry returnedCalendar = myService.insert(feedUrl, calendar);
		String gCalId = getPurCalendarIdFromUrl(returnedCalendar.getId());
		list.setGCalId(gCalId);
		//CalendarEntry returnedCalendar = myService.insert(postUrl, calendar);
		return true;
	}
	
	
	/** TODO: still in testing
	 * @param list
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public boolean updateCalendar(TaskList list) throws IOException, ServiceException{
		//CalendarFeed resultFeed = myService.getFeed(feedUrl, CalendarFeed.class);
		CalendarEntry calendar = new CalendarEntry();
		calendar.setTitle(new PlainTextConstruct("New title"));
		calendar.setColor(new ColorProperty("#A32929"));
		calendar.setSelected(SelectedProperty.TRUE);
		calendar.setId(list.getGCalId());
		CalendarEntry returnedCalendar = calendar.update();
		return false;
	}
	
	
	public boolean createNewEvent(TaskList list, Task task) throws IOException, ServiceException{
		if(task.getGCalId()!=null){
			return false;	// task already created
		}
		
		if(list.getGCalId() == null){
			createNewCalendar(list);
		}
		
		
		
		// Create the task into the task list
		
		return true;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create a CalenderService and authenticate
		CalendarService myService = new CalendarService("Task-Meter");
		String username = "songyangyu002@hotmail.com";
		String password = "flyfy1test";

		Scanner in = new Scanner(System.in);
//		password = in.nextLine();
		
		try {
			System.out.println("Start execution.");
			
			// call the constructor to get authentication TODO: remove my password before commit
			GCalSyn cal = new GCalSyn(username,password);
			
			System.out.println("Authentation succeeded.");
			
			
			// get the calendar list
			TaskList tasklists[] = cal.getCalLists();
			System.out.println("List of calendar: ");
			for(int i=0;i<tasklists.length;i++){
				System.out.println("List Name: " + tasklists[i].getName());
				System.out.println("	List Id: " + tasklists[i].getGCalId());
			}
			
			// test creating task list
			/*
			System.out.println("Creating task list");
			TaskList myList = new TaskList("Task Meter");
			String colour = "#2952A3";
			cal.createNewCalendar(myList,colour);
			System.out.println("Cal ID: " + myList.getGCalId());
			*/
			
			// test creating task
			TaskList myList = new TaskList("MetterTest");
			myList.setGCalId("6ks2ghk3fkbudqr71bjlcd9fs8@group.calendar.google.com");
			
			Task testTask = new Task();
			testTask.setName("Demo lalala ~~~");
			Date now = new Date();
			testTask.setStartDateTime(now);
			testTask.setEndDateTime(now);
			cal.createTaskOnServer(myList,testTask);
			
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.err.println("Error with URL.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
}


class BooleanWrapper{
	boolean val;
	
	BooleanWrapper(boolean newVal){
		val = newVal;
	}
	
	void set(boolean newval){
		val = newval;
	}
	
	boolean get(){
		return val;
	}
}