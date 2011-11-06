
package cs2103.t14j1.logic.smartbar;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.TaskLists;
import cs2103.t14j1.taskmeter.reminder.Reminder;

/**
 * @author SongYY
 *
 */
public class SmartParseTest {
	private ParseCommand sp;
	private DateTimeProcessor dt;
	private Calendar date;
	private int currentYear = Calendar.getInstance().get(Calendar.YEAR);
	int time;
	
	// method I'll be possibly using
	/* Test on time processor */
	private String getMessageInTestingDate() {
		return 
			"\nres	= " + dt.getDateTime().getDateWithTime() + 
			"\nwant	= " + date.getTime();
	}
	
	private String getMessageInTestingTime(){
		return
			"\nres	= " + dt.getDateTime().getTime() +
			"\nwant	= " + time;
	}
	
	private void setTime(int hour, int min, int sec) {
		time = hour * DateTime.SEC_PER_HOUR + 
				min*DateTime.SEC_PER_MINUTE +
				sec;
	}
	
	private List<Integer>getNumsFromSet(Set<Integer> set){
		return new LinkedList<Integer>(set);
	}

	private void setDateTimeToLastSec() {
		setDateTimeToTime(23, 59, 59);
	}

	private void dateAdd(int i) {
		this.date.add(Calendar.DATE, i);
		
	}

	private void newDate() {
		this.date = Calendar.getInstance();
	}
	
	private static boolean dateRoughlyEqual(Date a, Date b) {
		return (a.getTime() - b.getTime())<2000;
	}
	
	
	@Test
	public void addBasicTask(){
		Calendar currentTime = Calendar.getInstance();
		
		sp = new ParseCommand("Add rasing sun 3pm");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("rasing sun",sp.extractTaskName());
		
		sp = new ParseCommand("add sth this Friday 3am");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("sth",sp.extractTaskName());
		newDate();	date.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
		setDateTimeToTime(3, 0, 0);
		if(currentTime.get(Calendar.DAY_OF_WEEK) >= Calendar.FRIDAY)	date.add(Calendar.WEEK_OF_YEAR, 1);
		assertEquals(date.getTime(),sp.extractStartDate());
		assertEquals(time,(long)sp.extractStartTime());
		
		
		sp = new ParseCommand("Add basic task !3 3am for 2 hours, 3 minutes");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("basic task for 2 hours, 3 minutes",sp.extractTaskName());
		assertEquals(Priority.LOW,sp.extractPriority());
		newDate();setDateTimeToTime(3, 0, 0);
		if(currentTime.get(Calendar.AM_PM) == Calendar.PM)	dateAdd(1);
		assertEquals(date.getTime(),sp.extractStartDate());
		assertEquals(2 * DateTime.SEC_PER_HOUR + 3*DateTime.SEC_PER_MINUTE,(long)sp.extractDuration());
		date.add(Calendar.SECOND, 2 * DateTime.SEC_PER_HOUR + 3*DateTime.SEC_PER_MINUTE);
		assertEquals(date.getTime(),sp.extractEndDate());
		
		sp = new ParseCommand("Add basic task #home ! 1 4pm ~ 7pm by tomorrow @home");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("basic task ! 1",sp.extractTaskName());
		assertEquals("home", sp.extractListName());
		assertEquals(null,sp.extractPriority());
		newDate();setDateTimeToTime(16, 0, 0);
		assertEquals(date.getTime(),sp.extractStartDate());
		setDateTimeToTime(19, 0, 0);
		assertEquals(date.getTime(),sp.extractEndDate());
		assertEquals(DateTime.SEC_PER_HOUR * 3,(long)sp.extractDuration());
		newDate();DateTime.clearTimeFieldForDate(date);dateAdd(2);dateSecAdd(-1);
		assertEquals(date.getTime(), sp.extractDeadlineDate());
		assertEquals(null, sp.extractDeadlineTime());
		assertEquals("home",sp.extractPlace());
		
		
		sp = new ParseCommand("Add basic #(life is good) task @home 4am today");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("basic task",sp.extractTaskName());
		assertEquals("home",sp.extractPlace());
		assertEquals("life is good",sp.extractListName());
		newDate();setDateTimeToTime(4, 0, 0);
		if(currentTime.get(Calendar.AM_PM) == Calendar.PM)	dateAdd(1);
		assertEquals(date.getTime(),sp.extractStartDate());
		assertEquals(null,sp.extractEndDate());
		
		sp = new ParseCommand("Add basic task @(somewhere in the world)");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("basic task",sp.extractTaskName());
		assertEquals("somewhere in the world",sp.extractPlace());
		
		sp = new ParseCommand("Add basic task @(--1984--)");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("basic task",sp.extractTaskName());
		assertEquals("--1984--",sp.extractPlace());
	}
	
