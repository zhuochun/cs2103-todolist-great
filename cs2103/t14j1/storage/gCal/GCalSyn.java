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
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.Task;

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
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * @author songyy This class is used for synchronizing with google calendar
 */
public class GCalSyn {
	private String username;
	private String passwd;
	CalendarService myService;

	TreeMap<String, String> getCalIdFromListName;
	
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
	}

	/**
	 * Assumption: no two list have the same name
	 * 
	 * @return An array of string indicating all the lists owned by this user
	 * @throws UnsupportedEncodingException
	 *             When program fail -- the logic would tell the user that very
	 *             sorry we cannot process your darling requirement
	 */
	public ArrayList<String> getCalList() throws UnsupportedEncodingException {
		ArrayList<String> res = new ArrayList<String>();
		this.getCalIdFromListName = new TreeMap<String, String>();
		for (CalendarEntry calList : this.list.getEntries()) {
			String title = calList.getTitle().getPlainText();
			
			String calendarId = getPurCalendarIdFromUrl(calList.getId());
			res.add(title);
			this.getCalIdFromListName.put(title, calendarId);
		}

		return res;
	}

	/**
	 * TODO: figure out how to get the place from CalendarEventEntry
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
	public TaskList getTaskList(String gCalListName, Date queryStartTime,
			Date queryEndTime) throws IOException, ServiceException {
		String gCalId = getCalIdFromListName.get(gCalListName);

		// when the list name given doesn't exist
		if (gCalId == null)
			return null;
		TaskList list = new TaskList(gCalListName);

		// get the events
		CalendarQuery eventQuery = new CalendarQuery(new URL(String.format(
				privateUrlStrFormat, gCalId)));

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

		CalendarEventFeed eventFeed = myService.query(eventQuery,
				CalendarEventFeed.class);

		Date currentTime = new Date();

		for (CalendarEventEntry event : eventFeed.getEntries()) {

			String title = event.getTitle().getPlainText();

			// get the date of the event
			List<When> times = event.getTimes();
			// deal with the recurring task/event --

			boolean haveTime = false;
			for (When time : times) {
				haveTime = true;
				Date startTime = new Date(time.getStartTime().getValue());
				Date endTime = new Date(time.getEndTime().getValue());

				// mark it as completed when endTime is after the start time
				boolean completed = endTime.before(currentTime);

				cs2103.t14j1.storage.Task task = new cs2103.t14j1.storage.Task(
						title, null, gCalListName, Priority.NORMAL, startTime,
						endTime, null, new Long(endTime.getSeconds()
								- startTime.getSeconds()), completed);
				task.setGCalProperty(GCalSyn.UPDATED);
				list.addTask(task);
			}

			// it's possible that some events don't have time -- then create
			// without time
			if (!haveTime) {
				cs2103.t14j1.storage.Task task = new cs2103.t14j1.storage.Task(
						title, null, gCalListName, Priority.NORMAL, null, null,
						null, null, false);
			}
		}

		return list;
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
	
	
	public boolean updateCalendar(TaskList list) throws IOException, ServiceException{
		CalendarFeed resultFeed = myService.getFeed(feedUrl, CalendarFeed.class);
		CalendarEntry calendar = resultFeed.getEntries().get(0);
		calendar.setTitle(new PlainTextConstruct("New title"));
		calendar.setColor(new ColorProperty("#A32929"));
		calendar.setSelected(SelectedProperty.TRUE);
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
		String username;
		String password;

		Scanner in = new Scanner(System.in);
		System.out.print("User Name: ");
//		username = in.nextLine();	TODO: comment out later
		System.out.print("Password: ");
//		password = in.nextLine();
		
		try {
			System.out.println("Start execution.");
			
			// call the constructor to get authentication TODO: remove my password before commit
			GCalSyn cal = new GCalSyn("flyfy1@gmail.com","heroircjfur");
			
			System.out.println("Authentation succeeded.");
			
			List<String> calList = cal.getCalList();
			
			// get the calendar list
			for(int i = 0;i<calList.size();i++){
				String listName = calList.get(i);
				System.out.println( (i+1) + ": " + listName);
			}
			
			System.out.println("Calendar List printed.");
			
			/*	Testing for getting tasks and creating task list
			// get an index from user
			int index = -1;
			boolean askForIndex = true;
			while(calList.size()>0 && askForIndex){
				System.out.print("Which one you want to see, please give me the index: 1 ~ "
						+ calList.size() + "");
				index = in.nextInt();
				if(index > calList.size() || index < 1){
					System.err.println("Sorry, index out of range.");
					continue;
				}
				askForIndex = false;
			}
			
			// get the event inside that list
			TaskList taskList = cal.getTaskList(calList.get(index-1), null, null);
			
			List tasks = taskList.getTasks();
			System.out.println("List of tasks got: ");
			
			Iterator<Task> taskIt = tasks.iterator();
			while(taskIt.hasNext()){
				Task task = taskIt.next();
				System.out.println(task.getDisplayTaskStr());
			}
			*/
			
			// test creating task list
			/*
			System.out.println("Creating task list");
			TaskList myList = new TaskList("Task Meter");
			String colour = "#2952A3";
			cal.createNewCalendar(myList,colour);
			System.out.println("Cal ID: " + myList.getGCalId());
			*/
			
			// test creating a new task
			TaskList myList = new TaskList("Task Meter");
			myList.setGCalId("2v5euscfdm6mv6mpbgli6a9egg@group.calendar.google.com");
			
			
			
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