	@Test
	public void testTimePeriod(){
		sp = new ParseCommand("Add sth @(somewhere in the world) 4pm ~ 7pm by tomorrow");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("sth",sp.extractTaskName());
		newDate();setDateTimeToTime(16, 0, 0);
		assertEquals(date.getTime(),sp.extractStartDate());
		setDateTimeToTime(19, 0, 0);
		assertEquals(date.getTime(),sp.extractEndDate());
		assertEquals(DateTime.SEC_PER_HOUR * 3,(long)sp.extractDuration());
		newDate();DateTime.clearTimeFieldForDate(date);dateAdd(2);dateSecAdd(-1);
		assertEquals(date.getTime(), sp.extractDeadlineDate());
		assertEquals(null, sp.extractDeadlineTime());
		
		
		sp = new ParseCommand("Add sth 4:13:30 pm,12th,Dec ~ 7pm");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("sth",sp.extractTaskName());
		newDate();date.set(currentYear, Calendar.DECEMBER, 12);
		setDateTimeToTime(16, 13, 30);
		assertEquals(date.getTime(),sp.extractStartDate());
		int startTime = (int) (date.getTimeInMillis()/1000);
		setDateTimeToTime(19, 0, 0);
		int endTime = (int) (date.getTimeInMillis()/1000);
		assertEquals(date.getTime(),sp.extractEndDate());
		assertEquals(endTime - startTime,(long)sp.extractDuration());
		
		sp = new ParseCommand("Add sth Oct,13th ~ 12nd, Dec");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("sth",sp.extractTaskName());
		newDate();date.set(currentYear, Calendar.OCTOBER, 13);
		setDateTimeToTime(0, 0, 0);
		assertEquals(date.getTime(),sp.extractStartDate());
		startTime = (int) (date.getTimeInMillis()/1000);
		date.set(currentYear, Calendar.DECEMBER, 12);
		setDateTimeToTime(23, 59, 59);
		endTime = (int) (date.getTimeInMillis()/1000);
		assertEquals(date.getTime(),sp.extractEndDate());
		assertEquals(endTime - startTime,(long)sp.extractDuration());
		
		sp = new ParseCommand("add run 4:15pm Nov 12 ~ 2:00:12am, 20 Oct 2012");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("run",sp.extractTaskName());
		newDate();date.set(currentYear, Calendar.NOVEMBER, 12);
		setDateTimeToTime(16, 15, 0);
		assertEquals(date.getTime(),sp.extractStartDate());
		startTime = (int) (date.getTimeInMillis()/1000);
		date.set(2012, Calendar.OCTOBER, 20);
		setDateTimeToTime(2, 0, 12);
		endTime = (int) (date.getTimeInMillis()/1000);
		assertEquals(date.getTime(),sp.extractEndDate());
		assertEquals(endTime - startTime,(long)sp.extractDuration());
	}
	
	private void dateSecAdd(int i) {
		date.add(Calendar.SECOND, i);
	}

	private void setDateTimeToTime(int h,int m, int s) {
		setTime(h, m, s);
		DateTime.clearTimeFieldForDate(date);
		date.add(Calendar.SECOND, time);
	}

	@Test
	public void searchTask(){
		sp = new ParseCommand("/songyy today");
		assertEquals(Commands.SEARCH, sp.extractCommand());
		assertEquals("songyy", sp.extractTaskName());
		newDate();
		assertTrue(dateRoughlyEqual(date.getTime(),sp.extractSearchAfterDate()));
		setDateTimeToLastSec();
		assertTrue(dateRoughlyEqual(date.getTime(),sp.extractSearchBeforeDate()));
		
		sp = new ParseCommand("/songyy this Sept");
		newDate();date.set(currentYear, Calendar.SEPTEMBER, 1, 0, 0, 0);
		assertTrue(dateRoughlyEqual(date.getTime(),sp.extractSearchAfterDate()));
		newDate();date.set(currentYear, Calendar.OCTOBER, 1, 0, 0, 0);
		date.add(Calendar.SECOND, -1);
		assertTrue(dateRoughlyEqual(date.getTime(),sp.extractSearchBeforeDate()));
		
		sp = new ParseCommand("/songyy next day");
		newDate();dateAdd(1);setDateTimeToTime(0, 0, 0);
		assertTrue(dateRoughlyEqual(date.getTime(),sp.extractSearchAfterDate()));
		setDateTimeToTime(23, 59, 59);
		
		assertTrue(dateRoughlyEqual(date.getTime(),sp.extractSearchBeforeDate()));
		
		
		sp = new ParseCommand("/hero after tomorrow before 13th,Dec,2013 #(mumama) @gov !3");
		assertEquals(Commands.SEARCH, sp.extractCommand());
		assertEquals("hero", sp.extractTaskName());
		newDate();DateTime.clearTimeFieldForDate(date);dateAdd(1);
		assertEquals(date.getTime(), sp.extractSearchAfterDate());
		assertEquals("mumama", sp.extractListName());
		assertEquals("gov", sp.extractPlace());
		assertEquals(Priority.LOW,sp.extractPriority());
		date.set(2013, Calendar.DECEMBER, 13);
		setDateTimeToLastSec();
		assertEquals(date.getTime(), sp.extractSearchBeforeDate());
	}

	@Test
	public void deleteTask(){
		List<Integer> res;
		List<Integer> expected;
		
		sp = new ParseCommand("del 1-3,5,6,9");
		assertEquals(Commands.DELETE_TASK, sp.extractCommand());
		res = new ArrayList<Integer>();
		expected = new ArrayList<Integer>();
		expected.add(1);
		expected.add(2);
		expected.add(3);
		expected.add(5);
		expected.add(6);
		expected.add(9);
		
		res.addAll(sp.extractTaskNum());
		for(int i = 0;i < res.size();i++){
			assertEquals(res.get(i), expected.get(i));
		}
		
		sp = new ParseCommand("del 1-3,2~9 7 8 100~4");
		assertEquals(Commands.DELETE_TASK, sp.extractCommand());
		expected.clear();
		res.clear();
		
		expected.add(1);
		expected.add(2);
		expected.add(3);
		expected.add(4);
		expected.add(5);
		expected.add(6);
		expected.add(7);
		expected.add(8);
		expected.add(9);
		res.addAll(sp.extractTaskNum());
		for(int i = 0;i < res.size();i++){
			assertEquals(expected.get(i),res.get(i));
		}
		
	}
	
	@Test
	public void moveTask(){
		sp = new ParseCommand("mv 1 #(lualla)");
		assertEquals(Commands.MOVE_TASK, sp.extractCommand());
		assertEquals("lualla", sp.extractListName());
		assertEquals(1, sp.extractTaskNum().size());
		
		sp = new ParseCommand("move 12 #lulala");
		assertEquals(Commands.MOVE_TASK, sp.extractCommand());
		assertEquals(1, sp.extractTaskNum().size());
		assertEquals(12, (int)getNumsFromSet(sp.extractTaskNum()).get(0));
		
		sp = new ParseCommand("edit 1,2");	
		assertEquals(Commands.INVALID, sp.extractCommand());
	}

	@Test
	public void editTask(){
		sp = new ParseCommand("edit 1");
		assertEquals(Commands.EDIT_TASK, sp.extractCommand());
		
		sp = new ParseCommand("edit 1,2");
		assertEquals(Commands.INVALID, sp.extractCommand());
	}
	
	@Test
	public void markComplete(){
		sp = new ParseCommand("1-3,5 done");
		assertEquals(Commands.MARK_COMPLETE, sp.extractCommand());
		sp = new ParseCommand("done 1,1~2,5-6");
		assertEquals(Commands.MARK_COMPLETE, sp.extractCommand());
		List<Integer> res = getNumsFromSet(sp.extractTaskNum());
		List<Integer> want = new LinkedList<Integer>();
		want.add(1);want.add(2);want.add(5);want.add(6);
		for(int i=0;i<want.size();i++){
			assertEquals(want.get(i), res.get(i));
		}
	}
	
	@Test
	public void markPriority(){
		sp = new ParseCommand("1 2 3 4 5 !3");
		assertEquals(Commands.MARK_PRIORITY, sp.extractCommand());
		List<Integer> res = getNumsFromSet(sp.extractTaskNum());
		assertEquals(5, res.size());
		
		sp = new ParseCommand("1 !4");
		assertEquals(Commands.INVALID, sp.extractCommand());
	}
	
	@Test
	public void addReminder(){
		sp = new ParseCommand("remind 1 start");
		assertEquals(Commands.ADD_REMINDER, sp.extractCommand());
		assertEquals(Reminder.START, sp.getRemindParamter());
		sp = new ParseCommand("remind 2 end");
		assertEquals(Commands.ADD_REMINDER, sp.extractCommand());
		sp = new ParseCommand("remind 2 deadline");
		assertEquals(Commands.ADD_REMINDER, sp.extractCommand());
		sp = new ParseCommand("remind 12 13 14 tomorrow");
		assertEquals(Commands.ADD_REMINDER, sp.extractCommand());
		assertEquals(3, sp.extractTaskNum().size());
		sp = new ParseCommand("remind 1,2~5,3 tomrrow");
		assertEquals(Commands.INVALID, sp.extractCommand());
		sp = new ParseCommand("remind 1,2,3 in 2 minutes");
		assertEquals(Commands.ADD_REMINDER, sp.extractCommand());
		newDate();date.add(Calendar.MINUTE, 2);
		dateRoughlyEqual(date.getTime(), sp.getRemindTime());
	}
	
	@Test
	public void removeReminder(){
		sp = new ParseCommand("remind 1,2~5,3 cancel");
		assertEquals(Commands.REMOVE_REMINDER, sp.extractCommand());
		assertEquals(5, sp.extractTaskNum().size());
		sp = new ParseCommand("remind 1 can");
		assertEquals(Commands.INVALID, sp.extractCommand());
	}
	
	@Test
	public void addList(){
		sp = new ParseCommand("add #abc");
		assertEquals(Commands.ADD_LIST, sp.extractCommand());
		sp = new ParseCommand("add #(flyfy1 songyy)");
		assertEquals(Commands.ADD_LIST, sp.extractCommand());
	}
	
	@Test
	public void editList(){
		sp = new ParseCommand("edit #abc");
		assertEquals(Commands.EDIT_LIST, sp.extractCommand());
		sp = new ParseCommand("edit #(flyfy1 songyy)");
		assertEquals(Commands.EDIT_LIST, sp.extractCommand());
	}
	
	@Test
	public void renameList(){
		sp = new ParseCommand("rename #abc");
		assertEquals(Commands.INVALID, sp.extractCommand());
		sp = new ParseCommand("rename #(flyfy1 songyy) #(luala)");
		assertEquals(Commands.RENAME_LIST, sp.extractCommand());
		assertEquals("flyfy1 songyy", sp.extractListName());
		assertEquals("luala", sp.extractNewListName());
		sp = new ParseCommand("rename #(flyfy1 songyy) #");
		assertEquals(Commands.INVALID, sp.extractCommand());
	}
	
	@Test
	public void deleteList(){
		sp = new ParseCommand("delete #abc");
		assertEquals(Commands.DELETE_LIST, sp.extractCommand());
		sp = new ParseCommand("del #abc");
		assertEquals(Commands.DELETE_LIST, sp.extractCommand());
	}
	
	@Test
	public void switchList(){
		sp = new ParseCommand("#abc");
		assertEquals(Commands.SWITCH_LIST, sp.extractCommand());
		assertEquals("abc", sp.extractListName());
		sp = new ParseCommand("#(abc)");
		assertEquals(Commands.SWITCH_LIST, sp.extractCommand());
		assertEquals("abc", sp.extractListName());
		sp = new ParseCommand("#");	
		assertEquals(Commands.SWITCH_LIST, sp.extractCommand());
		assertEquals(TaskLists.INBOX, sp.extractListName());
	}
	
	@Test
	public void displayList(){
		sp = new ParseCommand("dis #abc");
		assertEquals(Commands.DISPLAY_LISTS, sp.extractCommand());
		sp = new ParseCommand("display");
		assertEquals(Commands.DISPLAY_LISTS, sp.extractCommand());
		assertEquals(TaskLists.INBOX, sp.extractListName());
		sp = new ParseCommand("dis #(abc)");	
		assertEquals(Commands.DISPLAY_LISTS, sp.extractCommand());
	}
	
	@Test
	public void displayTask(){
		sp = new ParseCommand("dis 1,2,3,5~100");
		assertEquals(Commands.DISPLAY_TASK, sp.extractCommand());
		assertEquals(99, sp.extractTaskNum().size());
	}
	
	@Test
	public void testOneStatementCommands(){
		sp = new ParseCommand("undo");
		assertEquals(Commands.UNDO, sp.extractCommand());
		sp = new ParseCommand("undoo");
		assertEquals(Commands.INVALID, sp.extractCommand());
		
		sp = new ParseCommand("exit");
		assertEquals(Commands.EXIT, sp.extractCommand());
		
		sp = new ParseCommand("redo");
		assertEquals(Commands.REDO, sp.extractCommand());
		
		sp = new ParseCommand("undo ");
		assertEquals(Commands.UNDO, sp.extractCommand());
		
		sp = new ParseCommand("exit	");
		assertEquals(Commands.EXIT, sp.extractCommand());
		
		sp = new ParseCommand("redo\n");
		assertEquals(Commands.REDO, sp.extractCommand());
		
		sp = new ParseCommand("redoo \n");
		assertEquals(Commands.INVALID, sp.extractCommand());
	}
	
	@Test
	public void testDateProcessor(){
		newDate(); DateTime.clearTimeFieldForDate(date);
		dt = new DateTimeProcessor("10:30:20 ,12/Oct/2011");
		setTime(10, 30, 20);date.set(2011, Calendar.OCTOBER, 12, 10, 30, 20);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
		assertTrue(getMessageInTestingDate(),dt.getDateTime().getDateWithTime().equals(date.getTime()));
		
		newDate();
		dt = new DateTimeProcessor("at 5 pm tomorrow");
		setTime(17, 0, 0);
		date.set(Calendar.HOUR_OF_DAY, 17);	date.add(Calendar.DATE, 1);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
		assertTrue(getMessageInTestingDate(),dt.getDateTime().getDateWithTime().equals(date.getTime()));
		
		newDate();
		dt = new DateTimeProcessor("at 1am 12th,Dec,2013");
		setTime(1, 0, 0);date.set(2013, Calendar.DECEMBER, 12,1,0,0);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
		assertTrue(getMessageInTestingDate(),dt.getDateTime().getDateWithTime().equals(date.getTime()));
		
		newDate();DateTime.clearTimeFieldForDate(date);
		dt = new DateTimeProcessor("1:30 pm Dec,12th,2020");
		setTime(13,30,0);date.set(2020, Calendar.DECEMBER, 12, 13, 30, 0);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
		assertTrue(getMessageInTestingDate(),dt.getDateTime().getDateWithTime().equals(date.getTime()));
		
		newDate();DateTime.clearTimeFieldForDate(date);
		dt = new DateTimeProcessor("1:30am today");
		setTime(1, 30, 0); date.set(Calendar.HOUR_OF_DAY, 1);date.set(Calendar.MINUTE, 30);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
		assertTrue(getMessageInTestingDate(),dt.getDateTime().getDateWithTime().equals(date.getTime()));
		
		newDate(); DateTime.clearTimeFieldForDate(date);
		dt = new DateTimeProcessor("10:30:20pm next Wednesday");
		setTime(22, 30, 20);date.add(Calendar.WEEK_OF_YEAR, 1);
		date.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
	}
}
